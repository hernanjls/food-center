package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable
public class DbOrder extends AbstractDbObject
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -4888609583270819317L;

    @Persistent
    private String userEmail; // user which made this order

    @Persistent
    private String compId;

    @Persistent
    private String compName = "";

    @Persistent
    private String compBranchId;

    @Persistent
    private String compBranchAddr = "";

    @Persistent
    private String restId;

    @Persistent
    private String restName = "";

    @Persistent
    private String restBranchId;

    @Persistent
    private String restBranchAddr = "";

    @Persistent
    private List<DbCourseOrder> courses = new ArrayList<DbCourseOrder>(); // Courses IDs

    @Persistent
    private Date date = null;

    @Persistent
    private OrderStatus status = null;

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
        
        if (null == status)
        {
            status = OrderStatus.CREATED;
        }
        
        if ((OrderStatus.CREATED != this.status) && (null == this.deliveryDate))
        {
            this.deliveryDate = new Date();
        }
    };

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public String getCompId()
    {
        return compId;
    }

    public void setCompId(String compId)
    {
        this.compId = compId;
    }

    public String getCompName()
    {
        return compName;
    }

    public void setCompName(String compName)
    {
        this.compName = compName;
    }

    public String getCompBranchId()
    {
        return compBranchId;
    }

    public void setCompBranchId(String compBranchId)
    {
        this.compBranchId = compBranchId;
    }

    public String getCompBranchAddr()
    {
        return compBranchAddr;
    }

    public void setCompBranchAddr(String compBranchAddr)
    {
        this.compBranchAddr = compBranchAddr;
    }

    public String getRestId()
    {
        return restId;
    }

    public void setRestId(String restId)
    {
        this.restId = restId;
    }

    public String getRestName()
    {
        return restName;
    }

    public void setRestName(String restName)
    {
        this.restName = restName;
    }

    public String getRestBranchId()
    {
        return restBranchId;
    }

    public void setRestBranchId(String restBranchId)
    {
        this.restBranchId = restBranchId;
    }

    public String getRestBranchAddr()
    {
        return restBranchAddr;
    }

    public void setRestBranchAddr(String restBranchAddr)
    {
        this.restBranchAddr = restBranchAddr;
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

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
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
