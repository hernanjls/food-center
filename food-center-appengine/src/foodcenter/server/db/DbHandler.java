package foodcenter.server.db;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.Point;

import foodcenter.server.db.modules.AbstractDbGeoObject;
import foodcenter.server.db.modules.AbstractDbObject;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUserGcm;

/**
 * Class for simplifying calls the the DB. <br>
 * 
 * This class will use the current thread PM.
 * 
 * @see {@link PMF#initThreadLocal()} to initialize the current thread PM
 * 
 * @author Dror
 *
 */
public class DbHandler
{
    private static Logger logger = LoggerFactory.getLogger(DbHandler.class);

    
    /**
     * Saves / updates the object to the DB, <br>
     * also commits the current transaction, and creates a new one
     * @param object is the object to persist.
     * @return the persisted object
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> T save(T object)
    {
//    	TODO check why it get lost here: new SimpleRequestProcessor(null).decodeInvocationArguments()
    	logger.info("save: " + object.getClass() + "state:" + JDOHelper.getObjectState(object));
    	PersistenceManager pm = PMF.get();
        try
        {
            T res = pm.makePersistent(object);
            Transaction tx = PMF.get().currentTransaction();
            tx.commit();
            return res;
        }
        catch (Throwable e)
        {
            logger.error(e.getMessage(), e);
            
            Transaction tx = PMF.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        finally
        {
        	PMF.get().currentTransaction().begin();
        }
        
        return null;
    }
    
    /**
     * deletes the DB object with the given id
     * @param clazz is the DbObject class to delete
     * @param id is the id of the object to delete
     * @return the number of deleted rows
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> Long delete(Class<T> clazz, String id)
    {
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(clazz);
            q.setFilter("id == value");
            q.declareParameters("String value");
            return q.deletePersistentAll(id.toString());
        }
        catch (Throwable e)
        {
            logger.error(e.getMessage(), e);
            return 0L;
        }
        finally
        {
//            pm.close();
        }
    }

    /**
     * find the clazz object which matches the id, and eager fetch it!
     * @param clazz is the class type to search for
     * @param id is the id of the object
     * @return the object of type clazz with the id
     * 
     * @see {@link AbstractDbObject}
     */    
    public static <T extends AbstractDbObject> T find(Class<T> clazz, String id)
    {
        PersistenceManager pm = PMF.get();
    	T res = null;
        try
    	{
            res =  pm.getObjectById(clazz, id);
            if (null == res)
            {
            	logger.warn(clazz.toString() + " with id: " + id + " was not found" );
            }
//            ThreadLocalPM.get().currentTransaction().commit();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
//            Transaction tx = ThreadLocalPM.get().currentTransaction();
//            if (tx.isActive())
//            {
//            	tx.rollback();
//            }
//            ThreadLocalPM.get().currentTransaction().begin();
        }
        return res;
    }
    
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
    @SuppressWarnings("unchecked")
    public static <T extends AbstractDbObject> List<T> find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values, Integer maxResults)
    {
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(clazz);
            if (null != baseQuery)
            {
                q.setFilter(baseQuery);
                
                if (null != declaredParams && null != values)
                {
                    q.declareParameters(declaredParams);
                }
            }
            if (null != maxResults)
            {
                q.setRange(0, maxResults); // limit query for the 1st result
            }
            else
            {
            	q.setRange(0, 100);
            }
            
            List<T> attached = null;
            if (null != declaredParams && null != values)
            {
                attached = (List<T>) q.executeWithArray(values);
            }
            else
            {
	            attached = (List<T>) q.execute();
            }
            	
    		return attached;
	            
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            Transaction tx = PMF.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
            PMF.get().currentTransaction().begin();
        }
        return null;
    }
    
    
    /**
     * @param clazz          - is the class to fetch from the DB.
     * @param baseQuery      - Base query string without the declared parameters and without the entity name. <br>
     *                         Ex: "lastName == lastNameParam".<br>
     *                         if null - no base query will be added
     * @param declaredParams - Declare the list of parameters query execution. <br>
     *                         The parameter declaration is a String containing <br>
     *                         one or more query parameter declarations separated with commas. <br>
     *                         Ex: "String value, Int x, Date dateP".<br>
     *                         if null or valus are null, no declared parameters nor values will be used.
     * @param values		 - the values for the declared parameters.<br>
     * 						   if null or declared params are null, no declared parameters nor values will be used.
     * @return Object matching the criteria, null if not found
     */
    public static <T extends AbstractDbObject> T find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values)
    {
        List<T> res = find(clazz, baseQuery, declaredParams, values, 1);
        return res.isEmpty() ? null : res.get(0);
    }
    
    
    /**
     * Search the DB the fpr up to maxResults objects.
     * @param clazz is the class type to search for.
     * @param maxResults is the maximum results to retrieve.
     * @return a list of up to maxResults objects from the DB, null on error.
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> List<T> find(Class<T> clazz, int maxResults)
    {
        return find(clazz, null, null, null, maxResults);
    }
    
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
    public static <T extends AbstractDbGeoObject> List<T> proximitySearch(Class<T> clazz, Integer maxResults, Double centerLat, Double centerLng, Double radiusMeters)
    {
        PersistenceManager pm = PMF.get();
    	pm.getFetchPlan().setMaxFetchDepth(-1);
        try
        {
            Point center = new Point(centerLat, centerLng);
            GeocellQuery baseQuery = new GeocellQuery();
            List<T> attached = GeocellManager.proximitySearch(center, maxResults, radiusMeters, clazz, baseQuery, pm);
            if (null != attached)
            {
	            //detach the objects
//	            return (List<T>) pm.detachCopyAll(attached);
            }
            return attached;
        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
        }
        finally
        {
//            pm.close();
        }
        return null;
    }
    
    
    
    @Deprecated
    public static DbRestaurant searchRestaurantByName(String name)
    {
        return find(DbRestaurant.class, "name == value", "String value", new Object[]{name});        
    }

    
    @Deprecated
    public static void saveMsg(String email, String msg)
    {
        PersistenceManager pm = PMF.get();

        DbMsg m = new DbMsg(email, msg);

        try
        {
            pm.makePersistent(m);
        }
        finally
        {
//            pm.close();
        }
    }

    
    @Deprecated
    public static long deleteMsg(String msg)
    {
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(DbMsg.class);
            q.setFilter("msg == value");
            q.declareParameters("String value");
            return q.deletePersistentAll(msg);
        }
        finally
        {
//            pm.close();
        }
    }

    
    @Deprecated
    public static List<DbMsg> getMsgs()
    {
        PersistenceManager pm = PMF.get();
        try
        {
            List<DbMsg> res = new LinkedList<DbMsg>();
            Extent<DbMsg> extent = pm.getExtent(DbMsg.class, false);
            for (DbMsg m : extent)
            {
                res.add(m);
            }
            return res;
        }
        finally
        {
//            pm.close();
        }
    }

    @Deprecated
    public static void gcmRegister(String email, String regId)
    {
        PersistenceManager pm = PMF.get();
        DbUserGcm userGcm = new DbUserGcm(email, regId);
        try
        {
            pm.makePersistent(userGcm);
        }
        finally
        {
//            pm.close();
        }
    }

    @Deprecated
    public static long gcmUnregister(String email, String regId)
    {
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(DbUserGcm.class);
            q.setFilter("email == emailValue && gcmKey == gcmValue");
            q.declareParameters("String emailValue, String gcmValue");
            return q.deletePersistentAll(email, regId);
        }
        finally
        {
//            pm.close();
        }
    }

    @Deprecated
    public static List<String> getGcmRegistered()
    {
        PersistenceManager pm = PMF.get();
        try
        {
            List<String> res = new LinkedList<String>();
            Extent<DbUserGcm> extent = pm.getExtent(DbUserGcm.class, false);

            for (DbUserGcm m : extent)
            {
                res.add(m.getGcmKey());
            }

            return res;
        }
        finally
        {
//            pm.close();
        }
    }
}
