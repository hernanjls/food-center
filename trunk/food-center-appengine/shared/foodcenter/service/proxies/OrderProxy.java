package foodcenter.service.proxies;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractOrderProxy;

@ProxyForName(value = "foodcenter.server.db.modules.DbOrder", locator = "foodcenter.server.db.DbObjectLocator")
public interface OrderProxy extends EntityProxy, AbstractOrderProxy
{

    public final static String[] ORDER_WITH = { "courses" , "date", "deliveryDate" };

    public List<CourseOrderProxy> getCourses();

    public void setCourses(List<CourseOrderProxy> courses);

    public Date getDate();

    public OrderStatus getStatus();
        
    public Date getDeliveryeDate();

    public ServiceType getService();
    
    public void setService(ServiceType service);
}
