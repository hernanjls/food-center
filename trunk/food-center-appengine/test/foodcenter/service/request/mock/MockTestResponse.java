package foodcenter.service.request.mock;

import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * 
 * RF test of fire() is not actually invoking a new thread on test env....
 * 
 * @param <T> is the expected response
 */
public class MockTestResponse<T> extends Receiver<T>
{
	public T response = null;
	
	@Override
	public void onSuccess(T response)
	{
		this.response = response;
	}
	@Override
	public void onFailure(ServerFailure error)
	{
	    LoggerFactory.getLogger(getClass()).error(error.getMessage());
	    this.response = null;
	}
};
