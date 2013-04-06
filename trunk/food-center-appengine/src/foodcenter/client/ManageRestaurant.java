package foodcenter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.panels.restaurant.MenuFlexTable;
import foodcenter.client.panels.restaurant.ProfilePannel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.UserCommonServiceProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantProxy;

public class ManageRestaurant implements EntryPoint
{
	private static final String GWT_CONTINER = "gwtContainer";
	private FoodCenterRequestFactory reqFactory = new RequestUtils().getRequestFactory();
	private UserCommonServiceProxy userCommonService = reqFactory.getUserCommonService(); 
	
	/**************************************************************************
     * Data Objects
     **************************************************************************/
//	private RestaurantProxy rest = null;
	
	private Boolean isAdmin; 
    
    /**************************************************************************
     * Panels                                                                   
     **************************************************************************/
    private Panel mainPanel = new VerticalPanel();
    
	@Override
    public void onModuleLoad()
    {
	    isAdmin = true;
	    RestaurantProxy rest = null;
	    
	    //if new :
	    rest = userCommonService.create(RestaurantProxy.class);
	    //else edit:
	    //TODO rest = edit rest ?
	    
	    buildMainPanel(rest);
    }
	
	private void buildMainPanel(RestaurantProxy rest)
	{
		HorizontalPanel hpanel = new HorizontalPanel();
	    
		Button saveButton = new Button("save");
        saveButton.addClickHandler(new SaveRestClickHandler(rest));
        saveButton.setEnabled(isAdmin);
        hpanel.add(saveButton);
        
        Button deleteButton = new Button("delete");
        deleteButton.addClickHandler(new DeleteRestClickHandler(rest));
        deleteButton.setEnabled(isAdmin);
        hpanel.add(deleteButton);
        
        mainPanel.add(hpanel);
        
        Panel stackPanel = createStackPanel(rest);
        mainPanel.add(stackPanel);
        RootPanel.get(ManageRestaurant.GWT_CONTINER).add(mainPanel);
	}
	
	private Panel createStackPanel(RestaurantProxy rest)
	{
        StackPanel stackPanel = new StackPanel();
        
        //profile pannel
        Panel profilePanel = new ProfilePannel(rest, isAdmin);
        Panel menuPanel = createMenuPannel(rest);
        Panel adminsPanel = createAdminPannel(rest);
        Panel waitersPanel = createWaitersPannel(rest);
        Panel chefsPanel = createChefsPannel(rest);
        Panel tablesPanel = createTablesPannel(rest);
        Panel branchesPanel = createBranchesPannel(rest);
        Panel ordersPanel = createOrdersPannel(rest);
        
        
        // TODO fix order
        stackPanel.add(profilePanel, "Profile");
        stackPanel.add(menuPanel, "Menu");
        stackPanel.add(adminsPanel, "Admins");
        stackPanel.add(waitersPanel, "Waiters");
        stackPanel.add(chefsPanel, "Chefs");
        stackPanel.add(tablesPanel, "Tables");
        stackPanel.add(branchesPanel, "Braches");
        stackPanel.add(ordersPanel, "Orders");

//      requestFactory.getUserCommonService().getRestaurant(0L); //TODO get id from somewhere
        
        return stackPanel;

	}
	private Panel createWaitersPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
	    VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createChefsPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
        VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createTablesPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
        VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createBranchesPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }

    private Panel createOrdersPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }

    private Panel createAdminPannel(RestaurantProxy rest)
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }
	
	private Panel createMenuPannel(RestaurantProxy rest)
    {
		MenuProxy menuProxy = rest.getMenu();
		if (null == menuProxy)
		{
			menuProxy = userCommonService.create(MenuProxy.class);
			rest.setMenu(menuProxy);
		}
		return new MenuFlexTable(userCommonService, menuProxy, isAdmin);
    }
	
	
	class SaveRestClickHandler implements ClickHandler
    {
	    private final RestaurantProxy rest;
	    
	    public SaveRestClickHandler(RestaurantProxy rest)
	    {
	        this.rest = rest;
	    }
	    
        @Override
        public void onClick(ClickEvent event)
        {
        	userCommonService
        		.saveRestaurant(this.rest)
        		.fire(new AddRestaurantReciever());
        }
    }
	
	class DeleteRestClickHandler implements ClickHandler
    {
        private final RestaurantProxy rest;
        
        public DeleteRestClickHandler(RestaurantProxy rest)
        {
            this.rest = rest;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
        	userCommonService
            	.deleteRestaurant(this.rest.getId())
            	.fire(new DeleteRestaurantReciever());
        }
    }
	
	class AddRestaurantReciever extends Receiver<Boolean>
	{

		@Override
        public void onSuccess(Boolean response)
        {
			Window.Location.replace("/food_center.jsp");
        }
		
		@Override
		public void onFailure(ServerFailure error)
		{
		    Window.alert("exception: " + error.getMessage());
		}
		
	}

	class DeleteRestaurantReciever extends Receiver<Boolean>
	{

		@Override
        public void onSuccess(Boolean response)
        {
			Window.alert("deleted!!!");
        }
		
		@Override
		public void onFailure(ServerFailure error)
		{
		    Window.alert("exception: " + error.getMessage());
		}
		
	}

}
