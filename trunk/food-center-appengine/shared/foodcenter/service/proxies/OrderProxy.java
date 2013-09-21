package foodcenter.service.proxies;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbOrder", locator = "foodcenter.server.db.DbObjectLocator")
public interface OrderProxy extends EntityProxy, AbstractEntityInterface
{

    public final static String[] ORDER_WITH = { "courses" , "date", "deliveryDate" };

    public String getUserEmail();

    public String getCompId();

    public String getCompBranchId();

    public String getCompName();

    public String getCompBranchAddr();

    public String getRestId();

    public void setRestId(String restId);
    
    public String getRestName();

    public String getRestBranchId();

    public String getRestBranchAddr();
    
    public void setRestBranchId(String restBranchId);

    public List<CourseOrderProxy> getCourses();

    public void setCourses(List<CourseOrderProxy> courses);

    public Date getDate();

    public OrderStatus getStatus();
        
    public Date getDeliveryeDate();

    public ServiceType getService();
    
    public void setService(ServiceType service);
}
