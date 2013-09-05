package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable //(detachable="true")
public class DbMenu extends AbstractDbObject
{
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 7723827631870435809L;
    
    private static final Logger logger = LoggerFactory.getLogger(DbCourse.class);
    
	@Persistent //(mappedBy="menu")
	private List<DbMenuCategory> categories = new ArrayList<DbMenuCategory>();

        
    public DbMenu()
    {
    	super();
    	logger.trace("new DbMenu()");
    }
    
	public List<DbMenuCategory> getCategories()
	{
		logger.debug("getCategories()");
		return this.categories;
	}

	public void setCategories(List<DbMenuCategory> categories)
	{
		logger.debug("setCategories(List<DbMenuCategory> categories)");
		this.categories = categories;
	}
    
}
