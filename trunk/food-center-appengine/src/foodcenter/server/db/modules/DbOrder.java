package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable(detachable="true")
public class DbOrder extends AbstractDbObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4888609583270819317L;
	
	@Persistent
	@Unowned   
	private DbUser user;   //user which made this order
	

	@Persistent
	@Unowned
	private List<String> courses = new ArrayList<String>();	// Courses IDs
	
	@Persistent
	private Date date;
	
	public DbOrder()
    {
        super();
    }

	
	
    @Override
    public void jdoPreStore() 
    {
        super.jdoPreStore();

        // Make sure the date is correct
        this.date = new Date();
    };
    
    public DbUser getUser()
    {
        return user;
    }
    
    public void setUser(DbUser user)
    {
        this.user = user;
    }
    
    public List<String> getCourses()
    {
        return courses;
    }
    
    public void setCourses(List<String> courses)
    {
        this.courses = courses;
    }
    
    public Date getDate()
    {
        return date;
    }

	
}
