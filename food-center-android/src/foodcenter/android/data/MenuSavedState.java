package foodcenter.android.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

public class MenuSavedState implements Cloneable
{
    /** (position -> numItems) counter, null if this is a category */
    public Map<Integer, Integer> counter = new TreeMap<Integer, Integer>();
   
    /** (position -> category name) , null if this is a course position */
    public Map<Integer, String> categoryNames = new TreeMap<Integer, String>();
    
    public List<CourseProxy> courses = new LinkedList<CourseProxy>();
    
    public MenuSavedState()
    {
        
    }
    
    public MenuSavedState(MenuProxy menu)
    {
        if (null == menu || null == menu.getCategories())
        {
            return;
        }

        // For each idx if courses is null than there is a category (or end of items)
        int n = menu.getCategories().size();
        int k = 0;
        for (int i = 0; i < n; ++i)
        {
            MenuCategoryProxy cat = menu.getCategories().get(i);
            categoryNames.put(k, cat.getCategoryTitle());
            courses.add(null);  // courses holds null where there is categories
            ++k;

            // When category is empty, continue to the next category.
            if (null == cat.getCourses() || 0 == cat.getCourses().size())
            {
                continue;
            }

            // Add all the courses to the adapter courses.
            int m = cat.getCourses().size();
            for (int j = 0; j < m; ++j)
            {
                this.counter.put(k, 0);
                courses.add(cat.getCourses().get(j));
                ++k;
            }
        }
    }
    
    @Override
    public MenuSavedState clone()
    {
        MenuSavedState res = new MenuSavedState();
        res.counter.putAll(this.counter);
        res.categoryNames.putAll(this.categoryNames);
        res.courses.addAll(this.courses);
        return res;
    }
}
