package foodcenter.client.panels.restaurant.branch;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.WebClientUtils;
import foodcenter.client.callbacks.OnClickServiceCheckBox;
import foodcenter.client.panels.common.GoogleMapsComposite;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantBranchProxy;

public class RestaurantBranchLocationVerticalPanel extends VerticalPanel
{
    private final RestaurantBranchProxy branch;
    private final boolean isEditMode;

    private final HorizontalPanel profilePanel; // holds phone and services
    private final HorizontalPanel hPanel; // holds all the text boxes and buttons for map
    
    private final TextBox lat;
    private final TextBox lng;
    private final TextBox address;
    private final Button setAddrButton;
    
    private GoogleMapsComposite map;
    
    public RestaurantBranchLocationVerticalPanel(RestaurantBranchProxy branch, boolean isEditMode)
    {
        super();
        
        this.branch = branch;
        this.isEditMode = isEditMode;
        
        setWidth("100%");
        
        profilePanel = createProfileInfoPanel();
        
        
        hPanel = new HorizontalPanel();
        
        address = new TextBox();
        address.setEnabled(false);
        hPanel.add(new Label("Address:"));
        hPanel.add(address);
        
        lat = new TextBox();
        lat.setEnabled(false);
        hPanel.add(new Label("lat:"));
        hPanel.add(lat);
        
        lng = new TextBox();
        lng.setEnabled(false);
        hPanel.add(new Label("lng:"));
        hPanel.add(lng);
        
        // add the set addr button
        setAddrButton = new Button("set");
        
        if (isEditMode)
        {   
            address.addKeyUpHandler(new SetAddressKeyUpHandler());
            address.setEnabled(true);
            
            setAddrButton.addClickHandler(new SetAddressClickHandler());
            hPanel.add(setAddrButton);
        }
        
        // add the map and the pannels
        if (!Maps.isLoaded())
        {
            Maps.loadMapsApi(WebClientUtils.BROWSER_API_KEY_MAPS, WebClientUtils.GOOGLE_API_MAPS_VER, false, new OnApiLoadRunnable());
        }
        else
        {
            loadMap();
        }
    }
    
    private HorizontalPanel createProfileInfoPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        res.add(createPhonePanel());
        res.add(createServicesPanel());

        return res;
    }
    
    private void loadMap()
    {
        map = new GoogleMapsComposite(branch, address, lat, lng, isEditMode);
        add(profilePanel);        
        add(hPanel);        
        add(map);
    }
    
    private class OnApiLoadRunnable implements Runnable
    {
        @Override
        public void run()
        {
            // create the map
            loadMap();
        }
    }
    
    
    private class SetAddressClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            map.updateMarkerByAddress();
        }
    }
    
    private class SetAddressKeyUpHandler implements KeyUpHandler
    {

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                map.updateMarkerByAddress();
            }
        }
        
    }

    
    private Panel createPhonePanel()
    {
        // creates the phone panel with restaurant phone
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        WebClientUtils.setNotNullText(phoneBox, branch.getPhone());
        phoneBox.addKeyUpHandler(new PhoneKeyUpHandler(phoneBox));
        phoneBox.setEnabled(isEditMode);
//        phoneBox.setReadOnly(!isEditMode);
        res.add(phoneBox);

        return res;
    }

    private Panel createServicesPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        CheckBox deliveryCheckBox = createServiceCheckBox(ServiceType.DELIVERY.getName());
        res.add(deliveryCheckBox);

        CheckBox takeAwayCheckBox = createServiceCheckBox(ServiceType.TAKE_AWAY.getName());
        res.add(takeAwayCheckBox);

        CheckBox tableCheckBox = createServiceCheckBox(ServiceType.TABLE.getName());
        res.add(tableCheckBox);

        return res;
    }

    private CheckBox createServiceCheckBox(String name)
    {
        CheckBox res = new CheckBox(name);
        List<ServiceType> services = branch.getServices();

        ServiceType service = ServiceType.forName(name);
        boolean value = services.contains(service);
        res.setValue(value);

        if (isEditMode)
        {
            res.addClickHandler(new OnClickServiceCheckBox(branch.getServices()));
        }
        res.setEnabled(isEditMode);

        return res;
    }
    
    
    
    private class PhoneKeyUpHandler implements KeyUpHandler
    {
        private final TextBox titleBox;

        public PhoneKeyUpHandler(TextBox titleBox)
        {
            this.titleBox = titleBox;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            branch.setPhone(titleBox.getText());
        }

    }


}
