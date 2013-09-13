package foodcenter.android.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import foodcenter.android.R;
import foodcenter.android.activities.helpers.OrderConfData;

public class OrderConfAdapter extends AbstractCourseAdapter
{
    public OrderConfAdapter(Activity activity,
                            OrderConfData data)
    {
        super(activity, data.getCourses(), data.getCounters());
    }

    @Override
    public int getCount()
    {
        // extra row for header
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (0 == position)
        {
            return getHeaderView(view, parent);
        }

        return getCourseView(position - 1, view, parent);
    }

    @Override
    public boolean isEnabled(int position)
    {
        return false;
    }
    
    private View getHeaderView(View view, ViewGroup parent)
    {
        if ((view == null) || (R.id.order_list_header_cnt != view.getId()))
        {
            // on scrolling view can be another view because of new position
            view = activity.getLayoutInflater().inflate(R.layout.order_list_header, parent, false);
        }

        return view;

    }

}
