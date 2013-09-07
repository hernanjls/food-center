package foodcenter.android;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows passing objects from one intent to another based on ID
 */
public class ObjectStore
{
    private static Map<String, Object> objects = new HashMap<String, Object>();
    
    public static Object getOnce(String id)
    {
        Object res = objects.get(id);
        objects.remove(id);
        return res;
    }
    
    public static void put(String id, Object entity)
    {
        objects.put(id, entity);
    }
}
