package com.fave100.server.util;

import java.util.Comparator;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class FixedSizePriorityQueue<E> extends TreeSet<E> {

    private int elementsLeft;

    public FixedSizePriorityQueue(final int maxSize, final Comparator<E> comparator) {
        super(comparator);
        this.elementsLeft = maxSize;
    }


    /**
     * @return true if element was added, false otherwise
     * */
    @Override
    public boolean add(final E e) {
        if (elementsLeft == 0 && size() == 0) {
            // max size was initiated to zero => just return false
            return false;
        } else if (elementsLeft > 0) {
            // queue isn't full => add element and descrement elementsLeft
            super.add(e);
            elementsLeft--;
            return true;
        } else {
            // there is already 1 or more elements => compare to the least
            final int compared = super.comparator().compare(e, this.first());
            if (compared == 1) {
                // new element is larger than the least in queue => pull the least and add new one to queue
                pollFirst();
                super.add(e);
                return true;
            } else {
                // new element is less than the least in queue => return false
                return false;
            }
        }
    }
}
