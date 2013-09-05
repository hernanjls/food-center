package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable //(detachable="true")
public class DbMenuCategory extends AbstractDbObject
{

	/**
	 * 
	 */
    private static final long serialVersionUID = 3918736183925374251L;

    private static final Logger logger = LoggerFactory.getLogger(DbMenuCategory.class);
    
	@Persistent
	private String title;

//	@Persistent
//	private DbMenu menu;
	
	@Persistent //(mappedBy="category")
	private List<DbCourse> courses = new ArrayList<DbCourse>();

	public DbMenuCategory()
	{
		super();
		logger.trace("new DbMenuCategory()");
		
	}
	
	public DbMenuCategory(String title)
	{
		this.title = title;
	}

	public String getCategoryTitle()
	{
		return this.title;
	}

	public void setCategoryTitle(String title)
	{
		this.title = title;
	}

//	public DbMenu getMenu()
//    {
//	    return menu;
//    }
//
//	public void setMenu(DbMenu menu)
//    {
//	    this.menu = menu;
//    }

	public List<DbCourse> getCourses()
	{
		logger.debug("getCourses()");
		return courses;
	}

	public void setCourses(List<DbCourse> courses)
	{
		logger.debug("setCourses(List<DbCourse> courses)");
		this.courses = courses;
	}

}
