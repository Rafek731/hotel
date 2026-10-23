package hotel.util.map;

import java.util.ArrayList;

public class MyMap<K, V> implements Map<K, V> {
    private final ArrayList<K> keys = new ArrayList<>();
    private final ArrayList<V> values = new ArrayList<>();

    public boolean put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Given key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Given value is null");
        }

        int i = keys.indexOf(key);

        if (i == -1) {
            keys.add(key);
            values.add(value);
        } else {
            values.set(i, value);
        }

        return true;
    }

    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Given key is null");
        }

        int i = keys.indexOf(key);
        if (i == -1) {
            return null;
        }
        return values.get(i);
    }

    public ArrayList<K> keys() {
        return new ArrayList<>(keys);
    }

    public boolean remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Given key is null");
        }

        int i = keys.indexOf(key);
        if (i == -1) {
            return false;
        }

        values.remove(i);
        keys.remove(i);
        return true;
    }

    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Given key is null");
        }

        return keys.contains(key);
    }
}
