package foodcenter.server.service.msg;

import com.google.web.bindery.requestfactory.shared.Locator;

import foodcenter.server.db.modules.DbMsg;

public class MsgLocator extends Locator<DbMsg, Void>
{

	@Override
	public DbMsg create(Class<? extends DbMsg> clazz)
	{
		return new DbMsg();
	}

	@Override
	public DbMsg find(Class<? extends DbMsg> clazz, Void id)
	{
		return create(clazz);
	}

	@Override
	public Class<DbMsg> getDomainType()
	{
		return DbMsg.class;
	}

	@Override
	public Void getId(DbMsg domainObject)
	{
		return null;
	}

	@Override
	public Class<Void> getIdType()
	{
		return Void.class;
	}

	@Override
	public Object getVersion(DbMsg domainObject)
	{
		return null;
	}

}
