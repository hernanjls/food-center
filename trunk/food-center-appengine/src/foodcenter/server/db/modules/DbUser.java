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

    public DbUser()
    {
        super();
    }
    
    @Persistent
    private String username;
    
    @Persistent
    private Boolean isAdmin;

    public String getUsername()
    {
        return username;
    }

    public Boolean getIsAdmin()
    {
        return isAdmin;
    }
    
    
    
}
