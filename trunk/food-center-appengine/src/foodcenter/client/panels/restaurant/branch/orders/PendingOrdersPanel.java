package foodcenter.client.panels.restaurant.branch.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.WebClientUtils;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.RestaurantChefServiceRequest;

public class PendingOrdersPanel extends VerticalPanel implements RedrawablePanel
{

    private final Logger logger = Logger.getLogger(PendingOrdersPanel.class.toString());
    private final String branchId;

    private ArrayList<OrderProxy> orders;
    private Socket socket = null;
    private int socketErrors = 0;

    private final static Integer SERVICE_TYPE_IMG_WIDTH_PX = 50;
    private final static Integer SERVICE_TYPE_IMG_HEIGHT_PX = 50;

    public PendingOrdersPanel(String branchId)
    {
        super();

        this.branchId = branchId;
        orders = new ArrayList<OrderProxy>();

        this.setWidth("100%");

        RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();

        service.createChannel(branchId).to(new ChannelTokenReciever());
        service.getPendingOrders(branchId)
            .with(OrderProxy.ORDER_WITH)
            .fire(new PendingOrdersReciever());
    }

    @Override
    public void redraw()
    {
        clear();
        for (OrderProxy o : orders)
        {
            add(new PendingOrderPanel(o));
        }
    }

    @Override
    public void close()
    {
        // close channel here!
        if (null != socket)
        {
            logger.fine("closing socket and setting to null");
            socket.close();
            socket = null;
        }
    }

    private class OnChannelCreated implements ChannelCreatedCallback
    {
        @Override
        public void onChannelCreated(Channel channel)
        {
            logger.fine("channel was created");

            // close current socket if it is opened
            close();

            // channel should never be null here
            logger.fine("openning socket");
            socket = channel.open(new MySocketListener());
        }
    }

    private class MySocketListener implements SocketListener
    {
        @Override
        public void onOpen()
        {
            logger.fine("socket was opened");
        }

        @Override
        public void onMessage(String orderId)
        {
            logger.fine("got orderId in msg: " + orderId);
            
            // message is received - can reset counter for socket errors (num open retries)
            socketErrors = 0;
            
            RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                .getRestaurantChefService();
            service.getOrderById(orderId).with(OrderProxy.ORDER_WITH).fire(new NewOrderReciever());
        }

        @Override
        public void onError(SocketError error)
        {
            logger.info("socket error, code "+ error.getCode() + ", desc=" + error.getDescription());
            // Renew token - socket is open for more than 2 hrs
            close();
            if (socketErrors >= WebClientUtils.SOCKET_ERROR_NUM_RETRIES)
            {
                // dev server was disconnected from the browser plugin
                Window.alert("socket error: " + error.getDescription()
                             + ", reached max retries - please refresh to re-open socket. ");
                return;
            }
         
            // try to re-open the channel
            //TODO reopen channel only on specific error code
            ++socketErrors;
            RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                .getRestaurantChefService();
            service.createChannel(branchId).fire(new ChannelTokenReciever());
        }

        @Override
        public void onClose()
        {
            logger.fine("socket was closed");
        }
    }

    private class ChannelTokenReciever extends Receiver<String>
    {
        @Override
        public void onSuccess(String token)
        {
            if (null != token && 0 != token.length())
            {
                if (null != socket)
                {
                    socket.close();
                    socket = null;
                }
                ChannelFactory.createChannel(token, new OnChannelCreated());
                return;
            }

            Window.alert("You can't get a token for channel probably because of privileges");
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't open channel, please refresh");
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
                if (!orders.contains(response))
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

    private class PendingOrderPanel extends FlexTable
    {
        private final OrderProxy order;

        private PendingOrderPanel(OrderProxy order)
        {
            super();

            this.order = order;

            this.setWidth("90%");

            draw();
        }

        private void draw()
        {
            super.removeAllRows();

            // Print the header
            setText(0, 0, order.getUserEmail());
            setText(0, 1, "Courses");

            // print the order type
            setWidget(1, 0, new PendingOrderTypePanel());

            // print the courses
            setWidget(1, 1, new PendingOrderCoursesPanel());

            // print the control panel (with buttons)
            setWidget(1, 2, new PendingOrderControlPanel());
        }

        private class PendingOrderTypePanel extends HorizontalPanel
        {
            EditableImage img = new EditableImage(null, null);

            public PendingOrderTypePanel()
            {
                super();

                add(img);

                ServiceType service = order.getService();
                String imgUrl = service.getUrl();
                img.updateImage(imgUrl,
                                SERVICE_TYPE_IMG_WIDTH_PX + "px",
                                SERVICE_TYPE_IMG_HEIGHT_PX + "px");

                switch (service)
                {
                    case DELIVERY:
                        add(new Label(order.getCompBranchAddr()));
                        break;
                    default:
                        add(new Label(service.getName()));
                        break;
                }
            }
        }

        private class PendingOrderCoursesPanel extends VerticalPanel
        {
            private PendingOrderCoursesPanel()
            {
                super();

                List<CourseOrderProxy> courses = order.getCourses();
                if (null == courses || courses.isEmpty())
                {
                    return;
                }

                for (CourseOrderProxy c : courses)
                {
                    add(new Label(c.getName()));
                }
            }
        }

        private class PendingOrderControlPanel extends VerticalPanel
        {
            public PendingOrderControlPanel()
            {
                super();

                Button doneButton = new Button("Done", new OnClickDoneButton());
                doneButton.setWidth("90%");
                add(doneButton);

                Button cancelButton = new Button("Cancel", new OnClickCancelButton());
                cancelButton.setWidth("90%");
                add(cancelButton);
            }
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
