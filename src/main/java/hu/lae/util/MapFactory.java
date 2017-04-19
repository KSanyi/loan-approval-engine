package hu.lae.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapFactory<T,V> {

    private MapFactory() {}
    
    public static<T,V> Map<T,V> of(T key, V value) {
        Map<T,V> map = new HashMap<>();
        map.put(key, value);
        return Collections.unmodifiableMap(map);
    }
    
    public static<T,V> Map<T,V> of(T key1, V value1, T key2, V value2) {
        Map<T,V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return Collections.unmodifiableMap(map);
    }
    
    public static<T,V> Map<T,V> of(T key1, V value1, T key2, V value2,T key3, V value3) {
        Map<T,V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return Collections.unmodifiableMap(map);
    }
    
}
