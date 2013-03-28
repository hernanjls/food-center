package foodcenter.server.service.msg;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class MsgServiceLocator implements ServiceLocator
{
	@Override
	public MsgService getInstance(Class<?> clazz)
	{
		// Or use Guice, Spring, whatever provides instances of MyService
		return new MsgService();
	}
	
}