package foodcenter.server.service.msg;

import com.google.web.bindery.requestfactory.shared.Locator;

import foodcenter.server.db.modules.DbMsg;

public class MsgLocator extends Locator<DbMsg, Long>
{

	@Override
	public DbMsg create(Class<? extends DbMsg> clazz)
	{
		return new DbMsg();
	}

	@Override
	public DbMsg find(Class<? extends DbMsg> clazz, Long id)
	{
		return create(clazz);
	}

	@Override
	public Class<DbMsg> getDomainType()
	{
		return DbMsg.class;
	}

	@Override
	public Long getId(DbMsg domainObject)
	{
		return domainObject.getId();
	}

	@Override
	public Class<Long> getIdType()
	{
		return Long.class;
	}

	@Override
	public Object getVersion(DbMsg domainObject)
	{
		return domainObject.getVersion();
	}

}
