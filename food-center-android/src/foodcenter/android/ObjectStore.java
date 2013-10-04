package foodcenter.android;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows passing objects from one intent to another based on ID
 */
public class ObjectStore
{
    /** class -> (key -> class-object) */
    private final static Map<Class<? extends Object>, Map<String, ? extends Object>> objects = 
        new HashMap<Class<? extends Object>, Map<String, ? extends Object>>();
    
    /**
     * @param clazz the class to search for
     * @param key the key to use
     * @return found object for clazz and key, or null
     */
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
    
    /**
     * Add a new object to ObjectStore. <br>
     * Or override an existing object with the same id and class. <br>
     * to delete an object put null.
     *
     * @param clazz the class type
     * @param id to save the object with (usually you want to user obj.getId() if it exists)
     * @param obj the object to store
     */
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
    
    /**
     * Clears the ObjectStore from all objects.<br>
     * @see {@link #put(Class, String, Object)} - to remove a single object.
     */
    public static void clear()
    {
        objects.clear();
    }
    
}
