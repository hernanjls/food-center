package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

@ServiceName(value = "foodcenter.server.service.MenuAdminService")
public interface MenuAdminServiceRequest extends RequestContext
{
	public Request<Void> addCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	public Request<Void> removeCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	public Request<Void> addMenuCategory(MenuProxy menu, MenuCategoryProxy category);

	public Request<Void> removeMenuCategory(MenuProxy menu, MenuCategoryProxy category);
}
