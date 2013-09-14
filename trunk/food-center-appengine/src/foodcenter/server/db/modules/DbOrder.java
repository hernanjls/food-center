package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable = "true")
public class DbOrder extends AbstractDbObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4888609583270819317L;

	@Persistent
	private String userId; // user which made this order

	@Persistent
    private String compId;

	@Persistent
	private String compBranchId;

	@Persistent
    private String restId;

	@Persistent
	private String restBranchId;

	@Persistent
	private List<DbCourseOrder> courses = new ArrayList<DbCourseOrder>(); // Courses IDs

	@Persistent
	private Date date = null;

	@Persistent
	private boolean delivered = false;
	
	@Persistent
    private Date deliveryDate = null;

	@Persistent
	private ServiceType service;
	
	public DbOrder()
	{
		super();
	}

	@Override
	public void jdoPreStore()
	{
		super.jdoPreStore();

		// Make sure the date is correct
		if (null == this.date)
		{
		    this.date = new Date();
		}
		if (this.delivered && null == this.deliveryDate)
		{
		    this.deliveryDate = new Date();
		}
	};

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getCompId()
    {
        return compId;
    }

    public void setCompId(String compId)
    {
        this.compId = compId;
    }
    
	public String getCompBranchId()
	{
		return compBranchId;
	}

	public void setCompBranchId(String compBranchId)
	{
		this.compBranchId = compBranchId;
	}

	public String getRestId()
    {
        return restId;
    }

    public void setRestId(String restId)
    {
        this.restId = restId;
    }
    
	public String getRestBranchId()
	{
		return restBranchId;
	}

	public void setRestBranchId(String restBranchId)
	{
		this.restBranchId = restBranchId;
	}

	public List<DbCourseOrder> getCourses()
	{
		return courses;
	}

	public void setCourses(List<DbCourseOrder> courses)
	{
		this.courses = courses;
	}

	public Date getDate()
	{
		return date;
	}

    public Boolean getDelivered()
    {
        return delivered;
    }

    public void setDelivered(boolean delivered)
    {
        this.delivered = delivered;
    }

    public Date getDeliveryeDate()
    {
        return deliveryDate;
    }

    public ServiceType getService()
    {
        return service;
    }

    public void setService(ServiceType service)
    {
        this.service = service;
    }

}
