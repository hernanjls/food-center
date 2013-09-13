package foodcenter.server;

import java.io.IOException;

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
    	PMF.initThreadLocal();    	
        try
        {
            chain.doFilter(req, res);            
        }
        catch (Exception e)
        {
        	log.error(e.getMessage(), e);
        }
        finally
        {
            PMF.closeThreadLocal();
        }

    }
}