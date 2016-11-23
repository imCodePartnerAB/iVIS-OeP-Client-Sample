package se.unlogic.standardutils.collections;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ReverseListIterator<T> implements Iterable<T> {

    private final ListIterator<? extends T> listIterator;        

    public ReverseListIterator(List<? extends T> list) {
        this.listIterator = list.listIterator(list.size());            
    }               

    public Iterator<T> iterator() {
       
    	return new Iterator<T>() {

            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            public T next() {
                return listIterator.previous();
            }

            public void remove() {
                listIterator.remove();
            }
        };
    }
}
