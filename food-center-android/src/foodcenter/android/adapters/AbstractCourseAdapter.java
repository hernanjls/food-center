package foodcenter.android.adapters;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import foodcenter.android.R;
import foodcenter.service.proxies.CourseProxy;

public abstract class AbstractCourseAdapter extends BaseAdapter
{

    protected final Activity activity;

    public AbstractCourseAdapter(Activity activity)
    {
        super();
        this.activity = activity;
    }
    
    /** get position -> numItems counter */
    protected abstract Map<Integer, Integer> getCounter();
    
    /** get list of courses, null where there is no course*/
    protected abstract List<CourseProxy> getCourses();
    
    @Override
    public int getCount()
    {
        return getCourses().size();
    }

    @Override
    public CourseProxy getItem(int position)
    {
        return getCourses().get(position);
    }

    /**
     * Require for structure, not really used in my code. <br>
     * Can be used to get the id of an item in the adapter for manual control.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    protected View getCourseView(int position, View view, ViewGroup parent)
    {
        CourseProxy c = getItem(position);
        if (null == c)
        {
            return null;
        }

        if ((view == null) || (R.id.branch_view_list_item != view.getId()))
        {
            // on scrolling view can be another view because of new position
            view = activity.getLayoutInflater().inflate(R.layout.branch_view_list_course_item,
                                                        parent,
                                                        false);
            CourseViewHolder holder = new CourseViewHolder(view);
            view.setTag(holder);
        }
        
        CourseViewHolder holder = (CourseViewHolder) view.getTag();
        
        if (null != c.getName())
        {
            holder.txtView.setText(c.getName());
        }

        if (null != c.getPrice())
        {
            holder.priceView.setText(c.getPrice().toString());
        }

        holder.cntView.setText(getCounter().get(position).toString());

        if (null != c.getInfo())
        {
            String info = c.getInfo().replace("\\n", "\n");
            holder.infoView.setText(info);
        }

        return view;
    }

    public Double getPrice(int pos)
    {
        CourseProxy b = getItem(pos);
        if (null == b)
        {
            return 0.0;
        }
        return b.getPrice() * getCounter().get(pos);
    }

    public double getTotalPrice()
    {
        Double totalCost = 0.0;
        int n = getCourses().size();
        for (int i = 0; i < n; ++i)
        {
            totalCost += getPrice(i);
        }
        return totalCost;
    }
    
    /** Holder for the view, getViewById is expensive */
    private class CourseViewHolder
    {
        private final TextView txtView;
        private final TextView priceView;
        private final EditText cntView;
        private final TextView infoView;
        
        private CourseViewHolder(View view)
        {
            txtView = (TextView) view.findViewById(R.id.branch_view_list_item_txt);
            priceView = (TextView) view.findViewById(R.id.branch_view_list_item_price);
            cntView = (EditText) view.findViewById(R.id.branch_view_list_item_cnt);
            infoView = (TextView) view.findViewById(R.id.branch_view_list_item_info);;
        }         
    }
}
