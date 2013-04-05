package foodcenter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantProxy;

public class ManageRestaurant implements EntryPoint
{
	private static final String GWT_CONTINER = "gwtContainer";
	private FoodCenterRequestFactory requestFactory = new RequestUtils().getRequestFactory();
	
	/**************************************************************************
     * Data Objects
     **************************************************************************/
    private ArrayList<RestaurantProxy> restaurants = new ArrayList<RestaurantProxy>(); 
    
    /**************************************************************************
     * Panels                                                                   
     **************************************************************************/
    private StackPanel mainStackPanel = new StackPanel();
    
    //profile pannel
    private Panel profilePanel = createProfilePannel();
    private Panel menuPanel = createMenuPannel();
    private Panel adminsPanel = createAdminPannel();
    private Panel waitersPanel = createWaitersPannel();
    private Panel chefsPanel = createChefsPannel();
    private Panel tablesPanel = createTablesPannel();
    private Panel branchesPanel = createBranchesPannel();
    private Panel ordersPanel = createOrdersPannel();
    
    
    
	@Override
    public void onModuleLoad()
    {
	    
	    
	    
	    
	    // TODO fix order
	    mainStackPanel.add(profilePanel, "Profile");
	    mainStackPanel.add(menuPanel, "Menu");
	    mainStackPanel.add(adminsPanel, "Admins");
	    mainStackPanel.add(waitersPanel, "Waiters");
	    mainStackPanel.add(chefsPanel, "Chefs");
	    mainStackPanel.add(tablesPanel, "Tables");
	    mainStackPanel.add(branchesPanel, "Braches");
	    mainStackPanel.add(ordersPanel, "Orders");
	    
		RootPanel.get(ManageRestaurant.GWT_CONTINER).add(mainStackPanel);
    }
	
	private Panel createWaitersPannel()
    {
        // TODO Auto-generated method stub
	    VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createChefsPannel()
    {
        // TODO Auto-generated method stub
        VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createTablesPannel()
    {
        // TODO Auto-generated method stub
        VerticalPanel panel = new VerticalPanel();
        return panel;
    }

    private Panel createBranchesPannel()
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }

    private Panel createOrdersPannel()
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }

    private Panel createAdminPannel()
    {
        // TODO Auto-generated method stub
        VerticalPanel profile = new VerticalPanel();
        return profile;
    }

    private Panel createProfilePannel()
	{
	    HorizontalPanel profile = new HorizontalPanel();
	    Image image = RequestUtils.getImage((List<Byte>)null); //TODO get image bytes;
	    if (null != image)
	    {
	        profile.add(image);
	    }
	    
	    //create the information pannel
	    VerticalPanel info = new VerticalPanel();
	    
	    //creates the name panel with restaurant name
	    HorizontalPanel name = new HorizontalPanel();
	    name.add(new Label("Name: "));
	    TextBox nameBox = new TextBox();
        nameBox.setText("Mock name"); //TODO get from restaurant
        name.add(nameBox);
	    info.add(name);
	    
	    //creates the phone panel with restaurant phone
        HorizontalPanel phone = new HorizontalPanel();
        phone.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        phoneBox.setText("0433333"); //TODO get from restaurant
        phone.add(phoneBox);
        info.add(phone);
       
        
        HorizontalPanel services = new HorizontalPanel();
        CheckBox deliveryCheckBox = new CheckBox("delivery");
        CheckBox takeAwayCheckBox = new CheckBox("take away");
        CheckBox tableCheckBox = new CheckBox("table");
        //TODO click handlers
        services.add(deliveryCheckBox);
        services.add(takeAwayCheckBox);
        services.add(tableCheckBox);
	    info.add(services);
	    
	    profile.add(info);
	    
	    
	    
	    return profile;
	}
	
	private Panel createMenuPannel()
    {
       //create the menu pannel
	    HorizontalPanel menuPannel = new HorizontalPanel();
             
        //creates the name panel with restaurant name
        FlexTable menu = new FlexTable();
        menu.setText(0, 0, "categories");
        menu.setText(0, 1, "menu");
        menu.setText(0, 2, "addCategory Button");
        
        for (int j=1 ; j< 4; ++j)
        {
            FlexTable courses = new FlexTable();
            courses.setText(0, 0, "name");
            courses.setText(0, 1, "price");
            Button addCourseButton = new Button();
            addCourseButton.setText("Add Course");
            courses.setWidget(0, 2, addCourseButton);
            
            for (int i=1; i< 4; ++i)
            {
                TextBox nameTB = new TextBox();
                nameTB.setText("course" + i);
                //TODO set handler
                TextBox priceTB = new TextBox();
                priceTB.setText("1000");
                //TODO set handler
                Button delete = new Button();
                delete.setText("delete");
                //TODO set handler
                courses.setWidget(i, 0, nameTB);
                courses.setWidget(i, 1, priceTB);
                courses.setWidget(i, 2, delete);
            }
            menu.setText(j, 0, "category" + j);
            menu.setWidget(j, 1, courses);
        }
        
	    menuPannel.add(menu);
        
        return menuPannel;
    }

}
