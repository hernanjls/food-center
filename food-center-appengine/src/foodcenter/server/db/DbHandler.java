package foodcenter.server.db;

import java.util.List;

import com.beoui.geocell.GeocellManager;

import foodcenter.server.db.modules.AbstractDbGeoObject;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.AbstractDbObject;
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
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> T find(Class<T> clazz, String id);
    
    /**
     * Search the DB the fpr up to maxResults objects.
     * @param clazz is the class type to search for.
     * @param maxResults is the maximum results to retrieve.
     * @return a list of up to maxResults objects from the DB, null on error.
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> List<T> findN(Class<T> clazz, Integer maxResults);
    
    /**
     * 
     * @param clazz is the class type to search.
     * @param maxResults must be > 0. The larger this number, the longer the fetch will take.
     * @param centerLat latitude of the center Point around which to search for matching entities.
     * @param centerLng longitude of the center Point around which to search for matching entities.
     * @param radiusMeters (optional) A number indicating the maximum distance to search, in meters. Set to 0 if no max distance is expected
     * @return a list of up to maxResults of matching DB objects, null on error.
     * 
     * @see {@link AbstractDbGeoObject}
     * @see {@link GeocellManager#proximitySearch(com.beoui.geocell.model.Point, int, double, Class, com.beoui.geocell.model.GeocellQuery, javax.jdo.PersistenceManager)}
     */
    public <T extends AbstractDbGeoObject> List<T> proximitySearch(Class<T> clazz, Integer maxResults, Double centerLat, Double centerLng, Double radiusMeters);
    
    /**
     * deletes the DB object with the given id
     * @param clazz is the DbObject class to delete
     * @param id is the id of the object to delete
     * @return the number of deleted rows
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> Long delete(Class<T> clazz, String id);
    
    /**
     * Saves / updates the object to the DB
     * @param object is the object to persist.
     * @return the persisted object
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> T save(T object);
    
    
    





    
}
