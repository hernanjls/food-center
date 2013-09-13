package foodcenter.android.adapters;

import java.util.LinkedList;
import java.util.TreeMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import foodcenter.android.R;
import foodcenter.android.activities.helpers.OrderConfData;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

public class MenuListAdapter extends AbstractCourseAdapter
{

    private final TreeMap<Integer, String> categoryNames;
    
    public MenuListAdapter(Activity activity, MenuProxy menu)
    {
        super(activity, new LinkedList<CourseProxy>(), new TreeMap<Integer, Integer>());
    
        categoryNames = new TreeMap<Integer, String>();
    
        if (null == menu.getCategories())
        {
            return;
        }

        // For each idx if courses is null than there is a category (or end of items)
        int n = menu.getCategories().size();
        for (int i = 0; i < n; ++i)
        {
            MenuCategoryProxy cat = menu.getCategories().get(i);
            int k = courses.size();
            categoryNames.put(k, cat.getCategoryTitle());
            courses.add(null);

            if (null == cat.getCourses() || 0 == cat.getCourses().size())
            {
                continue;
            }

            int m = cat.getCourses().size();
            for (int j = 0; j < m; ++j)
            {
                k = courses.size();
                counter.put(k, 0);
                courses.add(cat.getCourses().get(j));
            }
        }
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View res = getCourseView(position, convertView, parent);
        if (null != res)
        {
            return res;
        }
        return getHeaderView(position, convertView, parent);
    }

    
    
    @Override
    public boolean isEnabled(int position)
    {
        // Category is considered as a separator (disable swiping)
        return null != getItem(position);
    }
    
    @Override
    protected View getCourseView(int position, View view, ViewGroup parent)
    {
        view = super.getCourseView(position, view, parent);
        if (null != view)
        {
            view.setTag(R.id.swipable, true);
        }
        return view;

    }

    private View getHeaderView(int position, View view, ViewGroup parent)
    {
        String txt = categoryNames.get(position);
        if (view == null || !(view instanceof TextView))
        {
            // on scrolling view can be course view
            view = activity.getLayoutInflater().inflate(R.layout.branch_view_list_category_item,
                                                        parent,
                                                        false);
        }
        TextView res = (TextView) view;
        res.setText(txt);
        view.setTag(R.id.swipable, false);
        return res;
    }

    
    public OrderConfData getOrderConfData(ServiceType service)
    {
        OrderConfData res = new OrderConfData();
        res.setService(service);
        
        int n = getCount();
        for (int i = 0; i<n; ++i)
        {
            Integer cnt = counter.get(i);
            if (null != cnt && cnt > 0)
            {
                res.addCourse(courses.get(i), counter.get(i));
            }
        }
        return res;        
    }
    public void increaseCounter(int position)
    {
        int old = counter.get(position);
        counter.put(position, old + 1);
        notifyDataSetChanged();
    }

    /**
     * 
     * @param position
     * @return true if can still decrease
     */
    public boolean decreaseCounter(int position)
    {
        int old = counter.get(position);
        if (old > 0)
        {
            int i = old - 1;
            counter.put(position, i);
            notifyDataSetChanged();
            return (0 != i);
        }
        return false;
    }

    public void clearCounter(int pos)
    {
        counter.put(pos, 0);
        notifyDataSetChanged();
    }

    public void clearCounters()
    {
        for (Integer pos : counter.keySet())
        {
            counter.put(pos, 0);
        }
        notifyDataSetChanged();

    }

    public Integer getCounter(int pos)
    {
        return counter.get(pos);
    }
    

}
