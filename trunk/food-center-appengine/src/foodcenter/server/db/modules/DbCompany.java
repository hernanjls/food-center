package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable="true")
public class DbCompany extends AbstractDbObject
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6919498756425045653L;

	public DbCompany()
    {
        super();
    }

}
