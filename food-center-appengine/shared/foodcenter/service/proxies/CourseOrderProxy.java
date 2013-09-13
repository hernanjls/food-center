package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbCourseOrder", locator = "foodcenter.server.db.DbObjectLocator")
public interface CourseOrderProxy extends CourseProxy  
{
    public String getCourseId();
    
    public void setCourseId(String courseId);
    
    public int getCnt();

    public void setCnt(int cnt);
}


