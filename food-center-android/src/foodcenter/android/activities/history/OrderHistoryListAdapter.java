package foodcenter.android.activities.history;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import foodcenter.android.R;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;

public class OrderHistoryListAdapter extends BaseExpandableListAdapter
{

    private class CourseHolder
    {
        public TextView name;
        public EditText cnt;
        public TextView price;
    }
    
    private class OrderHolder
    {
        public ImageView serviceImage;
        public TextView restName;
        public TextView branchAddr;
        public TextView totalPrice;
        public TextView orderDate;
        public TextView deliveryDate;
    }
    
    private final Activity activity;
    private final List<OrderProxy> orders;

    private final DateFormat dateFormat;
    public OrderHistoryListAdapter(Activity context)
    {
        super();
        this.activity = context;
        dateFormat  = new SimpleDateFormat("dd.MM.yyyy HH:mm", activity.getResources().getConfiguration().locale);
        
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
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition,
                             boolean isLastChild,
                             View view,
                             ViewGroup parent)
    {
        
        CourseOrderProxy c = getChild(groupPosition, childPosition);
        if (null == c)
        {
            return null;
        }

        if (view == null)
        {
            // on scrolling view can be another view because of new position
            view = activity.getLayoutInflater().inflate(R.layout.order_history_course_layout,
                                                        parent,
                                                        false);
            CourseHolder holder = new CourseHolder();
            view.setTag(holder);
            
            holder.name = (TextView) view.findViewById(R.id.order_history_course_name);
            holder.price = (TextView) view.findViewById(R.id.order_history_course_price);
            holder.cnt = (EditText) view.findViewById(R.id.order_history_course_cnt);
        }

        CourseHolder holder = (CourseHolder) view.getTag();
        holder.name.setText(c.getName());
        holder.price.setText(c.getPrice().toString());
        holder.cnt.setText("" + c.getCnt());
        
        
        return view;
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
        OrderProxy o = getGroup(groupPosition);
        if (null == o)
        {
            return null;
        }

        if (view == null)
        {
            // on scrolling view can be another view because of new position
            view = activity.getLayoutInflater().inflate(R.layout.order_history_title_layout,
                                                        parent,
                                                        false);
            OrderHolder holder = new OrderHolder();
            view.setTag(holder);
            
            holder.serviceImage = (ImageView) view.findViewById(R.id.order_history_title_service_img);
            holder.restName = (TextView) view.findViewById(R.id.order_history_title_rest_name);
            holder.branchAddr = (TextView) view.findViewById(R.id.order_history_title_rest_branch_addr);
            holder.totalPrice = (TextView) view.findViewById(R.id.order_history_title_price);
            holder.orderDate = (TextView) view.findViewById(R.id.order_history_title_order_date);
            holder.deliveryDate = (TextView) view.findViewById(R.id.order_history_title_delivery_date);
        }

        OrderHolder holder = (OrderHolder) view.getTag();
        
        // Set the restaurant name
        holder.restName.setText(o.getRestName());
                        
        // Set total price of order
        Double totalPrice = 0.0;
        for (int i=0; i< getChildrenCount(groupPosition); ++i)
        {
            CourseOrderProxy c = getChild(groupPosition, i);
            totalPrice += c.getCnt() * c.getPrice();
        }
        holder.totalPrice.setText(totalPrice.toString());
        
        // Set Order date
        holder.orderDate.setText(dateFormat.format(o.getDate()));
        
        // Set Delivery date
        if (o.getDelivered())
        {
            holder.deliveryDate.setText(dateFormat.format(o.getDeliveryeDate()));
        }
        else
        {
            holder.deliveryDate.setText(activity.getString(R.string.order_order_delivery_date));
        }
        
        // Set restaurant branch address
        holder.branchAddr.setText(o.getRestBranchAddr());

        // Set the service image
        switch (o.getService())
        {
            case DELIVERY:
                holder.serviceImage.setBackgroundResource(R.drawable.delivery);
                break;
            case TABLE:
                holder.serviceImage.setBackgroundResource(R.drawable.table);
                break;
            case TAKE_AWAY:
                holder.serviceImage.setBackgroundResource(R.drawable.takeaway);
                break;
        }

        return view;
    }

    @Override
    public boolean hasStableIds()
    {
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
