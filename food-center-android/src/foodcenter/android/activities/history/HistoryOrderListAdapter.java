package foodcenter.android.activities.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;

public class HistoryOrderListAdapter extends BaseExpandableListAdapter
{

    private final Context context;
    private final List<OrderProxy> orders;

    public HistoryOrderListAdapter(Context context)
    {
        super();
        this.context = context;
        
        orders = new ArrayList<OrderProxy>();
    }
    
    @Override
    public CourseOrderProxy getChild(int groupPosition, int childPosition)
    {
        return getGroup(groupPosition).getCourses().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition,
                             boolean isLastChild,
                             View view,
                             ViewGroup parent)
    {
        TextView v = (TextView)view;
        if (null == v)
        {
            v = new TextView(context);
        }
        v.setText(getChild(groupPosition, childPosition).getName());
        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return getGroup(groupPosition).getCourses().size();
    }

    @Override
    public OrderProxy getGroup(int groupPosition)
    {
        
        return orders.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return orders.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition,
                             boolean isExpanded,
                             View view,
                             ViewGroup parent)
    {
        TextView v = (TextView)view;
        if (null == v)
        {
            v = new TextView(context);
        }
        v.setText("rest: " + getGroup(groupPosition).getRestId());
        
        return v;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
    
    public void addOrders(OrderProxy[] orders)
    {
        Collections.addAll(this.orders, orders);
        notifyDataSetChanged();
    }
    
    
    
}
