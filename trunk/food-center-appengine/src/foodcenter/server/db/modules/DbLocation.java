package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class DbLocation extends DbObject
{
    
    private double x;
    private double y;
    
    public DbLocation()
    {
        super();
    }
    

}
