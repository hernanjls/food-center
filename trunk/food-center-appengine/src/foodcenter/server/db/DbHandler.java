package foodcenter.server.db;

import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.google.appengine.api.datastore.Key;

import foodcenter.server.db.modules.AbstractDbGeoObject;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.AbstractDbObject;
import foodcenter.server.db.modules.DbRestaurant;

public interface DbHandler
{   
    public DbRestaurant searchRestaurantByName(String name);    
    
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
     * find the clazz object which matches the id, and eager fetch it!
     * @param clazz is the class type to search for
     * @param id is the id of the object
     * @return the object of type clazz with the id
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> T find(Class<T> clazz, String id);
    
    /**
     * @param clazz          - is the class to fetch from the DB.
     * @param baseQuery      - Base query string without the declared parameters and without the entity name. <br>
     *                         Ex: "lastName == lastNameParam".<br>
     *                         if null - no base query will be added
     * @param declaredParams - Declare the list of parameters query execution. <br>
     *                         The parameter declaration is a String containing <br>
     *                         one or more query parameter declarations separated with commas. <br>
     *                         Ex: "String value, Int x".<br>
     *                         if null or valus are null, no declared parameters nor values will be used.
     * @param values		 - the values for the declared parameters.<br>
     * 						   if null or declared params are null, no declared parameters nor values will be used.
     * @param maxResults     - max results to get, if null, no limit is taken
     * @return list of all objects matching, or null on failure
     */
    public <T extends AbstractDbObject> List<T> find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values, Integer maxResults);
    
    /**
     * @param clazz          - is the class to fetch from the DB.
     * @param baseQuery      - Base query string without the declared parameters and without the entity name. <br>
     *                         Ex: "lastName == lastNameParam".<br>
     *                         if null - no base query will be added
     * @param declaredParams - Declare the list of parameters query execution. <br>
     *                         The parameter declaration is a String containing <br>
     *                         one or more query parameter declarations separated with commas. <br>
     *                         Ex: "String value, Int x".<br>
     *                         if null or valus are null, no declared parameters nor values will be used.
     * @param values		 - the values for the declared parameters.<br>
     * 						   if null or declared params are null, no declared parameters nor values will be used.
     * @return Object matching the criteria, null if not found
     */
    public <T extends AbstractDbObject> T find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values);
    
    /**
     * Search the DB the fpr up to maxResults objects.
     * @param clazz is the class type to search for.
     * @param maxResults is the maximum results to retrieve.
     * @return a list of up to maxResults objects from the DB, null on error.
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> List<T> find(Class<T> clazz, int maxResults);
    
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
     * Saves / updates the object to the DB, <br>
     * also commits the current transaction, and creates a new one
     * @param object is the object to persist.
     * @return the persisted object
     * 
     * @see {@link AbstractDbObject}
     */
    public <T extends AbstractDbObject> T save(T object);
    
    
    





    
}
