package foodcenter.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.Locator;

import foodcenter.server.db.modules.AbstractDbObject;

public class DbObjectLocator extends Locator<AbstractDbObject, String>
{
	private Logger logger;

	public DbObjectLocator()
	{
		logger = LoggerFactory.getLogger(getClass());
		logger.trace("new DbObjectLocator()");
	}

	@Override
	public AbstractDbObject create(Class<? extends AbstractDbObject> clazz)
	{
		logger.trace("Loader.create: " + clazz);
		try
		{
			return clazz.newInstance();
		}
		catch (Exception e)
		{
			logger.error("unexpected exeption", e);
		}
		return null;
	}

	@Override
	public AbstractDbObject find(Class<? extends AbstractDbObject> clazz, String id)
	{
		logger.trace("Loader.find: " + clazz + " id: " + id);
		return DbHandler.find(clazz, id);
	}

	@Override
	public Class<AbstractDbObject> getDomainType()
	{
		logger.trace("Loader.getDomainType()");
		return null;
	}

	@Override
	public String getId(AbstractDbObject domainObject)
	{
		logger.trace("Loader.getId(AbstractDbObject domainObject)");
		String id = domainObject.getId();
		return id;
	}

	@Override
	public Class<String> getIdType()
	{
		logger.trace("Loader.getIdType()");
		return String.class;
	}

	@Override
	public Object getVersion(AbstractDbObject domainObject)
	{
        logger.trace("Loader.getVersion(AbstractDbObject domainObject)");
        Integer version = 0;
        try
        {
            version = domainObject.getVersion();
        }
        catch (Exception e)
        {
//            logger.warn(e.getMessage(), e);
        }
		return version;
	}

}
