package foodcenter.android;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows passing objects from one intent to another based on ID
 */
public class ObjectStore
{
    // class -> (key -> class-object)
    private final static Map<Class<? extends Object>, Map<String, ? extends Object>> objects = 
        new HashMap<Class<? extends Object>, Map<String, ? extends Object>>();
    
    @SuppressWarnings("unchecked")
    public static <T extends Object> T get(Class<T> clazz, String key)
    {
        Map<String, T> clazzMap = (Map<String, T>) objects.get(clazz);
        if (null == clazzMap)
        {
            return null;
        }
        return clazzMap.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Object> void put(Class<T> clazz, String id, T obj)
    {
        Map<String, T> clazzMap = (Map<String, T>) objects.get(clazz);
        if (null == clazzMap)
        {
            clazzMap = new HashMap<String, T>();
            objects.put(clazz, clazzMap);
        }
        clazzMap.put(id, obj);
    }
    
    public static void clear()
    {
        objects.clear();
    }
    
}
