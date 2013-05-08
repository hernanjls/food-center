package foodcenter.server.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.FetchGroup;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.Point;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;

import foodcenter.server.ThreadLocalPM;
import foodcenter.server.db.modules.AbstractDbGeoObject;
import foodcenter.server.db.modules.AbstractDbObject;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUserGcm;

public class DbHandlerImp implements DbHandler
{
    private Logger logger = LoggerFactory.getLogger(DbHandlerImp.class);

    
    private PersistenceManager getPersistenceManager()
    {
    	PersistenceManager pm = ThreadLocalPM.get();
    	
    	return pm;
    }
    
    @Override
    public <T extends AbstractDbObject> T save(T object)
    {
//    	TODO check why it get lost here: new SimpleRequestProcessor(null).decodeInvocationArguments()
    	logger.info("save: " + object.getClass() + "state:" + JDOHelper.getObjectState(object));
    	PersistenceManager pm = getPersistenceManager();
        try
        {
        	if (ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL == JDOHelper.getObjectState(object))
        	{
        		pm.makeTransient(object);
        	}
            T res = pm.makePersistent(object);
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            tx.commit();
            return res;
        }
        catch (Throwable e)
        {
            logger.error(e.getMessage(), e);
            
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        finally
        {
        	ThreadLocalPM.get().currentTransaction().begin();
        }
        
        return null;
    }
    
    @Override
    public <T extends AbstractDbObject> Long delete(Class<T> clazz, String id)
    {
        PersistenceManager pm = getPersistenceManager();
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
    
    @Override
    public <T extends AbstractDbObject> T find(Class<T> clazz, String id)
    {
        PersistenceManager pm = getPersistenceManager();
    	T res = null;
        try
    	{
            res =  pm.getObjectById(clazz, id);
            if (null == res)
            {
            	logger.warn(clazz.toString() + " with id: " + id + " was not found" );
            }
            ThreadLocalPM.get().currentTransaction().commit();
            
            return res;
        }
        catch (JDOObjectNotFoundException e)
        {
            logger.info(e.getMessage(), e);
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        finally
        {
        	Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
            ThreadLocalPM.get().currentTransaction().begin();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractDbObject> List<T> find(Class<T> clazz, String baseQuery, String declaredParams, Object[] values, Integer maxResults)
    {
        PersistenceManager pm = getPersistenceManager();
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
            
            ThreadLocalPM.get().currentTransaction().commit();
            	
    		return attached;
	            
        }
        catch (JDOObjectNotFoundException e)
        {
            logger.info(e.getMessage());
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            Transaction tx = ThreadLocalPM.get().currentTransaction();
            if (tx.isActive())
            {
            	tx.rollback();
            }
        }
        finally
        {
            ThreadLocalPM.get().currentTransaction().begin();
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
        PersistenceManager pm = getPersistenceManager();
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
    
    @Override
    public DbRestaurant searchRestaurantByName(String name)
    {
        return find(DbRestaurant.class, "name == value", "String value", new Object[]{name});        
    }

    @Override
    public void saveMsg(String email, String msg)
    {
        PersistenceManager pm = getPersistenceManager();

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

    @Override
    public long deleteMsg(String msg)
    {
        PersistenceManager pm = getPersistenceManager();
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

    @Override
    public List<DbMsg> getMsgs()
    {
        PersistenceManager pm = getPersistenceManager();
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

    @Override
    public void gcmRegister(String email, String regId)
    {
        PersistenceManager pm = getPersistenceManager();
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

    @Override
    public long gcmUnregister(String email, String regId)
    {
        PersistenceManager pm = getPersistenceManager();
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

    @Override
    public List<String> getGcmRegistered()
    {
        PersistenceManager pm = getPersistenceManager();
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
