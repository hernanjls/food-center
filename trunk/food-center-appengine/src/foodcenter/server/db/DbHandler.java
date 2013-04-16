package foodcenter.server.db;

import java.util.List;

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

    /**
     * find the clazz object which matches the id
     * @param clazz is the class type to search for
     * @param id is the id of the object
     * @return the object of type clazz with the id
     */
    public <T extends DbObject> T find(Class<T> clazz, String id);
    
    /**
     * fetch from the DB the 1st n objects.
     * @param clazz is the class type to search for.
     * @param n is the limit.
     * @return a list of up to n objects from the DB, null on error, <br>
     *  if less then n objects exists it returns all of them.
     */
    public <T extends DbObject> List<T> findN(Class<T> clazz, Integer n);
    
    public <T extends DbObject> Long delete(Class<T> clazz, String id);
    
    public <T extends DbObject> T save(T object);
    
    
    





    
}
