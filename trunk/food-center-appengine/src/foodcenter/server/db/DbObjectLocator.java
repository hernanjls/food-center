package foodcenter.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.Locator;

import foodcenter.server.db.modules.AbstractDbObject;

public class DbObjectLocator extends Locator<AbstractDbObject, String>
{
    private DbHandler db;
    private Logger logger;
    
    public DbObjectLocator()
    {
        this.db = new DbHandlerImp();
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public AbstractDbObject create(Class<? extends AbstractDbObject> clazz)
    {
    	logger.info("Loader.create: " + clazz);
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
    public AbstractDbObject find(Class<? extends AbstractDbObject> clazz, String id)
    {
    	logger.info("Loader.find: " + clazz + " id: " + id);
        return db.find(clazz, id);
    }

    @Override
    public Class<AbstractDbObject> getDomainType()
    {
        return null;
    }

    @Override
    public String getId(AbstractDbObject domainObject)
    {
        return domainObject.getId();
    }

    @Override
    public Class<String> getIdType()
    {
        return String.class;
    }

    @Override
    public Object getVersion(AbstractDbObject domainObject)
    {
        return domainObject.getVersion();
    }

}