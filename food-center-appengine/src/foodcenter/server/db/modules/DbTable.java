package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable //(detachable="true")
public class DbTable extends AbstractDbObject
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4949395591622620937L;

	public DbTable()
    {
        super();
    }

}
