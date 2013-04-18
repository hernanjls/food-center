package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class DbUser extends AbstractDbObject
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5539632030148672137L;

    @Persistent
    private String username;
    
    @Persistent
    private Boolean isAdmin;
    
    public DbUser()
    {
        super();
    }

    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
    	this.username = username;
    }

    public Boolean getIsAdmin()
    {
        return isAdmin;
    }
    
    public void setIsAdmin(Boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }
    
    
    
}
