package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable
public class DbOrder extends AbstractDbOrder
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -4888609583270819317L;


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
