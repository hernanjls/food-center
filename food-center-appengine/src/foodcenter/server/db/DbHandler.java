package foodcenter.server.db;

import java.util.List;

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
     * 
     * @param object is the object to persist.
     * @return the persisted object
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> T save(T object)
    {
        logger.info("save: " + object.getClass() + ", state:" + JDOHelper.getObjectState(object));
        PMF.makeTransactional();
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
            // PMF.get().currentTransaction().begin();
        }

        return null;
    }

    /**
     * deletes the DB object with the given id
     * 
     * @param clazz is the DbObject class to delete
     * @param id is the id of the object to delete
     * @return the number of deleted rows
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> Long delete(Class<T> clazz, String id)
    {
        PMF.makeTransactional();
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(clazz);
            q.setFilter("id == value");
            q.declareParameters("String value");
            Long res = q.deletePersistentAll(id.toString());

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

            return 0L;
        }
        finally
        {
            // pm.close();
        }
    }

    /**
     * find the clazz object which matches the id, and eager fetch it!
     * 
     * @param clazz is the class type to search for
     * @param id is the id of the object
     * @return the object of type clazz with the id
     * 
     * @see {@link AbstractDbObject}
     */
    public static <T extends AbstractDbObject> T find(Class<T> clazz, String id)
    {
        logger.info("find: " + clazz.getSimpleName() + ", id= " + id);
        PersistenceManager pm = PMF.get();
        T res = null;
        try
        {
            res = pm.getObjectById(clazz, id);
            if (null == res)
            {
                logger.warn(clazz.toString() + " with id: " + id + " was not found");
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        return res;
    }

    /**
     * @param clazz - is the class to fetch from the DB.
     * @param baseQuery - Base query string without the declared parameters and without the entity
     *            name. <br>
     *            Ex: "lastName == lastNameParam".<br>
     *            if null - no base query will be added <br>
     *            or "||" may be used on a single field.
     * @param declaredParams - Declare the list of parameters and values for query execution. <br>
     *            if null no declared parameters nor values will be used.
     * @param sortOrder - the order sorting the result (lower idx is 1st), <br>
     *            if null default order is used
     * @param startIdx - get results starting from index (null == 0)
     * @param endIdx - get results up to from index (null == startIdx + 100)
     * @return list of all objects matching, or null on failure
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractDbObject> List<T> find(Class<T> clazz,
                                                            String baseQuery,
                                                            List<DeclaredParameter> declaredParams,
                                                            List<SortOrder> sortOrders,
                                                            Integer startIdx,
                                                            Integer endIdx)
    {
        PersistenceManager pm = PMF.get();
        try
        {
            Query q = pm.newQuery(clazz);
            String decParamsStr = null;
            Object[] values = null;
            if (null != baseQuery)
            {
                q.setFilter(baseQuery);

                if (null != declaredParams && declaredParams.size() > 0)
                {
                    decParamsStr = getDeclaredParamsStr(declaredParams).toString();
                    q.declareParameters(decParamsStr);
                    values = getDeclaredParamsValues(declaredParams);
                }
            }

            if (null != sortOrders && sortOrders.size() > 0)
            {
                q.setOrdering(getSortOrder(sortOrders).toString());
            }

            startIdx = (null == startIdx) ? 0 : startIdx;
            endIdx = (null == endIdx) ? startIdx + 100 : endIdx;
            q.setRange(startIdx, endIdx); // limit query for the 1st result

            logger.info("find: " + clazz.getSimpleName()
                        + ", query= "
                        + baseQuery
                        + ", params= "
                        + decParamsStr);
            List<T> attached = null;
            if (null != values)
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
        }
        return null;
    }

    /**
     * @param clazz - is the class to fetch from the DB.
     * @param baseQuery - Base query string without the declared parameters and without the entity
     *            name. <br>
     *            Ex: "lastName == lastNameParam".<br>
     *            if null - no base query will be added <br>
     *            or "||" may be used on a single field.
     * @param declaredParams - Declare the list of parameters and values for query execution. <br>
     *            if null no declared parameters nor values will be used.
     * @param sortOrder - the order sorting the result (lower idx is 1st), <br>
     *            if null default order is used
     * @param maxResults - max results to get, if null, no limit is taken
     * @return list of all objects matching, or null on failure
     */
    public static <T extends AbstractDbObject> List<T> find(Class<T> clazz,
                                                            String baseQuery,
                                                            List<DeclaredParameter> declaredParams,
                                                            List<SortOrder> sortOrders,
                                                            Integer maxResults)
    {

        return find(clazz, baseQuery, declaredParams, null, 0, maxResults);
    }

    /**
     * @param clazz - is the class to fetch from the DB.
     * @param baseQuery - Base query string without the declared parameters and without the entity
     *            name. <br>
     *            Ex: "lastName == lastNameParam".<br>
     *            if null - no base query will be added <br>
     *            or "||" may be used on a single field.
     * @param declaredParams - Declare the list of parameters and values for query execution. <br>
     *            if null no declared parameters nor values will be used.
     * @return Object matching the criteria, null if not found
     */
    public static <T extends AbstractDbObject> T find(Class<T> clazz,
                                                      String baseQuery,
                                                      List<DeclaredParameter> declaredParams)
    {
        List<T> res = find(clazz, baseQuery, declaredParams, null, 1);
        return res.isEmpty() ? null : res.get(0);
    }

    /**
     * Search the DB the for up to maxResults objects.
     * 
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
     * @param radiusMeters (optional) A number indicating the maximum distance to search, in meters.
     *            Set to 0 if no max distance is expected
     * @return a list of up to maxResults of matching DB objects, null on error.
     * 
     * @see {@link AbstractDbGeoObject}
     * @see {@link GeocellManager#proximitySearch(com.beoui.geocell.model.Point, int, double, Class, com.beoui.geocell.model.GeocellQuery, javax.jdo.PersistenceManager)}
     */
    public static <T extends AbstractDbGeoObject> List<T> proximitySearch(Class<T> clazz,
                                                                          Integer maxResults,
                                                                          Double centerLat,
                                                                          Double centerLng,
                                                                          Double radiusMeters)
    {
        PersistenceManager pm = PMF.get();
        pm.getFetchPlan().setMaxFetchDepth(-1);
        try
        {
            Point center = new Point(centerLat, centerLng);
            GeocellQuery baseQuery = new GeocellQuery();
            List<T> attached = GeocellManager.proximitySearch(center,
                                                              maxResults,
                                                              radiusMeters,
                                                              clazz,
                                                              baseQuery,
                                                              pm);
            return attached;
        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
        }
        finally
        {
            // pm.close(); // is closed by PMF and PersistanceFilter
        }
        return null;
    }

    private static StringBuilder getDeclaredParamsStr(List<DeclaredParameter> params)
    {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int n = params.size();
        for (i = 0; i < n - 1; ++i)
        {
            builder.append(params.get(i).declaredName);
            builder.append(", ");
        }
        builder.append(params.get(i).declaredName);
        return builder;
    }

    private static Object[] getDeclaredParamsValues(List<DeclaredParameter> params)
    {
        int n = params.size();
        Object[] res = new Object[n];
        for (int i = 0; i < n; ++i)
        {
            res[i] = params.get(i).value;
        }
        return res;
    }

    private static StringBuilder getSortOrder(List<SortOrder> orders)
    {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int n = orders.size();
        for (i = 0; i < n - 1; ++i)
        {
            builder.append(orders.get(i).sort);
            builder.append(", ");
        }
        builder.append(orders.get(i).sort);
        return builder;
    }

    public static class DeclaredParameter
    {
        public final String declaredName;
        public final Object value;

        public DeclaredParameter(String declaredName, Object value)
        {
            if (null == declaredName || null == value)
            {
                throw new IllegalArgumentException("Null pointer");
            }

            this.declaredName = value.getClass().getSimpleName() + " " + declaredName;
            this.value = value;
        }
    }

    public static enum SortOrderDirection
    {
        ASC, DESC;
    }

    public static class SortOrder
    {
        public final String sort;

        public SortOrder(String paramName, SortOrderDirection direction)
        {
            if (null == paramName || null == direction)
            {
                throw new IllegalArgumentException("Null pointer");
            }
            this.sort = paramName + " " + direction.toString().toLowerCase();
        }
    }

}
