package foodcenter.server.db;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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

public class DbHandlerImp implements DbHandler
{
    private Logger logger = LoggerFactory.getLogger(DbHandlerImp.class);

    
    @Override
    public <T extends AbstractDbObject> T save(T object)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try
        {
            return pm.makePersistent(object);
        }
        catch (Throwable e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
            pm.close();
        }
    }
    
    @Override
    public <T extends AbstractDbObject> Long delete(Class<T> clazz, String id)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
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
            pm.close();
        }
    }
    
    @Override
    public <T extends AbstractDbObject> T find(Class<T> clazz, String id)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
    	pm.getFetchPlan().setMaxFetchDepth(-1);
    	T res = null;
        try
        {
            res =  pm.getObjectById(clazz, id);
            return pm.detachCopy(res);
        }
        catch (JDOObjectNotFoundException e)
        {
            logger.info(e.getMessage());
        }
        catch (Exception e)
        {
        	
            logger.error(e.getMessage(), e);
        }
        finally
        {
            pm.close();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractDbObject> List<T> find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values, Integer maxResults)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
    	pm.getFetchPlan().setMaxFetchDepth(-1);
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
            
            List<T> attached = null;
            if (null != declaredParams && null != values)
            {
                attached = (List<T>) q.executeWithArray(values);
            }
            else
            {
	            attached = (List<T>) q.execute();
            }
            if (null != attached)
            {
	            //detach the objects
            	Object o = pm.detachCopyAll(attached);
	            return (List<T>) o;
            }
        }
        catch (JDOObjectNotFoundException e)
        {
            logger.info(e.getMessage());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            pm.close();
        }
        return null;
    }
    
    @Override
    public <T extends AbstractDbObject> T find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values)
    {
        List<T> res = find(clazz, baseQuery, declaredParams, values, 1);
        return res.isEmpty() ? null : res.get(0);
    }
    
    
    @Override
    public <T extends AbstractDbObject> List<T> find(Class<T> clazz, int maxResults)
    {
        return find(clazz, null, null, null, maxResults);
    }
    
    
    @Override
    public <T extends AbstractDbGeoObject> List<T> proximitySearch(Class<T> clazz, Integer maxResults, Double centerLat, Double centerLng, Double radiusMeters)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
    	pm.getFetchPlan().setMaxFetchDepth(-1);
        try
        {
            Point center = new Point(centerLat, centerLng);
            GeocellQuery baseQuery = new GeocellQuery();
            List<T> attached = GeocellManager.proximitySearch(center, maxResults, radiusMeters, clazz, baseQuery, pm);
            if (null != attached)
            {
	            //detach the objects
	            return (List<T>) pm.detachCopyAll(attached);
            }
        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
        }
        finally
        {
            pm.close();
        }
        return null;
    }
    
    @Override
    public DbRestaurant searchRestaurantByName(String name)
    {
        return find(DbRestaurant.class, "name == value", "String value", new Object[]{name});        
    }

    @Override
    public void saveMsg(String email, String msg)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        DbMsg m = new DbMsg(email, msg);

        try
        {
            pm.makePersistent(m);
        }
        finally
        {
            pm.close();
        }
    }

    @Override
    public long deleteMsg(String msg)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try
        {
            Query q = pm.newQuery(DbMsg.class);
            q.setFilter("msg == value");
            q.declareParameters("String value");
            return q.deletePersistentAll(msg);
        }
        finally
        {
            pm.close();
        }
    }

    @Override
    public List<DbMsg> getMsgs()
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
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
            pm.close();
        }
    }

    @Override
    public void gcmRegister(String email, String regId)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        DbUserGcm userGcm = new DbUserGcm(email, regId);
        try
        {
            pm.makePersistent(userGcm);
        }
        finally
        {
            pm.close();
        }
    }

    @Override
    public long gcmUnregister(String email, String regId)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try
        {
            Query q = pm.newQuery(DbUserGcm.class);
            q.setFilter("email == emailValue && gcmKey == gcmValue");
            q.declareParameters("String emailValue, String gcmValue");
            return q.deletePersistentAll(email, regId);
        }
        finally
        {
            pm.close();
        }
    }

    @Override
    public List<String> getGcmRegistered()
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
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
            pm.close();
        }
    }
}
