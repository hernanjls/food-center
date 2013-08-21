package foodcenter.client.panels.main;
//package foodcenter.client.panels;
//
//import java.util.List;
//
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.Panel;
//import com.google.gwt.user.client.ui.PopupPanel;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.web.bindery.requestfactory.shared.Receiver;
//import com.google.web.bindery.requestfactory.shared.ServerFailure;
//
//import foodcenter.client.panels.RestaurantsListPanel.GetDefaultRestaurantsReceiver;
//import foodcenter.client.panels.RestaurantsListPanel.OnClickNewRestayrant;
//import foodcenter.client.panels.RestaurantsListPanel.OnClickSaveRestaurant;
//import foodcenter.client.panels.RestaurantsListPanel.OnClickUpdateRestaurant;
//import foodcenter.client.panels.RestaurantsListPanel.OnDiscardRestaurant;
//import foodcenter.client.panels.RestaurantsListPanel.SaveRestaurantReciever;
//import foodcenter.client.service.RequestUtils;
//import foodcenter.service.UserCommonServiceProxy;
//import foodcenter.service.enums.ServiceType;
//import foodcenter.service.proxies.CompanyProxy;
//import foodcenter.service.proxies.RestaurantProxy;
//
//public class CompaniesListPanelextends extends VerticalPanel
//{
//
//    private final boolean isAdmin;
//    static int i = 0;
//    private final Panel hPanel;
//    private final FlexTable restsTable;
//
//    public CompaniesListPanel(boolean isAdmin)
//    {
//        super();
//        this.isAdmin = isAdmin;
//
//        hPanel = createOptionsHorizonalPannel();
//        add(hPanel);
//        reloadCompanies();
//    }
//
//    private void reloadCompanies()
//    {
//        UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
//        PopupPanel popup = new PopupPanel(false);
//        popup.setWidget(new Label("Loading..."));
//        popup.center();
//        // TODO deal "with" this!!!!
//        service.getDefaultRestaurants().with(
//                                             "iconBytes",   //
//                                             "branches",    //
//                                             "admins",
//                                             "employees")  //
//                                             .fire(new GetDefaultCompaniesReceiver(popup));
//    }
//
//    private void redraw(List<RestaurantProxy> rests)
//    {
//        restsTable.removeAllRows();
//        printRestaurantsTableHeader();
//
//        int row = restsTable.getRowCount();
//        for (RestaurantProxy r : rests)
//        {
//            printRestaurantRow(r, row);
//            row++;
//        }
//    }
//
//    private void printRestaurantRow(RestaurantProxy rest, int row)
//    {
//        // set image here
//
//        String name = rest.getName();
//        restsTable.setText(row, 1, name);
//
//        String delivery = rest.getServices().contains(ServiceType.DELIVERY) ? "yes" : "no";
//        restsTable.setText(row, 2, delivery + i);
//
//        String takeAway = rest.getServices().contains(ServiceType.TAKE_AWAY) ? "yes" : "no";
//        restsTable.setText(row, 3, takeAway);
//
//        String table = rest.getServices().contains(ServiceType.TABLE) ? "yes" : "no";
//        restsTable.setText(row, 4, table);
//
//        Button edit = new Button("edit");
//        edit.addClickHandler(new OnClickUpdateRestaurant(rest, row));
//        // TODO edit restaurnt button
//        restsTable.setWidget(row, 5, edit);
//
//        Button delete = new Button("X");
//        // TODO edit restaurnt button
//        restsTable.setWidget(row, 6, delete);
//
//        ++i;
//
//    }
//
//    private void printRestaurantsTableHeader()
//    {
//        restsTable.setText(0, 0, "Image");
//        restsTable.setText(0, 1, "Name");
//        restsTable.setText(0, 2, "Delivery");
//        restsTable.setText(0, 3, "Take Away");
//        restsTable.setText(0, 4, "Table");
//
//        Button newButton = new Button("New");
//        newButton.addClickHandler(new OnClickNewRestayrant());
//        restsTable.setWidget(0, 5, newButton);
//    }
//
//    private Panel createOptionsHorizonalPannel()
//    {
//        TextBox searchBox = new TextBox();
//
//        CheckBox delivery = new CheckBox("delivery");
//        delivery.setValue(true);
//
//        CheckBox takeAway = new CheckBox("take away");
//        takeAway.setValue(true);
//
//        CheckBox table = new CheckBox("table");
//        table.setValue(true);
//
//        Button searchButton = new Button("Search");
//
//        HorizontalPanel hPanel = new HorizontalPanel();
//        hPanel.add(searchBox);
//        hPanel.add(delivery);
//        hPanel.add(takeAway);
//        hPanel.add(table);
//        hPanel.add(searchButton);
//
//        return hPanel;
//    }
//
//    private class GetDefaultCompaniesReceiver extends Receiver<List<CompanyProxy>>
//    {
//        private final PopupPanel popup;
//
//        public GetDefaultCompaniesReceiver(PopupPanel popup)
//        {
//            this.popup = popup;
//        }
//
//        @Override
//        public void onSuccess(List<CompanyProxy> response)
//        {
//            redraw(response);
//            popup.removeFromParent();
//        }
//
//        @Override
//        public void onFailure(ServerFailure error)
//        {
//            Window.alert("[FAIL] service connection error: " + error.getMessage());
//        }
//    }
//
//    private class OnClickNewRestayrant implements ClickHandler
//    {
//
//        @Override
//        public void onClick(ClickEvent event)
//        {
//            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
//
//            CompanyProxy comp = service.create(CompanyProxy.class);
//
//            PopupPanel popup = new PopupPanel(false);
//
//            int row = compsTable.getRowCount();
//            Runnable onSave = new OnClickSaveRestaurant(service, popup, comp, row);
//            Runnable onDiscard = new OnDiscardRestaurant(popup);
//
//            CompanyPanel compPanel = new RestaurantPanel(service, comp, isAdmin, onSave, onDiscard);
//
//            popup.setWidget(compPanel);
//            popup.show();
//
//        }
//
//    }
//
//    private class OnClickUpdateCompany implements ClickHandler
//    {
//
//        private final CompanyProxy comp;
//        private final int row;
//
//        public OnClickUpdateRestaurant(CompanyProxy comp, int row)
//        {
//            this.comp = comp;
//            this.row = row;
//        }
//
//        @Override
//        public void onClick(ClickEvent event)
//        {
//            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService();
//
//            PopupPanel popup = new PopupPanel(false);
//
//            CompanyProxy editable = service.edit(comp);
//
//            Runnable onSave = new OnClickSaveCompany(service, popup, comp, row);
//            Runnable onDiscard = new OnDiscardCompany(popup);
//
//            CompanyPanel compPanel = new CompanyPanel(service, editable, isAdmin, onSave, onDiscard);
//
//            popup.setWidget(compPanel);
//            popup.show();
//
//        }
//
//    }
//
//    private class OnClickSaveCompany implements Runnable
//    {
//        private final UserCommonServiceProxy requestContext;
//        private final PopupPanel popup;
//        private final CompanyProxy comp;
//        private final int row;
//
//        public OnClickSaveCompany(UserCommonServiceProxy requestContext, PopupPanel popup, CompanyProxy comp, int row)
//        {
//            this.requestContext = requestContext;
//            this.popup = popup;
//            this.comp = comp;
//            this.row = row;
//        }
//
//        @Override
//        public void run()
//        {
//            popup.hide();
//
//            PopupPanel loading = new PopupPanel(false);
//            loading.setWidget(new Label("Loading..."));
//            loading.center();
//
//            // UserCommonServiceProxy service =
//            // RequestUtils.getRequestFactory().getUserCommonService();
//            // requestContext.append(service);
//            // service.saveRestaurant(rest).to(new SaveRestaurantReciever(loading, rest, row));
//            // requestContext.fire();
//            requestContext.saveRestaurant(rest).fire(new SaveRestaurantReciever(loading, rest, row));
//
//        }
//
//    }
//
//    class SaveRestaurantReciever extends Receiver<RestaurantProxy>
//    {
//        private final PopupPanel loading;
//        private final RestaurantProxy rest;
//        private final int row;
//
//        public SaveRestaurantReciever(PopupPanel loading, RestaurantProxy rest, int row)
//        {
//            this.loading = loading;
//            this.rest = rest;
//            this.row = row;
//        }
//
//        @Override
//        public void onSuccess(RestaurantProxy response)
//        {
//            loading.removeFromParent();
//            reloadRestaurants();
//        }
//
//        @Override
//        public void onFailure(ServerFailure error)
//        {
//            loading.hide();
//            Window.alert("exception: " + error.getMessage());
//        }
//
//    }
//
//    private class OnDiscardRestaurant implements Runnable
//    {
//        private final PopupPanel popup;
//
//        public OnDiscardRestaurant(PopupPanel popup)
//        {
//            this.popup = popup;
//        }
//
//        @Override
//        public void run()
//        {
//            popup.removeFromParent();
//
//        }
//
//    }
//
//}
