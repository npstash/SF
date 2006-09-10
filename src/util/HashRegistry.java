package util;

import java.util.HashMap;

/**
 * @author Nestor
 */
public class HashRegistry {

    private static HashMap reg = new HashMap();
    
    public static void addKey(Object key, Object value) {
        reg.put(key, value);
    }
    
    public static Object getObject(Object key) {
        return reg.get(key);
    }
    
}
