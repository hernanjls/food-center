package foodcenter.client.handlers;

import foodcenter.service.proxies.RestaurantBranchProxy;

public interface RestaurantBranchHandler
{
    
    /**
     * Do something with the given branch
     * @param branch - is the branch to handle
     * @param panel - is the panel to redraw when done
     */
    public void handle(RestaurantBranchProxy branch, RedrawablePannel panel);
}
