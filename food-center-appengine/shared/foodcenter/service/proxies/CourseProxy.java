package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbCourse", locator = "foodcenter.server.db.DbObjectLocator")
public interface CourseProxy extends EntityProxy
{
    public String getName();

    public void setName(String name);

    public Double getPrice();

    public void setPrice(Double price);

}
