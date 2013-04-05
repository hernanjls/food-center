package foodcenter.server.db;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbObject;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUserGcm;

public class DbHandlerImp implements DbHandler
{
    private Logger logger = LoggerFactory.getLogger(DbHandlerImp.class);

    
    @Override
    public <T extends DbObject> T save(T object)
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
    public <T extends DbObject> Long delete(Class<T> clazz, Long id)
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
    public <T extends DbObject> T find(Class<T> clazz, Long id)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try
        {
            return pm.getObjectById(clazz, id);
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
    public DbRestaurant searchRestaurantByName(String name)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try
        {

            Query q = pm.newQuery(DbRestaurant.class);
            q.setFilter("name == value"); // where DbRestaurant.name = 'value'
            q.declareParameters("String value");
            q.setRange(0, 1); // limit query for the 1st result

            @SuppressWarnings("unchecked")
            List<DbRestaurant> res = (List<DbRestaurant>) q.execute(name); // with name as value

            return res.isEmpty() ? null : res.get(0);

        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
            return null;
        }
        finally
        {
            pm.close();
        }
    }

    public Long createRestaurant(String name)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        DbRestaurant r = new DbRestaurant(name);

        try
        {
            logger.info("createing a new restaurant with name " + name);
            pm.makePersistent(r);
            return r.getId();
        }
        catch (Exception e)
        {
            logger.error("unexpected exeption", e);
            return null;
        }
        finally
        {
            pm.close();
        }
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
