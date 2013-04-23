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
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.UserCommonServiceProxy;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantsListPanel extends VerticalPanel
{

    private final boolean isAdmin;
    static int i = 0;
    private final Panel hPanel;
    private final FlexTable restsTable;
    
    public RestaurantsListPanel(boolean isAdmin)
    {
        super();
        this.isAdmin = isAdmin;
        
        hPanel = createOptionsHorizonalPannel();
        add(hPanel);
        
        restsTable = new FlexTable();
        add(restsTable);
        
        UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
        PopupPanel popup = new PopupPanel(false);
        popup.setWidget(new Label("Loading..."));
        popup.center();
        //TODO deal "with" this!!!!
        service.getDefaultRestaurants().with("menu","menu.categories","iconBytes","branches","branches.menu","branches.menu.categories","branches.menu.categories.courses","admins").fire(new GetDefaultRestaurantsReceiver(popup));
    }
    
    private void redraw(List<RestaurantProxy> rests)
    {
        restsTable.removeAllRows();
        printRestaurantsTableHeader();
        
        int row = restsTable.getRowCount();
        for (RestaurantProxy r : rests)
        {
            printRestaurantRow(r, row);
            row++;
        }
    }
    
    private void printRestaurantRow(RestaurantProxy rest, int row)
    {
        // set image here
        
        String name = rest.getName();
        restsTable.setText(row, 1, name);
        
        String delivery =  rest.getServices().contains(ServiceType.DELIVERY) ? "yes" : "no";
        restsTable.setText(row, 2, delivery + i);
        
        String takeAway =  rest.getServices().contains(ServiceType.TAKE_AWAY) ? "yes" : "no";
        restsTable.setText(row, 3, takeAway);
        
        String table =  rest.getServices().contains(ServiceType.TABLE) ? "yes" : "no";
        restsTable.setText(row, 4, table);
        
        Button edit = new Button("edit");
        edit.addClickHandler(new OnClickUpdateRestaurant(rest, row));
        //TODO edit restaurnt button
        restsTable.setWidget(row, 5, edit);
        
        Button delete = new Button("X");
        //TODO edit restaurnt button
        restsTable.setWidget(row, 6, delete);
        
        ++i;
        
    }
    
    private void printRestaurantsTableHeader()
    {
        restsTable.setText(0, 0, "Image");
        restsTable.setText(0, 1, "Name");
        restsTable.setText(0, 2, "Delivery");
        restsTable.setText(0, 3, "Take Away");
        restsTable.setText(0, 4, "Table");
        
        Button newButton = new Button("New");
        newButton.addClickHandler(new NewRestaurantClickHandler());
        restsTable.setWidget(0, 5, newButton);
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
            Window.alert("[FAIL] service connection error: " + error.getMessage());
        }
    }
    
    private class NewRestaurantClickHandler implements ClickHandler
    {

        
        @Override
        public void onClick(ClickEvent event)
        {
            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
            
            RestaurantProxy rest = service.create(RestaurantProxy.class);
            
            PopupPanel popup = new PopupPanel(false);
            
            int row = restsTable.getRowCount();
            Runnable onSave = new OnClickSaveRestaurant(service, popup, rest, row);
            Runnable onDiscard = new OnDiscardRestaurant(popup);
         
            RestaurantPanel restPanel = new RestaurantPanel(service , rest, isAdmin, onSave, onDiscard);
            
            popup.setWidget(restPanel);
            popup.show();
            
        }
        
    }
    
    private class OnClickUpdateRestaurant implements ClickHandler
    {

        private final RestaurantProxy rest;
        private final int row;
        
        public OnClickUpdateRestaurant(RestaurantProxy rest, int row)
        {
            this.rest = rest;
            this.row = row;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
            
            PopupPanel popup = new PopupPanel(false);
            
            RestaurantProxy editable = service.edit(rest);
            
            Runnable onSave = new OnClickSaveRestaurant(service, popup, rest, row);
            Runnable onDiscard = new OnDiscardRestaurant(popup);
            
            RestaurantPanel restPanel = new RestaurantPanel(service , editable, isAdmin, onSave, onDiscard);
            
            popup.setWidget(restPanel);
            popup.show();
            
        }
        
    }
    
    
    private class OnClickSaveRestaurant implements Runnable
    {
        private final RequestContext requestContext;
        private final PopupPanel popup;
        private final RestaurantProxy rest;
        private final int row;

        public OnClickSaveRestaurant(RequestContext requestContext, PopupPanel popup, RestaurantProxy rest, int row)
        {
            this.requestContext = requestContext;
            this.popup = popup;
            this.rest = rest;
            this.row = row;
        }

        @Override
        public void run()
        {
            popup.hide();
            
            PopupPanel loading = new PopupPanel(false);
            loading.setWidget(new Label("Loading..."));
            loading.center();
            
            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
            requestContext.append(service);
            service.saveRestaurant(rest).to(new SaveRestaurantReciever(loading, rest, row));
            requestContext.fire();

        }

    }

    class SaveRestaurantReciever extends Receiver<RestaurantProxy>
    {
        private final PopupPanel loading;
        private final RestaurantProxy rest;
        private final int row;
        
        public SaveRestaurantReciever(PopupPanel loading, RestaurantProxy rest, int row)
        {
            this.loading = loading;
            this.rest = rest;
            this.row = row;
        }
        
        @Override
        public void onSuccess(RestaurantProxy response)
        {
            loading.removeFromParent();
            printRestaurantRow(rest, row);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            loading.hide();
            Window.alert("exception: " + error.getMessage());
        }

    }

    private class OnDiscardRestaurant implements Runnable
    {
        private final PopupPanel popup;

        public OnDiscardRestaurant(PopupPanel popup)
        {
            this.popup = popup;
        }

        @Override
        public void run()
        {
            popup.removeFromParent();

        }

    }

}
