package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable(detachable = "true")
//@FetchGroup(name = "DbCourse", members = { @Persistent(name = "category") })
public class DbCourse extends AbstractDbObject
{

	/**
	 * 
	 */
    private static final long serialVersionUID = 6823279601912582912L;

    private static final Logger logger = LoggerFactory.getLogger(DbCourse.class);
    
	@Persistent
	private String name;

	@Persistent
    private String info;

	@Persistent
	private Double price;


//	@Persistent
//	DbMenuCategory category;

	public DbCourse()
	{
		super();
		logger.trace("new DbCourse()");
	}
	
	public DbCourse(String name, Double price)
	{
		this();
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

	public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
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
