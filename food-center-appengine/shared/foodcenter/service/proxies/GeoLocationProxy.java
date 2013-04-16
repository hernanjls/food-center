package foodcenter.service.proxies;

import java.util.List;


public interface GeoLocationProxy
{

    public Double getLat();

    public Double getLng();

    public List<Double> getGeoLocation();
    
    public void setGeoLocation(List<Double> latLng);
    

}
