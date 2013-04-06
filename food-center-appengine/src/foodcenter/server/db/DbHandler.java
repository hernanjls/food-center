package foodcenter.server.db;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;

import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbObject;
import foodcenter.server.db.modules.DbRestaurant;

public interface DbHandler
{

    
    public DbRestaurant searchRestaurantByName(String name);

    /**
     * creates a blank restaurant
     * @param name is the restaurant name
     * @return the restaurant id
     */
    public String createRestaurant(String name);
    
    
    
    
    // deprecated (remian for example only)
    
	public List<DbMsg> getMsgs();

	/**
	 * 
	 * @param msg
	 * @return the number of delete rows
	 */
	public long deleteMsg(String msg);

	public void saveMsg(String email, String msg);

	
	public void gcmRegister(String email, String regId);

    public long gcmUnregister(String email, String regId);

    public List<String> getGcmRegistered();

    public <T extends DbObject> T find(Class<T> clazz, String id);
    
    public <T extends DbObject> Long delete(Class<T> clazz, String id);
    
    public <T extends DbObject> T save(T object);
    





    
}