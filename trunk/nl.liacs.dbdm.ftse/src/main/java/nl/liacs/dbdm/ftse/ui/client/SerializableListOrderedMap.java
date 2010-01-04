/*
 * Created: Oct 14, 2008 | 2:51:52 PM
 * 
 * Avan Software Technology Advisors Co., http://asta.ir
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Behrooz Nobakht [behrooz at ce dot sharif dot edu]
 * @author Sadegh Ali Akbari
 */
public class SerializableListOrderedMap<K extends Serializable, V extends Serializable> implements Map<K, V>,
		Serializable {

	private static final long serialVersionUID = 1L;

	protected List<K> keys;
	protected Map<K, V> delegate;

	public SerializableListOrderedMap() {
		keys = new ArrayList<K>();
		delegate = new HashMap<K, V>();
	}

	public void clear() {
		keys.clear();
		delegate.clear();
	}

	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Set<K> keySet = keySet();
		List<Map.Entry<K, V>> entryList = new ArrayList<Entry<K, V>>();
		for (K key : keySet) {
			SimpleEntry<K, V> entry = new SimpleEntry<K, V>(key, get(key));
			entryList.add(entry);
		}
		return new OrderedSet<Entry<K, V>>(entryList);
	}

	public V get(Object key) {
		return delegate.get(key);
	}

	public boolean isEmpty() {
		return keys.isEmpty();
	}

	public Set<K> keySet() {
		return new OrderedSet<K>(this.keys);
	}

	public V put(K key, V value) {
		if (keys.contains(key)) {
			return null;
		}
		keys.add(key);
		return delegate.put(key, value);
	}

	public V put(int index, K key, V value) {
		if (keys.contains(key)) {
			return null;
		}
		keys.add(index, key);
		return delegate.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
			this.put(e.getKey(), e.getValue());
		}
	}

	public V remove(Object key) {
		keys.remove(key);
		return delegate.remove(key);
	}

	public int size() {
		return keys.size();
	}

	public Collection<V> values() {
		return delegate.values();
	}

	public class OrderedSet<E> implements Set<E>, Serializable {

		private static final long serialVersionUID = 1L;
		private List<E> values;

		public OrderedSet() {
			values = new ArrayList<E>();
		}

		public OrderedSet(List<E> values) {
			this.values = values;
		}

		public boolean add(E e) {
			if (values.contains(e)) {
				return false;
			}
			return values.add(e);
		}

		public boolean addAll(Collection<? extends E> c) {
			boolean result = true;
			for (E e : c) {
				result = result && this.add(e);
			}
			return result;
		}

		public void clear() {
			values.clear();
		}

		public boolean contains(Object o) {
			return values.contains(o);
		}

		public boolean containsAll(Collection<?> c) {
			return values.containsAll(c);
		}

		public boolean isEmpty() {
			return values.isEmpty();
		}

		public Iterator<E> iterator() {
			return values.iterator();
		}

		public boolean remove(Object o) {
			return values.remove(o);
		}

		public boolean removeAll(Collection<?> c) {
			return values.removeAll(c);
		}

		public boolean retainAll(Collection<?> c) {
			return values.retainAll(c);
		}

		public int size() {
			return values.size();
		}

		public Object[] toArray() {
			return values.toArray();
		}

		public <T> T[] toArray(T[] a) {
			return values.toArray(a);
		}

	}

}
