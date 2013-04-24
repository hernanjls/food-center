package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class DbMenu extends AbstractDbObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4112168431679237604L;

	@Persistent(defaultFetchGroup = "true")
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
