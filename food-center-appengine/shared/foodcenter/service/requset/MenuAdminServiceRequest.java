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
    /**
     * adds the course to the category
     * @param cat       - category which will hold the course
     * @param course    - course to add
     * @return void
     * 
     * @note save restaurant/ menu after the changes
     */
	public Request<Void> addCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	/**
	 * removes the course from the category
     * @param cat       - category which will hold the course
     * @param course    - course to add
     * @return void
     * 
     * @note save restaurant/ menu after the changes
	 */
	public Request<Void> removeCategoryCourse(MenuCategoryProxy cat, CourseProxy course);

	/**
	 * adds the category to the menu
	 * @param menu     - menu which will hold the category
	 * @param category - category to add to the menu
	 * @return void
	 * 
	 * @note save restaurant/ menu after the changes
	 */
	public Request<Void> addMenuCategory(MenuProxy menu, MenuCategoryProxy category);

	/**
	 * removes the category from the menu
	 * @param menu     - menu which hold the category
	 * @param category - category to remove from the menu 
	 * @return void
	 * 
	 * @note save restaurant/ menu after the changes
	 */
	public Request<Void> removeMenuCategory(MenuProxy menu, MenuCategoryProxy category);
	
	public Request<MenuProxy> saveMenu(MenuProxy menu);
}
