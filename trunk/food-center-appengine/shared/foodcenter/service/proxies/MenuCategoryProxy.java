package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbMenuCategory", locator = "foodcenter.server.db.DbObjectLocator")
public interface MenuCategoryProxy extends EntityProxy
{

    public String getCategoryTitle();
    
    public void setCategoryTitle(String title);
    
    public List<CourseProxy> getCourses();
    
    public void setCourses(List<CourseProxy> courses);
}
