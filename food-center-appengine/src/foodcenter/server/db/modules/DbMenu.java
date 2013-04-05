package foodcenter.server.db.modules;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.proxies.MenuCategoryProxy;

@PersistenceCapable
public class DbMenu extends DbObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4112168431679237604L;

	@Persistent
	private List<DbMenuCategory> categories = new LinkedList<DbMenuCategory>();
	
	public DbMenu()
    {
        super();
    }
	
    public List<DbMenuCategory> getCategories()
    {
        return this.categories;
    }
    
    public void setCategories(List<DbMenuCategory> categories)
    {
        this.categories = categories;
    }


}
