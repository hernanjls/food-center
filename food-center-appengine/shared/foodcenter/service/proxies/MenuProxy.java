package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbMenu", locator = "foodcenter.server.db.DbObjectLocator")
public interface MenuProxy extends EntityProxy, AbstractEntityInterface
{
    public List<MenuCategoryProxy> getCategories();
    
    public void setCategories(List<MenuCategoryProxy> categories);

}
