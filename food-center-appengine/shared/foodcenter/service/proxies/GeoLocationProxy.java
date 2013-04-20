package foodcenter.service.proxies;



public interface GeoLocationProxy
{

    public Double getLat();
    
    public void setLat(Double lat);

    public Double getLng();
    
    public void setLng(Double lng);
    
    public void setAddress(String address);
    
    public String getAddress();
    

}
