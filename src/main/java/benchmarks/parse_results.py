import os
import re
import sys
import matplotlib.pyplot as plt
import numpy as np
import collections
OPERATIONS = "OPERATIONS"
SCANS = "SCANS"


def is_parameters_line(line):
    return line.startswith("itemsRange=")


def scans_throughput(line):
    as_string = remove_line_prefix("scans throughput", line)
    if as_string:
        return eval(as_string)


def operations_throughput(line):
    as_string = remove_line_prefix("operations throughput", line)
    if as_string:
        return eval(as_string)


def remove_line_prefix(prefix, line):
    if not line.startswith(prefix):
        return None
    return line[len(prefix):]


Throughputs = collections.namedtuple("Throughputs", ("scans", "operations"))
parameters_names =('itemsRange', 'threadsAmount', 'preFillAmount', 'mapNumber',
                                                    'experimentIndex', 'experimentsCount', 'testDurationSeconds',
                                                    'warmUpSeconds')
Parameters = collections.namedtuple("Parameters", parameters_names)
PARAMTER_PATTERN = "([^ ]+)=([^ ]+)"


def parse_parameters_line(line):
    d = dict([(x, eval(y)) for x, y in re.compile(PARAMTER_PATTERN).findall(line)])
    return Parameters(**d)


def read_results_string(string):
    res = {}
    operations = None
    scans = None
    current_parameters = None
    for line in string.splitlines():
        if is_parameters_line(line):
            current_parameters = parse_parameters_line(line)
            operations = None
            scans = None
        elif operations is None:
            operations = operations_throughput(line)
            if operations is not None:
                res[current_parameters] = Throughputs(scans, operations)
                print_suspicious_measures(operations, current_parameters)
        elif scans is None:
            scans = scans_throughput(line)
            if scans is not None:
                res[current_parameters] = Throughputs(scans, operations)
                print_suspicious_measures(scans, current_parameters)
    return res


def print_suspicious_measures(measures, parameters_line):
    measures = clean_data(measures)
    error = np.std(measures) / np.average(measures)
    if error > 0.1:
        print "suspicious:", measures
        print "Please envoke again:"
        print parameters_line


def clean_data(measures):
    avg = np.average(measures)
    i = worst_data_point = np.argmax(np.abs(np.array(measures) - avg))
    return measures[:i] + measures[i + 1:]


def clean_average(measures):
    return np.average(clean_data(measures))


def get_data_lists(parameters_to_throughputs, experiment_index, map_number):
    operations = []
    scans = []
    for p, t in parameters_to_throughputs.iteritems():
        if p.experimentIndex == experiment_index and p.mapNumber == map_number:
            if t.operations:
                operations.append((p.threadsAmount, clean_average(t.operations)))
            if t.scans:
                scans.append((p.threadsAmount, clean_average(t.scans)))
    operations.sort(key=lambda (threads_amount, y): threads_amount)
    scans.sort(key=lambda (threads_amount, y): threads_amount)

    return operations, scans


def map_name(map_number):
    return {0: "KiWi", 1: "K-ary Tree", 2: "SkipList", 3: "ConcurrentHashMap"}[map_number]


def map_color_marker(map_number):
    return [('black', 's'), ('orange', 'D'), ('blue', 'o'), ('green', '+')][map_number]


def title(experiment_index):
    experiment_title = ["Get only", "Put only", "Scan only", "Parallel put&scan"][experiment_index]
    return "Experiment {}\n{}".format(experiment_index + 1, experiment_title)


def create_graph(parameters_to_throughputs, experiment_index, operations_or_scans):
    maps_amount = max([p.mapNumber for p in parameters_to_throughputs]) + 1
    last_x = None
    for map_number in xrange(maps_amount + 1):
        operations, scans = get_data_lists(parameters_to_throughputs, experiment_index, map_number)
        x, y = [], []
        if operations_or_scans == SCANS and scans:
            x, y = zip(*scans)
        elif operations_or_scans == OPERATIONS and operations:
            x, y = zip(*operations)
        if x:
            color, marker = map_color_marker(map_number)
            plt.plot(x, y, color=color, marker=marker, label=map_name(map_number))
            last_x = x
    x1, x2 = plt.xlim()
    plt.xlim(x1 - 1, x2 + 2)
    y1, y2 = plt.ylim()
    print y1, y2
    plt.ylim(y1 - 1, y2 + 4)
    plt.xticks(last_x)
    plt.legend(loc='upper left')
    if operations_or_scans == OPERATIONS:
        word = "operations"
    else:
        word = "scanned elements"
    plt.ylabel("Million of {} per second".format(word))
    plt.xlabel("Number of threads")
    plt.title(title(experiment_index))
    plt.grid()


def generate_graphs(outdir):
    for experiment_index, data_type in [(0, OPERATIONS), (1, OPERATIONS), (2, SCANS), (3, OPERATIONS), (3, SCANS)]:
        plt.cla()
        all_data = {}
        for fname in sys.argv[1:]:
            with open(fname, "rb") as f:
                data = read_results_string(f.read())
                all_data.update(data)
        create_graph(all_data, experiment_index, data_type)
        plt.savefig(os.path.join(outdir, "experiment{}_{}.png".format(experiment_index, data_type)))


if __name__ == '__main__':
    print "Parse the running results of benchmark.Main and " \
          "generates graphs results graphs (in current directory)"
    print "Usage: {} <fname1> <fname2> ..."
    generate_graphs('.')
