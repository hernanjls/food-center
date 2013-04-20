package foodcenter.server.db.modules;

import java.util.List;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.annotations.Geocells;
import com.beoui.geocell.annotations.Latitude;
import com.beoui.geocell.annotations.Longitude;
import com.beoui.geocell.model.Point;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDbGeoObject extends AbstractDbObject
{

    @Persistent
    @Latitude
    private Double lat;
    
    @Persistent
    @Longitude
    private Double lng;
    
    @Persistent
    @Geocells
    private List<String> geoCells;
    
    @Persistent
    private String address;
    /**
     * 
     */
    private static final long serialVersionUID = -2812463966981641700L;

    public AbstractDbGeoObject()
    {
        super();
        setLat(0.0);
        setLng(0.0);
    }

    public Double getLat()
    {
        return lat;
    }


    public void setLat(Double lat)
    {
        this.lat = lat;
    }
    
    public Double getLng()
    {
        return lng;
    }

    public void setLng(Double lng)
    {
        this.lng = lng;
    }

    public List<String> getGeoCells()
    {
        return geoCells;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }
    
    @Override
    public void jdoPreStore()
    {
        super.jdoPreStore();
        
        Point p = new Point(lat, lng);
        this.geoCells = GeocellManager.generateGeoCell(p);
    }

}
