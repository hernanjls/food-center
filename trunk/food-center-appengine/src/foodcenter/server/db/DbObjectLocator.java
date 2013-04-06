package foodcenter.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.Locator;

import foodcenter.server.db.modules.DbObject;

public class DbObjectLocator extends Locator<DbObject, String>
{
    private DbHandler db;
    private Logger logger;
    
    public DbObjectLocator()
    {
        this.db = new DbHandlerImp();
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public DbObject create(Class<? extends DbObject> clazz)
    {
        try
        {
            return clazz.newInstance();
        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
            return null;
        }
    }

    @Override
    public DbObject find(Class<? extends DbObject> clazz, String id)
    {
        return db.find(clazz, id);
    }

    @Override
    public Class<DbObject> getDomainType()
    {
        return null;
    }

    @Override
    public String getId(DbObject domainObject)
    {
        return domainObject.getId();
    }

    @Override
    public Class<String> getIdType()
    {
        return String.class;
    }

    @Override
    public Object getVersion(DbObject domainObject)
    {
        return domainObject.getVersion();
    }

}
