package foodcenter.server.db.modules;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
@FetchGroup(name = "DbCourse", members = { @Persistent(name = "category") })
public class DbCourse extends AbstractDbObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4031177620915344142L;

	@Persistent
	private String name;

	@Persistent
	private Double price;

	@Persistent
	DbMenuCategory category;

	public DbCourse()
	{
		super();
	}
	
	public DbCourse(String name, Double price)
	{
		this.name = name;
		this.price = price;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Double getPrice()
	{
		return price;
	}

	public void setPrice(Double price)
	{
		this.price = price;
	}

}
