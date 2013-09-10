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
    private List<CourseProxy> courses = null;
    private TreeMap<Integer, Integer> counter = null; // position -> counter
    private TreeMap<Integer, String> categoryNames;
    private static final int layoutId_item = R.layout.branch_view_list_course_item;
    private static final int layoutId_header = R.layout.branch_view_list_category_item;

    public MenuListAdapter(Activity activity, MenuProxy menu)
    {
        super();

        this.activity = activity;

        courses = new LinkedList<CourseProxy>();
        categoryNames = new TreeMap<Integer, String>();
        counter = new TreeMap<Integer, Integer>();
        
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

    public Integer getCounter(int pos)
    {
        return counter.get(pos);
    }

    @Override
    public int getCount()
    {
        return (null != courses) ? courses.size() : 0;
    }

    @Override
    public CourseProxy getItem(int position)
    {
        return courses.get(position);
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

    private View getHeaderView(int position, View view, ViewGroup parent)
    {
        String txt = categoryNames.get(position);
        if (view == null || !(view instanceof TextView))
        {
        	// on scrolling view can be course view
            view = activity.getLayoutInflater().inflate(layoutId_header, parent, false);
        }
        TextView res = (TextView) view;
        res.setText(txt);
        view.setTag(R.id.swipable, false);
        return res;
    }

    private View getCourseView(int position, View view, ViewGroup parent)
    {
        CourseProxy c = getItem(position);
        if (null == c)
        {
            return null;
        }

        if (view == null || !(view instanceof RelativeLayout))
        {
        	// on scrolling view can be category view
            view = activity.getLayoutInflater().inflate(layoutId_item, parent, false);
        }

        TextView txtView = (TextView) view.findViewById(R.id.branch_view_list_item_txt);
        if (null != c.getName())
        {
            txtView.setText(c.getName());
        }

        TextView priceView = (TextView) view.findViewById(R.id.branch_view_list_item_price);
        if (null != c.getPrice())
        {
            priceView.setText(c.getPrice().toString());
        }

        EditText cntView = (EditText) view.findViewById(R.id.branch_view_list_item_cnt);
        cntView.setText(counter.get(position).toString());

        TextView infoView =  (TextView) view.findViewById(R.id.branch_view_list_item_info);
        if (null != c.getInfo())
        {
            String info = c.getInfo().replace("\\n", "\n");
            infoView.setText(info);
        }
        
        ObjectCashe.put(CourseProxy.class, c.getId(), c);
        view.setTag(R.id.adapter_id_tag, c.getId());
        view.setTag(R.id.swipable, true);
        return view;
    }
}
