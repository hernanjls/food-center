package foodcenter.android.activities.branch;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import foodcenter.android.R;
import foodcenter.android.adapters.AbstractCourseAdapter;
import foodcenter.android.data.MenuSavedState;
import foodcenter.android.data.OrderData;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuProxy;

/**
 * List Adapter which handles the menu items.
 */
public class BranchMenuListAdapter extends AbstractCourseAdapter
{

    private final MenuSavedState savedState;
    
    public BranchMenuListAdapter(Activity activity, MenuProxy menu, MenuSavedState savedState)
    {
        super(activity);

        this.savedState = (null != savedState) ? savedState : new MenuSavedState(menu);

    }

    public MenuSavedState getSavedState()
    {
        return savedState.clone();
    }
    
    @Override
    protected Map<Integer, Integer> getCounter()
    {
        return savedState.counter;
    }
    
    @Override
    protected List<CourseProxy> getCourses()
    {
        return savedState.courses;
    }

    
    /**
     * create a new ImageView for each item referenced by the Adapter
     */
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
        // Category is considered as a separator (disable clicking/ swiping)
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
        
        if (view == null || !(view instanceof TextView))
        {
            // on scrolling view can be course view
            view = activity.getLayoutInflater().inflate(R.layout.branch_view_list_category_item,
                                                        parent,
                                                        false);
        }
        
        String txt = savedState.categoryNames.get(position);
        TextView res = (TextView) view;
        res.setText(txt);
        view.setTag(R.id.swipable, false);
        return res;
    }

    /** aggregate the order information into a OrderData holder */
    public OrderData getOrderConfData(ServiceType service)
    {
        OrderData res = new OrderData();
        res.setService(service);
        
        int n = getCount();
        for (int i = 0; i<n; ++i)
        {
            Integer cnt = savedState.counter.get(i);
            if (null != cnt && cnt > 0)
            {
                res.addCourse(savedState.courses.get(i), savedState.counter.get(i));
            }
        }
        return res;        
    }
    
    /** 
     * Increase the counter of item at position. <br>
     * Usually called on swipe right
     * 
     * @param position
     */
    public void increaseCounter(int position)
    {
        int old = savedState.counter.get(position);
        savedState.counter.put(position, old + 1);
        notifyDataSetChanged();
    }

    /** 
     * Decrease the counter of item at position. <br>
     * Usually called on swipe left
     * 
     * @param position
     * @return true if can still decrease
     */
    public boolean decreaseCounter(int position)
    {
        int old = savedState.counter.get(position);
        if (old > 0)
        {
            int i = old - 1;
            savedState.counter.put(position, i);
            notifyDataSetChanged();
            return (0 != i);
        }
        return false;
    }

    /**
     * Clear the counter for position
     * 
     * @param pos
     */
    public void clearCounter(int pos)
    {
        savedState.counter.put(pos, 0);
        notifyDataSetChanged();
    }

    /**
     * Clear counters for all the items
     */
    public void clearCounters()
    {
        for (Integer pos : savedState.counter.keySet())
        {
            savedState.counter.put(pos, 0);
        }
        notifyDataSetChanged();

    }

    /**
     * @param pos
     * @return the counter of item at pos
     */
    public Integer getCounter(int pos)
    {
        return savedState.counter.get(pos);
    }

}
