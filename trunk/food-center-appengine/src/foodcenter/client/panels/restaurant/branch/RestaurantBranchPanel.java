package foodcenter.client.panels.restaurant.branch;

import java.util.logging.Logger;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.WebClientUtils;
import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.panels.common.BranchOrdersHistoryPanel;
import foodcenter.client.panels.common.UsersPanel;
import foodcenter.client.panels.restaurant.branch.orders.PendingOrdersPanel;
import foodcenter.client.panels.restaurant.branch.orders.PendingReservationsPanel;
import foodcenter.client.panels.restaurant.menu.MenuPanel;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.autobean.AutoBeanHelper;
import foodcenter.service.autobean.OrderBroadcast;
import foodcenter.service.autobean.OrderBroadcastAutoBeanFactory;
import foodcenter.service.autobean.OrderBroadcastType;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;
import foodcenter.service.requset.RestaurantChefServiceRequest;

public class RestaurantBranchPanel extends PopupPanel implements RedrawablePanel
{

    private final RestaurantBranchAdminServiceRequest service;
    private final RestaurantBranchProxy branch;
    private final PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback;

    private final Logger logger = Logger.getLogger(PendingOrdersPanel.class.toString());
    private final static OrderBroadcastAutoBeanFactory factory = GWT.create(OrderBroadcastAutoBeanFactory.class);


    private Socket socket = null;
    private int socketErrors = 0;

    private final boolean isEditMode;
    private final VerticalPanel main;

    private MenuPanel menuPanel = null;

    private PendingOrdersPanel pendingOrders = null;
    private PendingReservationsPanel pendingReservations = null;

    public RestaurantBranchPanel(RestaurantBranchProxy branch,
                                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
    {
        this(branch, callback, null);
    }

    public RestaurantBranchPanel(RestaurantBranchProxy branch,
                                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback,
                                 RestaurantBranchAdminServiceRequest service)
    {
        super();

        this.branch = branch;
        this.callback = callback;
        this.service = service;

        this.isEditMode = (service != null);

        setStyleName("popup-common");

        this.main = new VerticalPanel();
        main.setStyleName("popup-main-panel");

        // Create a channel!
        RestaurantChefServiceRequest channelService = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();

        // Add the main Panel
        add(main);

        // Show this Panel
        show();

        // Draw the main Panel's data
        redraw();

        if (branch.isChef() && !isEditMode)
        {
            channelService.createChannel(branch.getId()).fire(new ChannelTokenReciever());
        }
    }

    @Override
    public void redraw()
    {
        main.clear();

        main.add(createButtonsPanel());
        main.add(createDetailsPanel());

        center();
        setPopupPosition(getAbsoluteLeft(), 60);
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
        
        removeFromParent();
    }

    private Panel createButtonsPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        Button close = new Button("Close", new CloseClickHandler());
        res.add(close);

        if (isEditMode)
        {
            Button save = new Button("Save", new SaveClickHandler());
            res.add(save);
        }
        else if (branch.isEditable())
        {
            Button edit = new Button("Edit", new EditClickHandler());
            res.add(edit);
        }

        return res;
    }

    private Widget createDetailsPanel()
    {
        TabPanel res = new TabPanel();
        res.setWidth("100%");
        // res.setHeight("250px");

        Panel locationPanel = new RestaurantBranchLocationVerticalPanel(branch, isEditMode);
        res.add(locationPanel, "Location");
        res.selectTab(res.getTabBar().getTabCount() - 1);

        menuPanel = new MenuPanel(branch.getMenu(), service);
        res.add(menuPanel, "Menu");

        if (branch.isEditable())
        {
            Panel adminsPanel = new UsersPanel(branch.getAdmins(), isEditMode);
            res.add(adminsPanel, "Admins");

            Panel waitersPanel = new UsersPanel(branch.getWaiters(), isEditMode);
            res.add(waitersPanel, "Waiters");

            Panel chefsPanel = new UsersPanel(branch.getChefs(), isEditMode);
            res.add(chefsPanel, "Chefs");
         
            if (!isEditMode)
            {
                Panel orders = new BranchOrdersHistoryPanel(branch.getId(), true);
                res.add(orders, "Orders");
            }
        }

        
        if (!isEditMode && branch.isChef())
        {
            pendingOrders = new PendingOrdersPanel(branch.getId());
            res.add(pendingOrders, "Pending Orders");
        }

        if (!isEditMode && branch.isEditable())
        {
            pendingReservations = new PendingReservationsPanel(branch.getId());
            res.add(pendingReservations, "Pending Reservations");
        }
        
        // TODO tables res.add(tablesPanel, "Tables");
        // TODO orders res.add(ordersPanel, "Orders");
        return res;
    }

    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */

    private class OnChannelCreated implements ChannelCreatedCallback
    {
        @Override
        public void onChannelCreated(Channel channel)
        {
            logger.fine("channel was created");

            // close current socket if it is opened
            if (null != socket)
            {
                socket.close();
                socket = null;
            }

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
        public void onMessage(String json)
        {
            logger.fine("got order in msg: " + json);
            OrderBroadcast order = AutoBeanHelper.deserializeFromJson(factory, OrderBroadcast.class, json);
            // message is received - can reset counter for socket errors (num open retries)
            socketErrors = 0;
            
            if (OrderBroadcastType.ORDER == order.getType())
            {
                pendingOrders.handleOrderId(order.getId());
            }
            else if (OrderBroadcastType.TABLE == order.getType())
            {
                pendingReservations.handleReservationId(order.getId());
            }
            else
            {
                Window.alert("Invalid order type...");
            }
        }

        @Override
        public void onError(SocketError error)
        {
            logger.info("socket error, code "+ error.getCode() + ", desc=" + error.getDescription());
            // Renew token - socket is open for more than 2 hrs
            socket.close();
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
            service.createChannel(branch.getId()).fire(new ChannelTokenReciever());
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

    private class CloseClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.close(RestaurantBranchPanel.this, branch);
        }
    }

    private class EditClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(RestaurantBranchPanel.this, branch, callback);
        }
    }

    private class SaveClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            menuPanel.setToService();

            callback.save(RestaurantBranchPanel.this,
                          branch,
                          callback,
                          (RestaurantAdminServiceRequest) service);
        }
    }

}
