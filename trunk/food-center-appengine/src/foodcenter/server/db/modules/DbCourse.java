package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable
public class DbCourse extends DbObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4031177620915344142L;

	@Persistent
	private String name;
	
	@Persistent
	private Double price;
	
	public DbCourse()
    {
        super();
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
