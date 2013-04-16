package foodcenter.server.db.modules;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.persistence.Transient;

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
    
    /**
     * 
     */
    private static final long serialVersionUID = -2812463966981641700L;

    public AbstractDbGeoObject()
    {
        super();
        setGeoLocation(0,0);
    }

    public Double getLat()
    {
        return lat;
    }


    public Double getLng()
    {
        return lng;
    }


    public List<String> getGeoCells()
    {
        return geoCells;
    }

    public void setGeoLocation(List<Double> latLng)
    {
        if (null == latLng || latLng.size() != 2)
        {
            return;
        }
        
        // parse latitude and longitude from the list
        Double lat = latLng.get(0);
        Double lng = latLng.get(1);
        
        // set the location
        setGeoLocation(lat, lng);
    }
        
    public List<Double> getGeoLocation()
    {
        List<Double> res = new LinkedList<Double>();
        res.add(lat);
        res.add(lng);
        return res;
    }

    private void setGeoLocation(double lat, double lng)
    {
        this.lat = lat;
        this.lng = lng;
        
        Point p = new Point(lat, lng);
        this.geoCells = GeocellManager.generateGeoCell(p);
    }

}
