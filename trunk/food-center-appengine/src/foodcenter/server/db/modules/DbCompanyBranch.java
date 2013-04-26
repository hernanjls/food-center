package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;


@PersistenceCapable(detachable="true")
public class DbCompanyBranch extends AbstractDbObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5543026953456550227L;

	public DbCompanyBranch()
    {
        super();
    }

}
