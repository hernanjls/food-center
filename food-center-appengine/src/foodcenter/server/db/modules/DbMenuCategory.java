package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class DbMenuCategory extends AbstractDbObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7877797583355332333L;

	@Persistent
	private String title;

	@Persistent(defaultFetchGroup = "true")
	private List<DbCourse> courses = new ArrayList<DbCourse>();

	public DbMenuCategory()
	{
		super();
	}

	public String getCategoryTitle()
	{
		return this.title;
	}

	public void setCategoryTitle(String title)
	{
		this.title = title;
	}

	public List<DbCourse> getCourses()
	{
		return courses;
	}

	public void setCourses(List<DbCourse> courses)
	{
		this.courses = courses;
	}

}
