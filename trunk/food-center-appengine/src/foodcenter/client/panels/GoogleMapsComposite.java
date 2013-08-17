package foodcenter.client.panels;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.service.proxies.interfaces.AbstractGeoLocationInterface;

/**
 * 
 * @reference 
 *            http://stackoverflow.com/questions/4320992/getting-location-details-from-google-map-in-
 *            gwt
 */
public class GoogleMapsComposite extends Composite
{
    private final AbstractGeoLocationInterface proxy;
    private final TextBox addressBox;
    private final TextBox latBox;
    private final TextBox lngBox;
    private final boolean isEditMode;
    
    private MapWidget map;
    private Geocoder geoCoder;
    private Marker marker;

    /**
     * @param address - is the textbox which holds the address, will be modified on marker movement
     * @param latBox - is the textbox to add the value to
     * @param lngBox - is the textbox to add the value to
     * @param lat - is the default lat to load with
     * @param lng - is the default lng to load with
     */
    public GoogleMapsComposite(AbstractGeoLocationInterface proxy,
                               TextBox address,
                               TextBox latBox,
                               TextBox lngBox,
                               boolean isEditMode)
    {
        this.proxy = proxy;
        this.addressBox = address;
        this.latBox = latBox;
        this.lngBox = lngBox;
        this.isEditMode = isEditMode;

        LatLng center = LatLng.newInstance(proxy.getLat(), proxy.getLng());

        this.geoCoder = new Geocoder();

        MarkerOptions options = MarkerOptions.newInstance();
        options.setDraggable(true);
        marker = new Marker(center, options);
        marker.setVisible(true);
        if (isEditMode)
        {
            marker.addMarkerDragEndHandler(new GoogleMapMarkerDragEndHandler());
        }
        else
        {
            marker.setDraggingEnabled(false);
        }

        map = new MapWidget(center, 6);
        map.setSize("100%", "350px");
        map.addOverlay(marker);
        map.addControl(new LargeMapControl());

        addressBox.setText(proxy.getAddress());

        updateMarkerByAddress();

        initWidget(map);
    }

    public void updateMarkerByAddress()
    {
        geoCoder.getLocations(addressBox.getText(), new GoogleMapLocationCallback());
    }

    /**
     * update the text boxes with the lat, lng and address
     * 
     * @param point is the LatLng point of the location
     * @param address is the address string of the location
     */
    private void updateTextBoxes(LatLng point, String address)
    {
        if (null != latBox)
        {
            latBox.setText("" + point.getLatitude());
        }
        if (null != lngBox)
        {
            lngBox.setText("" + point.getLongitude());
        }
        if (null != address)
        {
            addressBox.setText(address);
        }
    }

    private void updateProxy(LatLng point, String address)
    {
        if (isEditMode)
        {
            proxy.setAddress(address);
            proxy.setLat(point.getLatitude());
            proxy.setLng(point.getLongitude());
        }
    }

    private void performReverseLookup(final LatLng point)
    {
        geoCoder.getLocations(point, new GoogleMapLocationCallback());
    }

    private class GoogleMapMarkerDragEndHandler implements MarkerDragEndHandler
    {
        @Override
        public void onDragEnd(MarkerDragEndEvent event)
        {
            LatLng point = event.getSender().getLatLng();
            if (point != null)
            {
                performReverseLookup(point);
            }
        }
    }

    private class GoogleMapLocationCallback implements LocationCallback
    {

        @Override
        public void onSuccess(JsArray<Placemark> locations)
        {
            if (locations.length() > 0)
            {
                Placemark location = locations.get(0);
                LatLng point = location.getPoint();
                marker.setLatLng(point);
                marker.setVisible(true);
                String address = location.getAddress();

                updateTextBoxes(point, address);
                updateProxy(point, address);
            }
            else
            {
                Window.alert("Was not able to find the location");
            }
        }

        @Override
        public void onFailure(int statusCode)
        {
            Window.alert("Failure Was not able to find the location");
        }
    }
}