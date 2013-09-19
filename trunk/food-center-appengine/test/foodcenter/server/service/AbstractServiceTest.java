package foodcenter.server.service;

import foodcenter.server.db.AbstractDbTest;
import foodcenter.server.db.modules.DbRestaurant;

public abstract class AbstractServiceTest extends AbstractDbTest
{
	
	protected DbRestaurant saveRest(DbRestaurant rest)
	{
        setUpPMF();
        rest = RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        
        return rest;
	}
}
