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
import foodcenter.client.panels.GoogleMapsComposite;
import foodcenter.service.proxies.RestaurantBranchProxy;

public class RestaurantBranchLocationVerticalPanel extends VerticalPanel
{
    private final RestaurantBranchProxy branchPrxoy;
    
    private final HorizontalPanel hPanel; // holds all the text boxes and buttons
    
    private final TextBox lat;
    private final TextBox lng;
    private final TextBox address;
    private final Button setAddrButton;
    
    private GoogleMapsComposite map;
    
    public RestaurantBranchLocationVerticalPanel(RestaurantBranchProxy branchPrxoy)
    {
        super();
        
        this.branchPrxoy = branchPrxoy;
        
        if (null == branchPrxoy.getLat())
        {
            branchPrxoy.setLat(ClientUtils.GOOGLE_API_DEFAULT_LAT);
        }
        
        if (null == branchPrxoy.getLng())
        {
            branchPrxoy.setLng(ClientUtils.GOOGLE_API_DEFAULT_LNG);
        }
        if (null == branchPrxoy.getAddress())
        {
            branchPrxoy.setAddress(ClientUtils.GOOGLE_API_DEFAULT_ADDR);
        }
        
        this.lat = new TextBox();
        this.lat.setEnabled(false);
        
        this.lng = new TextBox();
        this.lng.setEnabled(false);
        
        this.address = new TextBox();
        this.setAddrButton = new Button("set");
        
        this.hPanel = new HorizontalPanel();
        this.hPanel.add(new Label("Address:"));
        this.hPanel.add(address);
        this.hPanel.add(new Label("lat:"));
        this.hPanel.add(lat);
        this.hPanel.add(new Label("lng:"));
        this.hPanel.add(lng);
        
        // add the button 
        this.hPanel.add(setAddrButton);
        
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
        map = new GoogleMapsComposite(branchPrxoy, address, lat, lng);
        
        // set the search button/ enter click
        setAddrButton.addClickHandler(new SetAddressClickHandler());
        address.addKeyUpHandler(new SetAddressKeyUpHandler());
        
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
