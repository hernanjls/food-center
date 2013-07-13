package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;

import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

public interface MenuAdminServiceRequest
{
	public Request<Void> addCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	public Request<Void> removeCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	public Request<Void> addMenuCategory(MenuProxy menu, MenuCategoryProxy category);

	public Request<Void> removeMenuCategory(MenuProxy menu, MenuCategoryProxy category);
}
