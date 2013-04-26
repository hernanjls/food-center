package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
@FetchGroup(name = "DbMenu", members = { @Persistent(name = "categories") })
public class DbMenu extends AbstractDbObject
{

	/**
	 * 
	 */
    private static final long serialVersionUID = 2593814734949906174L;
    
    @Persistent(mappedBy="menu")
	private List<DbMenuCategory> categories = new ArrayList<DbMenuCategory>();

        
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
