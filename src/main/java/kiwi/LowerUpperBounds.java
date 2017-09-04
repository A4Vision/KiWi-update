package kiwi;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by bugabuga
 * n 02/09/17.
 */

public class LowerUpperBounds{
    private LongAdder lowerBound;
    private LongAdder upperBound;
    public final boolean isFake;

    public LowerUpperBounds(boolean isFake){
        this.isFake = isFake;
        if(!isFake){
            lowerBound = new LongAdder();
            upperBound = new LongAdder();
        }
    }

    public Integer getUpperBound(){
        if(isFake){
            return Integer.MAX_VALUE;
        }else{
            return upperBound.intValue();
        }
    }

    public Integer getLowerBound(){
        if(isFake){
            return 0;
        }else{
            return lowerBound.intValue();
        }
    }

    public void startInsert(boolean dataIsNull){
        if(dataIsNull){
            maybeAboutToRemove();
        }else{
            maybeAboutToAdd();
        }
    }

    public void finishInsert(boolean newDataIsNull, boolean oldDataIsNull) {
        // Could be simplified to the less verbose pseudo-code:
//        if(oldDataIsNull){
//            lowerBound.increment();
//        }else{
//            upperBound.decrement();
//        }
        if(newDataIsNull){
            if(oldDataIsNull){
                undoAboutToRemove();
            }else{
                certainlyRemoved();
            }
        }else{
            if(oldDataIsNull){
                certainlyAdded();
            }else{
                undoAboutToAdd();
            }
        }
    }

    private void maybeAboutToAdd(){
        if(isFake) return;
        upperBound.increment();
    }

    private void maybeAboutToRemove(){
        if(isFake) return;
        lowerBound.decrement();
    }

    private void undoAboutToRemove(){
        if(isFake) return;
        lowerBound.increment();
    }

    private void undoAboutToAdd(){
        if(isFake) return;
        upperBound.decrement();
    }

    private void certainlyAdded(){
        if(isFake) return;
        lowerBound.increment();
    }

    private void certainlyRemoved(){
        if(isFake) return;
        upperBound.decrement();
    }

    public void undoPut(boolean dataIsNull) {
        if(dataIsNull){
            undoAboutToRemove();
        }else{
            undoAboutToAdd();
        }
    }
}
