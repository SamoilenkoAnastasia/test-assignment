/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

import ua.kpi.comsys.test2.NumberList;

/**
     * @author Anastasia Samoilenko
     * @group IA-33
     * @variant 19
     */

public class NumberListImpl implements NumberList {
    
   private static final int MAIN_BASE = 16;
   private static final int ADDITIONAL_BASE = 2;
   private int base = MAIN_BASE;
    
   private static class Node {
        Byte value;
        Node next;

        Node(Byte value) {
            this.value = value;
        }
    } 
    
    private Node head;
    private int size;
    
    public NumberListImpl() {
        head = null;
        size = 0;
    }
    
    public NumberListImpl(File file) {
       this();
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            line = br.readLine();
            if (line == null) {
                return; 
            }
            
            BigInteger number = new BigInteger(line.trim());
            
            if (number.compareTo(BigInteger.ZERO) < 0) {
                 return; 
            }
            
            fromDecimalBigInteger(number, MAIN_BASE);
        } catch (IOException e) {
            return;
        } catch (NumberFormatException e) {
            return;
        }   
    }

    public NumberListImpl(String value) {
       this();
        try {
            BigInteger number = new BigInteger(value);
            if (number.compareTo(BigInteger.ZERO) < 0) {
                return; 
            }
            fromDecimalBigInteger(number, MAIN_BASE);
        } catch (NumberFormatException e) {
             return; 
        }
    }


    private void fromDecimalBigInteger(BigInteger value, int base) {
        clear();
        this.base = base;
        BigInteger baseBI = BigInteger.valueOf(base);

        if (value.equals(BigInteger.ZERO)) {
            add((byte) 0);
            return;
        }

        BigInteger zero = BigInteger.ZERO;
        while (value.compareTo(zero) > 0) {
            BigInteger remainder = value.remainder(baseBI);
            value = value.divide(baseBI);

            add(0, remainder.byteValue()); 
    }
}

    private BigInteger toDecimalBigInteger() {
    
    if (head == null) {
        return BigInteger.ZERO;
    }

    BigInteger result = BigInteger.ZERO;
    BigInteger baseBI = BigInteger.valueOf(this.base); 
    Node current = head; 

    for (int i = 0; i < size; i++) {
        BigInteger digit = BigInteger.valueOf(current.value);
        
        result = result.multiply(baseBI);
        
        result = result.add(digit);

        current = current.next;
    }
    
    return result;
}
    
    public void saveList(File file) {
       try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRecordBookNumber() {
        return 19;
    }

    public NumberListImpl changeScale() {
        BigInteger decimal = toDecimalBigInteger();
        int targetBase = (this.base == MAIN_BASE) ? ADDITIONAL_BASE : MAIN_BASE;
        NumberListImpl result = new NumberListImpl();
        result.fromDecimalBigInteger(decimal, targetBase); 
        return result;
    }



    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger a = this.toDecimalBigInteger();
        BigInteger b = ((NumberListImpl) arg).toDecimalBigInteger();
        BigInteger r = a.and(b);

        NumberListImpl result = new NumberListImpl();
        result.fromDecimalBigInteger(r, MAIN_BASE);
        return result;
    }

    public String toDecimalString() {
        return toDecimalBigInteger().toString();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node curr = head;
        for (int i = 0; i < size; i++) {
            sb.append(Integer.toHexString(curr.value).toUpperCase());
            curr = curr.next;
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List)) return false;

        List<?> other = (List<?>) o;
        if (other.size() != size) return false;

        Iterator<?> it = other.iterator();
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(it.next())) return false;
        }
        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
         return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Byte next() {
                return get(i++);
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        return arr;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean add(Byte e) {
        if (e == null || e < 0 || e >= this.base)
            throw new IllegalArgumentException("Invalid digit");

        Node node = new Node(e);
        if (head == null) {
            head = node;
            node.next = head;
        } else {
            Node last = getNode(size - 1);
            last.next = node;
            node.next = head;
        }
        size++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
        if (!contains(element)) {
            return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        return addAll(size, c);
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (c == null || c.isEmpty()) {
            return false;
        }
        for (Byte element : c) {
            if (element == null || element < 0 || element >= this.base) {
                throw new IllegalArgumentException("Invalid digit for base " + this.base);
            }
        }
        boolean modified = false;
        int currentIndex = index;
        for (Byte element : c) {
            add(currentIndex++, element); 
            modified = true;
        }
        return modified;
    }



    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            Byte element = it.next();
            if (c.contains(element)) {
                int index = indexOf(element); 
                if (index >= 0) {
                    remove(index);
                    modified = true;
                    return removeAllByIndexing(c); 
                }
            }
        }
        return modified; 
    }
    
    private boolean removeAllByIndexing(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; ) {
            if (c.contains(get(i))) {
                remove(i);
                modified = true;
            } else {
                i++;
            }
        }
        return modified;
    }



    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size; ) {
            if (!c.contains(get(i))) {
                remove(i);
                modified = true;
               
            } else {
                i++;
            }
        }
        return modified;
    }


    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
       return getNode(index).value;
    }
    
    private Node getNode(int index) {
           if (index < 0 || index >= size) 
               throw new IndexOutOfBoundsException();
           
           Node curr = head;
           for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr;
       }


    @Override
    public Byte set(int index, Byte element) {
        if (element == null || element < 0 || element >= this.base)
            throw new IllegalArgumentException();

        Node node = getNode(index);
        Byte old = node.value;
        node.value = element;
        return old;
    }


    @Override
    public void add(int index, Byte element) {
        if (element == null || element < 0 || element >= this.base)
            throw new IllegalArgumentException();

        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();

        Node node = new Node(element);

        if (index == 0) {
            if (head == null) {
                head = node;
                node.next = head;
            } else {
                Node last = getNode(size - 1);
                node.next = head;
                head = node;
                last.next = head;
            }
        } else {
            Node prev = getNode(index - 1);
            node.next = prev.next;
            prev.next = node;
        }
        size++;
    }


    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        Byte removed;
        if (index == 0) {
            removed = head.value;
            if (size == 1) {
                head = null;
            } else {
                Node last = getNode(size - 1);
                head = head.next;
                last.next = head;
            }
        } else {
            Node prev = getNode(index - 1);
            removed = prev.next.value;
            prev.next = prev.next.next;
        }
        size--;
        return removed;   
    }


    @Override
    public int indexOf(Object o) {
        Node curr = head;
        for (int i = 0; i < size; i++) {
            if (curr.value.equals(o)) return i;
            curr = curr.next;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }


    @Override
    public ListIterator<Byte> listIterator() {
        throw new UnsupportedOperationException();
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        throw new UnsupportedOperationException();
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
         throw new IndexOutOfBoundsException();
     }
     
     NumberListImpl sublist = new NumberListImpl();
     for (int i = fromIndex; i < toIndex; i++) {
         sublist.add(get(i));
     }
     return sublist;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index2 < 0 || index1 >= size || index2 >= size)
            return false;

        Node a = getNode(index1);
        Node b = getNode(index2);

        Byte tmp = a.value;
        a.value = b.value;
        b.value = tmp;
        return true;
    }


    @Override
    public void sortAscending() {
       for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)
                if (get(i) > get(j))
                    swap(i, j);
    }


    @Override
    public void sortDescending() {
        for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)
                if (get(i) < get(j))
                    swap(i, j);
    }


    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
        }
    }


    @Override
    public void shiftRight() {
        if (size > 1) {
        Node prevLast = getNode(size - 2);
        Node last = prevLast.next;
        prevLast.next = head;
        head = last;
        }
    }
}
