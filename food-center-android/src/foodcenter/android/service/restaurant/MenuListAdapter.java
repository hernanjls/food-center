package foodcenter.android.service.restaurant;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import foodcenter.android.ObjectCashe;
import foodcenter.android.R;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

public class MenuListAdapter extends BaseAdapter
{

    private final Activity activity;
    private CourseProxy courses[] = null;
    private TreeMap<Integer, Integer> counter = null; // position -> counter
    private TreeMap<Integer, String> categoryNames;
    private static final int layoutId_item = R.layout.branch_view_list_course_item;
    private static final int layoutId_header = R.layout.branch_view_list_category_item;

    public MenuListAdapter(Activity activity, MenuProxy menu)
    {
        super();

        this.activity = activity;

        categoryNames = new TreeMap<Integer, String>();
        counter = new TreeMap<Integer, Integer>();
        
        List<CourseProxy> coursesList = new LinkedList<CourseProxy>();
        if (null == menu.getCategories())
        {
            return;
        }

        // For each idx if courses is null than there is a category (or end of items)
        int n = menu.getCategories().size();
        int idx = 0;
        for (int i = 0; i < n; ++i)
        {
            MenuCategoryProxy cat = menu.getCategories().get(i);
            categoryNames.put(idx, cat.getCategoryTitle());
            coursesList.add(null);
            ++idx;
            if (null == cat.getCourses() || 0 == cat.getCourses().size())
            {
                continue;
            }

            int m = cat.getCourses().size();
            for (int j = 0; j < m; ++j)
            {
                coursesList.add(cat.getCourses().get(j));
                counter.put(idx, 0);
                ++idx;
            }
        }
        courses = new CourseProxy[coursesList.size()];
        coursesList.toArray(courses);
    }

    public void increaseCounter(int position)
    {
        int old = counter.get(position);
        counter.put(position, old+1);
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
            int i = old-1;
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
    
    public Double getPrice(int pos)
    {
        CourseProxy b = getItem(pos);
        if (null == b)
        {
            return 0.0;
        }
        return b.getPrice() * counter.get(pos);
    }
    
    @Override
    public int getCount()
    {
        return (null != courses) ? courses.length : 0;
    }

    @Override
    public CourseProxy getItem(int position)
    {
        if (position >= getCount())
        {
            return null;
        }
        return courses[position];
    }
    
    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return null != getItem(position);
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

    private View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        String txt = categoryNames.get(position);
        if (convertView == null)
        {
            convertView = activity.getLayoutInflater().inflate(layoutId_header, parent, false);
        }
        TextView res = (TextView) convertView;
        res.setText(txt);
        return res;
    }

    private View getCourseView(int position, View convertView, ViewGroup parent)
    {
        CourseProxy c = getItem(position);
        if (null == c)
        {
            return null;
        }

        if (convertView == null)
        {
            convertView = activity.getLayoutInflater().inflate(layoutId_item, parent, false);
        }

        RelativeLayout res = (RelativeLayout) convertView;

        TextView txtView = (TextView) res.findViewById(R.id.branch_view_list_item_txt);
        txtView.setText(c.getName());

        TextView priceView = (TextView) res.findViewById(R.id.branch_view_list_item_price);
        priceView.setText(c.getPrice().toString());

        EditText cntView = (EditText) res.findViewById(R.id.branch_view_list_item_cnt);
        cntView.setText(counter.get(position).toString());
        
        ObjectCashe.put(CourseProxy.class, c.getId(), c);
        res.setTag(R.id.adapter_id_tag, c.getId());
        return res;
    }
}
