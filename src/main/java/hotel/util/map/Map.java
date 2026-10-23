package hotel.util.map;

import java.util.List;

public interface Map<K, V> {
    boolean put(K key, V value);
    V get(K key);
    List<K> keys();
    boolean remove(K key);
    boolean contains(K key);
}
