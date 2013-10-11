package foodcenter.client.panels.restaurant.branch.orders;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.WebClientUtils;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.RestaurantChefServiceRequest;

public class PendingOrdersPanel extends FlexTable
{
    private ArrayList<OrderProxy> orders;

    private final DateTimeFormat formatter;
    
    private final static Integer SERVICE_TYPE_IMG_WIDTH_PX = 50;
    private final static Integer SERVICE_TYPE_IMG_HEIGHT_PX = 50;

    public PendingOrdersPanel(String branchId)
    {
        super();

        orders = new ArrayList<OrderProxy>();
        formatter = WebClientUtils.getDateFormatter();
        
        setStyleName("one-column-emphasis");

        RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();

        service.getPendingOrders(branchId)
            .with(OrderProxy.ORDER_WITH)
            .fire(new PendingOrdersReciever());
    }

    public void handleOrderId(String orderId)
    {
        if (null == orderId)
        {
            return;
        }

        RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();
        service.getOrderById(orderId).with(OrderProxy.ORDER_WITH).fire(new NewOrderReciever());
    }

    public void redraw()
    {
        removeAllRows();
        int row = 0;
        setText(row, 0, "Order information");
        setText(row, 1, "Date");
        setText(row, 2, "Courses");
        getRowFormatter().setStyleName(row, "th");
        ++row;
        
        for (OrderProxy o : orders)
        {
            setWidget(row, 0, new PendingOrderTypePanel(o));
            setText(row, 1, formatter.format(o.getDate()));
            setWidget(row, 2, new CoursesInfoPanel(o));
            setWidget(row, 3, new PendingOrderControlPanel(o));
            getRowFormatter().setStyleName(row, "td");
            ++row;
        }
    }

    private class NewOrderReciever extends Receiver<OrderProxy>
    {
        @Override
        public void onSuccess(OrderProxy response)
        {
            if (null == response)
            {
                return;
            }

            synchronized (orders)
            {

                if (OrderStatus.CREATED != response.getStatus())
                {
                    orders.remove(response);
                    redraw();
                }
                else
                {
                    orders.add(response);
                    redraw();
                }
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't get order: " + error.getMessage());
        }

    }

    private class PendingOrdersReciever extends Receiver<List<OrderProxy>>
    {

        @Override
        public void onSuccess(List<OrderProxy> response)
        {
            if (null == response || response.isEmpty())
            {
                return;
            }

            synchronized (orders)
            {
                for (OrderProxy o : response)
                {
                    if (!orders.contains(o))
                    {
                        orders.add(o);
                    }
                }
                redraw();
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't get orders: " + error.getMessage());
        }

    }

    private class PendingOrderTypePanel extends HorizontalPanel
    {
        EditableImage img = new EditableImage(null, null);
        VerticalPanel body = new VerticalPanel();

        private PendingOrderTypePanel(OrderProxy order)
        {
            super();
            
            add(img);
            add(body);

            ServiceType service = order.getService();
            String imgUrl = service.getUrl();
            img.updateImage(imgUrl,
                            SERVICE_TYPE_IMG_WIDTH_PX + "px",
                            SERVICE_TYPE_IMG_HEIGHT_PX + "px");

            body.add(new Label(order.getUserEmail()));
            String extra = (ServiceType.DELIVERY == order.getService()) ? order.getCompBranchAddr()
                                                                       : service.getName();
            body.add(new Label(extra));
        }
    }

    private class PendingOrderControlPanel extends VerticalPanel
    {
        private final OrderProxy order;
        private PendingOrderControlPanel(OrderProxy order)
        {
            super();
            this.order = order;

            Button doneButton = new Button("Done", new OnClickDoneButton());
            doneButton.setWidth("90%");
            add(doneButton);

            Button cancelButton = new Button("Cancel", new OnClickCancelButton());
            cancelButton.setWidth("90%");
            add(cancelButton);
        }
        
        private class OnClickDoneButton extends Receiver<OrderProxy> implements ClickHandler
        {

            @Override
            public void onClick(ClickEvent event)
            {
                synchronized (orders)
                {
                    orders.remove(order);
                    redraw();
                }
                RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                    .getRestaurantChefService();
                service.deliverOrder(order.getId()).fire(this);

            }

            @Override
            public void onSuccess(OrderProxy response)
            {
                // do nothing all is good!
            }

            @Override
            public void onFailure(ServerFailure error)
            {
                Window.alert("Failed to save order: " + error.getMessage());
                synchronized (orders)
                {
                    orders.add(order);
                    redraw();
                }

            }

        }

        private class OnClickCancelButton extends Receiver<OrderProxy> implements ClickHandler
        {

            @Override
            public void onClick(ClickEvent event)
            {
                synchronized (orders)
                {
                    orders.remove(order);
                    redraw();
                }

                RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                    .getRestaurantChefService();
                service.cancelOrder(order.getId()).fire(this);
            }

            @Override
            public void onSuccess(OrderProxy response)
            {
                // do nothing all is good!
            }

            @Override
            public void onFailure(ServerFailure error)
            {
                Window.alert("Failed to cancel order: " + error.getMessage());
                synchronized (orders)
                {
                    orders.add(order);
                    redraw();
                }
            }
        }
    }
}
