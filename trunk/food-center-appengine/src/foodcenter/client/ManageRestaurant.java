package foodcenter.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;

public class ManageRestaurant implements EntryPoint
{
	private static final String GWT_CONTINER = "gwtContainer";
	private FoodCenterRequestFactory requestFactory = new RequestUtils().getRequestFactory();
	
	/**************************************************************************
     * Data Objects
     **************************************************************************/
//	private RestaurantProxy rest = null;
    
    /**************************************************************************
     * Panels                                                                   
     **************************************************************************/
    private Panel mainPanel = new VerticalPanel();
    
	@Override
    public void onModuleLoad()
    {
	    
	    RestaurantProxy rest = null;
	    
	    //if new :
	    rest = requestFactory.getUserCommonService().create(RestaurantProxy.class);
	    //else edit:
	    //TODO rest = edit rest ?
	    
	    buildMainPanel(rest);
    }
	
	private void buildMainPanel(RestaurantProxy rest)
	{
	    Button saveButton = new Button("save");
        saveButton.addClickHandler(new SaveRestClickHandler(rest));
        mainPanel.add(saveButton);
        
        Button deleteButton = new Button("delete");
        saveButton.addClickHandler(new DeleteRestClickHandler(rest));
        mainPanel.add(saveButton);
        
        Panel stackPanel = createStackPanel(rest);
        mainPanel.add(stackPanel);
        RootPanel.get(ManageRestaurant.GWT_CONTINER).add(mainPanel);
	}
	
	private Panel createStackPanel(RestaurantProxy rest)
	{
        StackPanel stackPanel = new StackPanel();
        
        //profile pannel
        Panel profilePanel = createProfilePannel(rest);
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

    private void setNotNullText(ValueBoxBase<String> w, String s)
    {
        if (null != s)
        {
            w.setText(s);
        }
    }
    
    private Panel createProfilePannel(RestaurantProxy rest)
	{
        
//        MenuProxy menuProxy = rest.getMenu();
//        if (null == menuProxy)
//        {
//            menuProxy = requestFactory.getUserCommonService().create(MenuProxy.class);
//            rest.setMenu(menuProxy);
//        }
//        return new MenuFlexTable(requestFactory, menuProxy);
	    HorizontalPanel profile = new HorizontalPanel();
	    Image image = RequestUtils.getImage(rest.getIconBytes()); //TODO get image bytes;
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
	    setNotNullText(nameBox, rest.getName());

        name.add(nameBox);
	    info.add(name);
	    
	    //creates the phone panel with restaurant phone
        HorizontalPanel phone = new HorizontalPanel();
        phone.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        setNotNullText(phoneBox, rest.getPhone());
        phone.add(phoneBox);
        info.add(phone);
       
        
        HorizontalPanel services = new HorizontalPanel();
        
        CheckBox deliveryCheckBox = new CheckBox("delivery");
        //deliveryCheckBox.setValue(rest.getServices().contains(ServiceType.DELIVERY));
        //TODO click handler
        
        CheckBox takeAwayCheckBox = new CheckBox("take away");
        //takeAwayCheckBox.setValue(rest.getServices().contains(ServiceType.TAKE_AWAY));
        //TODO click handler
        
        CheckBox tableCheckBox = new CheckBox("table");
        //tableCheckBox.setValue(rest.getServices().contains(ServiceType.TABLE));
        //TODO click handler
        
        services.add(deliveryCheckBox);
        services.add(takeAwayCheckBox);
        services.add(tableCheckBox);
	    info.add(services);
	    
	    profile.add(info);
	    
	    
	    
	    return profile;
	}
	
	private Panel createMenuPannel(RestaurantProxy rest)
    {
       //create the menu pannel
	    //HorizontalPanel menuPannel = new HorizontalPanel();
             
        //creates the name panel with restaurant name
        FlexTable menu = new FlexTable();
        
        menu.setText(0, 0, "categories");
        Button addCatButton = new Button("Add Category");
        addCatButton.addClickHandler(new AddCategoryClickHandler(menu));
        //TODO add click handler
        
        menu.setWidget(0, 1, addCatButton);
        
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
        
        return menu;
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
            requestFactory.getUserCommonService().saveRestaurant(this.rest);
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
            requestFactory.getUserCommonService().deleteRestaurant(this.rest.getId());
        }
    }

	class AddCategoryClickHandler implements ClickHandler 
    {
	    private final FlexTable menu;
	    
	    public AddCategoryClickHandler(FlexTable menu)
	    {
	        this.menu = menu;
	        
	    }
        @Override
        public void onClick(ClickEvent event)
        {
            int row = menu.getRowCount();
            TextBox categoryName = new TextBox();
            FlexTable coursesTable = new FlexTable();
            if (coursesTable.getRowCount()==0){
                coursesTable.setText(0, 0, "name");
                coursesTable.setText(0, 1, "price");
                Button addCourseButton = new Button();
                addCourseButton.setText("Add Course"); //TODO add click handler    
            }
//            addCourseButton.addClickHandler(new AddCourseClickHandler(coursesTable));
//            coursesTable.setWidget(0, 2, addCourseButton);

            menu.setWidget(row, 0, categoryName);
            menu.setWidget(row, 1, coursesTable);
            
        }
    }
	
	class AddCourseClickHandler implements ClickHandler 
    {
        private final FlexTable coursesTable;
        
        public AddCourseClickHandler(FlexTable coursesTable)
        {
            this.coursesTable = coursesTable;
            
        }
        @Override
        public void onClick(ClickEvent event)
        {
            int row = coursesTable.getRowCount();
            TextBox categoryName = new TextBox();
            
            FlexTable categoryTable = new FlexTable();
            categoryTable.setText(0, 0, "name");
            categoryTable.setText(0, 1, "price");
            Button addCourseButton = new Button();
            addCourseButton.setText("Add Course"); //TODO add click handler
//            addCourseButton.addClickHandler(handler);
            categoryTable.setWidget(0, 2, addCourseButton);
//            
//            for (int i=1; i< 4; ++i)
//            {
//                TextBox nameTB = new TextBox();
//                nameTB.setText("course" + i);
//                //TODO set handler
//                TextBox priceTB = new TextBox();
//                priceTB.setText("1000");
//                //TODO set handler
//                Button delete = new Button();
//                delete.setText("delete");
//                //TODO set handler
//                categoryTable.setWidget(i, 0, nameTB);
//                categoryTable.setWidget(i, 1, priceTB);
//                categoryTable.setWidget(i, 2, delete);
//            }
//            
            coursesTable.setWidget(row, 0, categoryName);
            coursesTable.setWidget(row, 1, categoryTable);
            
        }
    }
}
