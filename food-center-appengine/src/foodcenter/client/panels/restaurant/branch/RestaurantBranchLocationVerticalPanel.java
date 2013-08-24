package foodcenter.client.panels.restaurant.branch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.ClientUtils;
import foodcenter.client.panels.common.GoogleMapsComposite;
import foodcenter.service.proxies.RestaurantBranchProxy;

public class RestaurantBranchLocationVerticalPanel extends VerticalPanel
{
    private final RestaurantBranchProxy branchPrxoy;
    private final boolean isEditMode;
    
    private final HorizontalPanel hPanel; // holds all the text boxes and buttons
    
    private final TextBox lat;
    private final TextBox lng;
    private final TextBox address;
    private final Button setAddrButton;
    
    private GoogleMapsComposite map;
    
    public RestaurantBranchLocationVerticalPanel(RestaurantBranchProxy branchPrxoy, boolean isEditMode)
    {
        super();
        
        this.branchPrxoy = branchPrxoy;
        this.isEditMode = isEditMode;
        
        setWidth("100%");
        
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
            Maps.loadMapsApi(ClientUtils.GOOGLE_API_MAPS_KEY, ClientUtils.GOOGLE_API_MAPS_VER, false, new OnApiLoadRunnable());
        }
        else
        {
            loadMap();
        }
        
    }
    
    private void loadMap()
    {
        map = new GoogleMapsComposite(branchPrxoy, address, lat, lng, isEditMode);
                
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

}
