package foodcenter.service.proxies;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;

@ProxyForName(value = "foodcenter.server.db.modules.DbOrder", locator = "foodcenter.server.db.DbObjectLocator")
public interface OrderProxy extends EntityProxy
{

    public final static String[] ORDER_WITH = { "courses", "date", "deliveryDate" };

    public String getUserId();

    public void setUserId(String userId);

    public String getCompId();

    public void setCompId(String compId);

    public String getCompBranchId();

    public void setCompBranchId(String compBranchId);

    public String getRestId();

    public void setRestId(String restId);

    public String getRestBranchId();

    public void setRestBranchId(String restBranchId);

    public List<CourseOrderProxy> getCourses();

    public void setCourses(List<CourseOrderProxy> courses);

    public Date getDate();

    public Boolean getDelivered();
    
    public void setDelivered(boolean delivered);
    
    public Date getDeliveryeDate();

    public ServiceType getService();
    
    public void setService(ServiceType service);
}
