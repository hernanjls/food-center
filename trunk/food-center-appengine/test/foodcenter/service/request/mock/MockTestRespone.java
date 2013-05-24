package foodcenter.service.request.mock;

import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * 
 * RF test of fire() is not actually invoking a new thread on test env....
 * 
 * @param <T> is the expected response
 */
public class MockTestRespone<T> extends Receiver<T>
{
	public T response = null;
	
	@Override
	public void onSuccess(T response)
	{
		this.response = response;
	}
};
