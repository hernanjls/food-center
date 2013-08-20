package foodcenter.client.panels;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.requset.AdminServiceRequest;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class RestaurantsListPanel extends VerticalPanel
{

    private final static int COLUMN_IMAGE = 0;
    private final static int COLUMN_NAME = 1;
    private final static int COLUMN_DELIVERY = 2;
    private final static int COLUMN_TAKEAWAY = 3;
    private final static int COLUMN_TABLE = 4;
    private final static int COLUMN_NEW_BUTTON = 5;
    private final static int COLUMN_VIEW_BUTTON = 5;
    private final static int COLUMN_EDIT_BUTTON = 6;
    private final static int COLUMN_DELETE_BUTTON = 7;

    private final boolean isAdmin;
    private final Panel hPanel;
    private final FlexTable restsTable;

    private RestaurantProxy lastViewdRest;

    public RestaurantsListPanel(boolean isAdmin)
    {
        super();
        this.isAdmin = isAdmin;

        hPanel = createOptionsHorizonalPannel();
        add(hPanel);

        restsTable = new FlexTable();
        add(restsTable);

        lastViewdRest = null;

        reloadRestaurants();
    }

    private void reloadRestaurants()
    {
        ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
        PopupPanel popup = new PopupPanel(false);
        popup.setWidget(new Label("Loading..."));
        popup.center();

        // Load the Restaurants without Menus, etc...
        // Fetch Groups issue not appearing on JUNIT tests!
        service.getDefaultRestaurants().fire(new GetDefaultRestaurantsReceiver(popup));
    }

    private void redraw(List<RestaurantProxy> rests)
    {
        restsTable.removeAllRows();
        printRestaurantsTableHeader();

        if (null != rests)
        {
            int row = restsTable.getRowCount();
            for (RestaurantProxy r : rests)
            {
                printRestaurantRow(r, row);
                row++;
            }
        }
    }

    private void printRestaurantRow(RestaurantProxy rest, int row)
    {
        EditableImage img = new EditableImage(rest.getImageUrl());
        restsTable.setWidget(row, COLUMN_IMAGE, img);
        
        String name = rest.getName();
        restsTable.setText(row, COLUMN_NAME, name);

        String delivery = rest.getServices().contains(ServiceType.DELIVERY) ? "yes" : "no";
        restsTable.setText(row, COLUMN_DELIVERY, delivery);

        String takeAway = rest.getServices().contains(ServiceType.TAKE_AWAY) ? "yes" : "no";
        restsTable.setText(row, COLUMN_TAKEAWAY, takeAway);

        String table = rest.getServices().contains(ServiceType.TABLE) ? "yes" : "no";
        restsTable.setText(row, COLUMN_TABLE, table);

        Button view = new Button("View");
        view.addClickHandler(new OnClickViewRestaurant(rest));
        restsTable.setWidget(row, COLUMN_VIEW_BUTTON, view);

        if (rest.isEditable())
        {
            Button edit = new Button("Edit");
            edit.addClickHandler(new OnClickEditRestaurant(rest));
            restsTable.setWidget(row, COLUMN_EDIT_BUTTON, edit);

            Button delete = new Button("Delete");
            delete.addClickHandler(new OnClickDeleteRestaurant(rest));
            restsTable.setWidget(row, COLUMN_DELETE_BUTTON, delete);
        }
    }

    private void printRestaurantsTableHeader()
    {
        restsTable.setText(0, COLUMN_IMAGE, "Image");
        restsTable.setText(0, COLUMN_NAME, "Name");
        restsTable.setText(0, COLUMN_DELIVERY, "Delivery");
        restsTable.setText(0, COLUMN_TAKEAWAY, "Take Away");
        restsTable.setText(0, COLUMN_TABLE, "Table");

        if (isAdmin)
        {
            Button newButton = new Button("New");
            newButton.addClickHandler(new OnClickNewRestaurant());
            restsTable.setWidget(0, COLUMN_NEW_BUTTON, newButton);
        }

    }

    private Panel createOptionsHorizonalPannel()
    {
        TextBox searchBox = new TextBox();

        CheckBox delivery = new CheckBox("delivery");
        delivery.setValue(true);

        CheckBox takeAway = new CheckBox("take away");
        takeAway.setValue(true);

        CheckBox table = new CheckBox("table");
        table.setValue(true);

        Button searchButton = new Button("Search");

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(searchBox);
        hPanel.add(delivery);
        hPanel.add(takeAway);
        hPanel.add(table);
        hPanel.add(searchButton);

        return hPanel;
    }

    private class GetDefaultRestaurantsReceiver extends Receiver<List<RestaurantProxy>>
    {
        private final PopupPanel popup;

        public GetDefaultRestaurantsReceiver(PopupPanel popup)
        {
            this.popup = popup;
        }

        @Override
        public void onSuccess(List<RestaurantProxy> response)
        {
            redraw(response);
            popup.removeFromParent();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            popup.removeFromParent();
            Window.alert("[FAIL] service connection error: " + error.getMessage());
        }
    }

    private abstract class OnClickCommonRestaurant extends Receiver<RestaurantProxy> implements ClickHandler
    {
        /**
         * 
         * @param rest - If null a new restaurant is created and panel starts in edit mode
         * @param isEditMode - Creates editable restaurant and starts the panel in edit mode
         */
        protected void onClickRestaurant(RestaurantProxy rest, boolean isEditMode)
        {
            // Create a popup panel to show the new restaurant in
            PopupPanel holder = new PopupPanel(false);

            Runnable afterClose = new AfterCloseRestaurant(holder);

            // Will be set according to the edit mode
            Runnable afterOk = null;
            Runnable onClickEdit = null;

            RestaurantAdminServiceRequest service = RequestUtils.getRequestFactory()
                .getRestaurantAdminService();

            if (null == rest)
            {
                rest = RequestUtils.createRestaurantProxy(service);
                isEditMode = true;
                afterOk = new AfterOkRestaurant();
            }

            else if (isEditMode)
            {
                rest = service.edit(rest);
                afterOk = new AfterOkRestaurant();
            }
            else if (rest.isEditable())
            {
                onClickEdit = new OnClickEditRestaurant(rest);
            }

            RestaurantPanel restPanel = new RestaurantPanel(service,
                                                            rest,
                                                            isEditMode,
                                                            afterOk,
                                                            afterClose,
                                                            onClickEdit);

            // Add the restaurant panel to the popup and show it.
            holder.setWidget(restPanel);
            holder.show();
        }

        @Override
        public void onSuccess(RestaurantProxy response)
        {
            lastViewdRest = response;            
            onClick(null);
        }
        
        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't load rest " + error.getMessage());
        }
    }

    private class OnClickViewRestaurant extends OnClickCommonRestaurant
    {
        private final RestaurantProxy rest;

        public OnClickViewRestaurant(RestaurantProxy rest)
        {
            super();
            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            if (null == lastViewdRest || !lastViewdRest.getId().equals(rest.getId()))
            {
                ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
                service.getRestaurantById(rest.getId()).with(RestaurantProxy.REST_WITH).fire(this);
            }
            else
            {
                onClickRestaurant(lastViewdRest, false);
            }
        }
    }

    private class OnClickEditRestaurant extends OnClickCommonRestaurant implements Runnable
    {
        private final RestaurantProxy rest;

        public OnClickEditRestaurant(RestaurantProxy rest)
        {
            super();
            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            if (null == lastViewdRest || !lastViewdRest.getId().equals(rest.getId()))
            {
                ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
                service.getRestaurantById(rest.getId()).with(RestaurantProxy.REST_WITH).fire(this);
            }
            else
            {
                onClickRestaurant(lastViewdRest, true);
            }
        }

        @Override
        public void run()
        {
            onClick(null);
        }
    }
    
    private class OnClickDeleteRestaurant extends Receiver<Boolean> implements ClickHandler
    {
        private final RestaurantProxy rest;
        
        public OnClickDeleteRestaurant(RestaurantProxy rest)
        {
            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            AdminServiceRequest service = RequestUtils.getRequestFactory().getAdminService();
            service.deleteRestaurant(rest.getId()).fire(this);
        }

        @Override
        public void onSuccess(Boolean response)
        {
            reloadRestaurants();            
        }
        
        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert(error.getMessage());
        }
        
    }

    private class OnClickNewRestaurant extends OnClickCommonRestaurant
    {
        @Override
        public void onClick(ClickEvent event)
        {
            // Will create a new restaurant
            onClickRestaurant(null, true);
        }
    }

    /**
     * Removes the holder from the parent
     */
    private class AfterCloseRestaurant implements Runnable
    {
        private final PopupPanel holder;

        public AfterCloseRestaurant(PopupPanel holder)
        {
            this.holder = holder;
        }

        @Override
        public void run()
        {
            holder.removeFromParent();
            reloadRestaurants();
        }

    }

    /**
     * Reloads the restaurant {@link RestaurantsListPanel#reloadRestaurants()} <br>
     * will be called before {@link AfterCloseRestaurant}
     */
    private class AfterOkRestaurant implements Runnable 
    {
        @Override
        public void run()
        {
            lastViewdRest = null;
        }
    }

}
