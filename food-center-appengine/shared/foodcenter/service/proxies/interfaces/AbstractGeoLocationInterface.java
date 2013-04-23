package foodcenter.service.proxies.interfaces;



public interface AbstractGeoLocationInterface extends AbstractEntityInterface
{

    public Double getLat();
    
    public void setLat(Double lat);

    public Double getLng();
    
    public void setLng(Double lng);
    
    public void setAddress(String address);
    
    public String getAddress();
    

}
