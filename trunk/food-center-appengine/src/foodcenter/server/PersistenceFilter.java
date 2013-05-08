package foodcenter.server;

import java.io.IOException;

import javax.jdo.FetchGroup;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.PMF;

public class PersistenceFilter implements Filter
{
    protected static final Logger log = LoggerFactory.getLogger(PersistenceFilter.class.getName());
    

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        
    }

    @Override
    public void destroy()
    {
        
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
    	log.info("starting PersistenceManager");
        
    	PersistenceManager pm = PMF.get().getPersistenceManager();
//      pm.getFetchPlan().addGroup(FetchGroup.ALL);
//    	pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
//    	pm.setDetachAllOnCommit(true);
    	
        ThreadLocalPM.set(pm);
        Transaction tx = null;
        try
        {
        	tx = pm.currentTransaction();
        	tx.begin();
            chain.doFilter(req, res);
            
            tx = pm.currentTransaction();
            if (tx.isActive())
            {
            	tx.commit();
            }
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(), e);
        }
        finally
        {
        	if (null != tx && tx.isActive())
        	{
        		tx.rollback();
        	}
            log.info("closing PersistenceManager");
            pm.close();
        }

    }
}