package owlSpeak.engine.his;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class SortedMap<K extends Comparable<? super K>, V extends Comparable<? super V>>
		implements Map<K, V> {

	private TreeSet<Entry<K, V>> entries;

	public SortedMap() {
		entries = new TreeSet<Entry<K, V>>(new Comparator<Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> arg0, Entry<K, V> arg1) {
				int cmp = arg0.getValue().compareTo(arg1.getValue());
				if (cmp == 0)
					cmp = arg0.getKey().compareTo(arg1.getKey());
				return cmp;
			}

		});
	}

	@Override
	public void clear() {
		entries.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		for (Entry<K, V> e : entries) {
			if (e.getKey().equals(arg0))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object arg0) {
		for (Entry<K, V> e : entries) {
			if (e.getValue().equals(arg0))
				return true;
		}
		return false;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return entries;
	}

	@Override
	public V get(Object arg0) {
		for (Entry<K, V> e : entries) {
			if (e.getKey().equals(arg0))
				return e.getValue();
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/**
	 * This implementation has not backed up the collection in the map object.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Set<K> keySet() {
		if (isEmpty())
			return null;
		TreeSet<K> keys = new TreeSet<K>();
		for (Entry<K, V> e : entries) {
			keys.add(e.getKey());
		}
		return keys;
	}

	@Override
	public V put(K arg0, V arg1) {
		V returnVal = null;
		for (Entry<K, V> e : entries) {
			if (e.getKey().equals(arg0)) {
				returnVal = e.getValue();
				e.setValue(arg1);
				return returnVal;
			}
		}
		entries.add(new SimpleEntry<K, V>(arg0, arg1));
		return returnVal;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		for (java.util.Map.Entry<? extends K, ? extends V> e : arg0.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public V remove(Object arg0) {
		V returnVal = null;
		for (Entry<K, V> e : entries) {
			if (e.getKey().equals(arg0)) {
				returnVal = e.getValue();
				entries.remove(e);
				break;
			}
		}
		return returnVal;
	}

	@Override
	public int size() {
		return entries.size();
	}

	/**
	 * This implementation has not backed up the collection in the map object.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Collection<V> values() {
		if (isEmpty())
			return null;
		Collection<V> values = new Vector<V>();
		for (Entry<K, V> e : entries) {
			values.add(e.getValue());
		}
		return values;
	}

	@Override
	public String toString() {
		Iterator<Entry<K, V>> i = entrySet().iterator();
		if (!i.hasNext())
			return "{}";
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext())
				return sb.append('}').toString();
			sb.append(", ");
		}
	}

	public Entry<K, V> pollFirstEntry() {
		return entries.pollFirst();
	}
	
	public Entry<K, V> pollLastEntry() {
		return entries.pollLast();
	}
}
