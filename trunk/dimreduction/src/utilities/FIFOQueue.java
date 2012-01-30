/***************************************************************************/
/*** Interactive Graphic Environment for Dimensionality Reduction        ***/
/***                                                                     ***/
/*** Copyright (C) 2006  David Corrêa Martins Junior                     ***/
/***                     Fabrício Martins Lopes                          ***/
/***                     Roberto Marcondes Cesar Junior                  ***/
/***                                                                     ***/
/*** This library is free software; you can redistribute it and/or       ***/
/*** modify it under the terms of the GNU Lesser General Public          ***/
/*** License as published by the Free Software Foundation; either        ***/
/*** version 2.1 of the License, or (at your option) any later version.  ***/
/***                                                                     ***/
/*** This library is distributed in the hope that it will be useful,     ***/
/*** but WITHOUT ANY WARRANTY; without even the implied warranty of      ***/
/*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU   ***/
/*** Lesser General Public License for more details.                     ***/
/***                                                                     ***/
/*** You should have received a copy of the GNU Lesser General Public    ***/
/*** License along with this library; if not, write to the Free Software ***/
/*** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA       ***/
/*** 02110-1301  USA                                                     ***/
/***                                                                     ***/
/*** Contact: David Corrêa Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabrício Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/

/***************************************************************************/
/*** This class implements a first in first out queue                    ***/
/***************************************************************************/

package utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FIFOQueue implements Collection, Queue {
    private static final int DEFAULT_SIZE = 10;
   
    private Object[] m_values;
    private int m_in;
    private int m_out;
    private boolean m_full;
    private boolean m_empty;
   
    public FIFOQueue() {
	this(DEFAULT_SIZE);  
	m_full = false;
    }
   
    public FIFOQueue(int size) {
	m_values = new Object[size];
	m_empty = true;
    }
   
    @Override
    public void enq(Object element) {
	if (m_full) throw new IllegalStateException("Queue full."); 
	m_empty = false;
     
	m_values[m_in++] = element;
	if (m_in == m_values.length) m_in = 0;
	m_full = m_in == m_out;
    }
   
    @Override
    public Object deq() {
	if (m_empty) throw new NoSuchElementException("Heap empty.");
	m_full = false;
     
	Object o = m_values[m_out];
	m_values[m_out++] = null;
     
	if (m_out == m_values.length) m_out= 0;
	m_empty = m_in == m_out;
     
	return(o);
    }

    @Override
    public int size() {
	if (m_empty) return(0);
	if (m_full) return(m_values.length);
    
	int size = 0;
	for (int i = m_out; i < m_values.length;i++, size++) {
	    if (i == m_in) return(size);      
	}
    
	for (int i = 0; i < m_in; i++, size++);
    
	return(size);
    }

    @Override
    public void clear() {
	m_in = 0;
	m_out = 0;
	m_full = false;
	m_empty = true;
    }

  
    @Override
    public boolean isEmpty() {
	return(m_empty);
    }
  
    public boolean isFull() {
	return(m_full);  
    }

    @Override
    public Object[] toArray() {
	return(m_values);  
    }

    @Override
    public boolean add(Object o) {
	enq(o);
	return(true);
    }

    @Override
    public boolean contains(Object o) {
	for (int i = 0; i < m_values.length; i++) {
	    if (m_values[i] != null && m_values[i].equals(o)) return(true);  
	}
	return(false);
    }

    @Override
    public boolean remove(Object o) {
	throw new UnsupportedOperationException("remove(Object)");
    }

    @Override
    public boolean addAll(Collection c) {
	for (Iterator itr = c.iterator(); itr.hasNext();) {
	    enq(itr.next());  
	}
	return(true);
    }

    @Override
    public boolean containsAll(Collection c) {
	for (Iterator itr = c.iterator(); itr.hasNext();) {
	    if (!contains(itr.next())) return(false);
	}
	return(true);
    }

    @Override
    public boolean removeAll(Collection c) {
	throw new UnsupportedOperationException("removeAll(Collection)");
    }

    @Override
    public boolean retainAll(Collection c) {
	throw new UnsupportedOperationException("retainAll(Collection)");
    }

    @Override
    public Iterator iterator() {
	return(new QueueIterator());
    }

    @Override
    public Object[] toArray(Object[] a) {
	int size = size();
	if (a.length < size) {
	    a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
	}
    
	int j = 0;
	for (int i = m_out; i < m_values.length; i++, j++) {
	    if (i == m_in) {
		if (j < a.length) a[j] = null;
		return(a);
	    }
	    a[j] = m_values[i];
	}
    
	for (int i = 0; i < m_out; i++, j++) {
	    a[j] = m_values[i];  
	}
    
	if (j < a.length) a[j] = null;
    
	return(a);
    }
  
    public class QueueIterator implements Iterator {
	int m_index = m_out;
    
	private QueueIterator() {}
    
        @Override
	public void remove() {
	    throw new UnsupportedOperationException("remove()");  
	}

        @Override
	public boolean hasNext() {
	    return(m_index != m_in);
	}

        @Override
	public Object next() {
	    Object o = m_values[m_index++];
	    if (m_index == m_values.length) m_index = 0;
	    return(o);
	}
    }
  
// *** ver para deletar.    
    //package level visibility data methods for testing
    int in() { return(m_in); }
  
    int out() { return(m_out); }
  
    Object[] values() { return(m_values); }
  
}
