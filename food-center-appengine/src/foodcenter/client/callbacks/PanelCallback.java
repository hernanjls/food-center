package foodcenter.client.callbacks;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.RequestContext;

public interface PanelCallback<T extends EntityProxy, P extends RequestContext>
{

    /**
     * If panel is not null: calls panel.close()
     * 
     * @param panel - the caller panel to be closed 
     * @param proxy - the proxy which is associated to the button
     */
    public void close(RedrawablePanel panel, T proxy);
    
    /**
     * Saves the proxy, and opens a new View of the saved proxy on success
     * 
     * @param panel - the caller panel (usually will be closed from here)
     * @param proxy - the proxy to save
     * @param callback - callback for the new view which is created
     * @param service - service which the changes occur on
     */
    public void save(RedrawablePanel panel, T proxy, PanelCallback<T,P> callback, P service);
    
    /**
     * Opens a new view of this proxy
     * 
     * @param panel - the caller panel (usually will be closed from here)
     * @param proxy - proxy to show
     * @param callback - callback for the new view which is created
     */
    public void view(RedrawablePanel panel, T proxy, PanelCallback<T,P> callback);
    
    /**
     * Opens a new edit of this proxy
     * 
     * @param panel - the caller panel (usually will be closed from here)
     * @param proxy - proxy to show
     * @param callback - callback for the new view which is created
     */
    public void edit(RedrawablePanel panel, T proxy, PanelCallback<T,P> callback);
    
    /**
     * Creates a new proxy and opens a new view of this proxy
     * 
     * @param panel - the caller panel (usually will be closed from here)
     * @param callback - callback for the new view which is created
     */
    public void createNew(RedrawablePanel panel, PanelCallback<T,P> callback);
    
    /**
     * Deletes this proxy
     * 
     * @param panel - the caller panel (usually will be closed from here)
     * @param proxy - the proxy to delete
     */
    public void del(RedrawablePanel panel, T proxy);
    
    public void error(RedrawablePanel panel, T proxy, String reason);
}

