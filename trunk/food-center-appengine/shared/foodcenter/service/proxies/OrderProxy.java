package foodcenter.service.proxies;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbOrder", locator = "foodcenter.server.db.DbObjectLocator")
public interface OrderProxy extends EntityProxy
{

	public final static String[] ORDER_WITH = { "user", //
        "courses", //
        "date" };
	
	public UserProxy getUser();
    
    public void setUser(UserProxy user);
    
    public List<String> getCourses();
    
    public void setCourses(List<String> courses);
    
    public Date getDate();

}
