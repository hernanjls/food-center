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

import foodcenter.service.proxies.interfaces.AbstractGeoLocationInterface;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDbGeoObject extends AbstractDbObject implements AbstractGeoLocationInterface
{

	@Persistent
	@Latitude
	private Double lat = 0.0;

	@Persistent
	@Longitude
	private Double lng = 0.0;

	@Persistent(defaultFetchGroup = "true")
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
