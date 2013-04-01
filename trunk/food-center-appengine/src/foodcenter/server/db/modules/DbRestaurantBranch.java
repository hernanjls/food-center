package foodcenter.server.db.modules;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable
public class DbRestaurantBranch extends DbObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 2314058106724557278L;
    
    @Persistent
    private DbRestaurant restaurant;
    
    @Persistent
    private List<DbUser> admins;
    
    @Persistent
    private List<DbUser> waiters;
    
    @Persistent
    private List<DbUser> chefs;
    
    @Persistent
    private List<DbTable> tables;
    
    @Persistent
    private List<DbCart> orders;
    
    @Persistent
    private DbMenu menu;
    
    @Persistent
    private DbLocation location;
    
    @Persistent
    private List<ServiceType> services;
    
    @Persistent
    private String phone;
    
}
