package foodcenter.server.db.modules;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable
public class DbRestaurantBranch extends AbstractDbGeoObject
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
    private List<ServiceType> services;
    
    @Persistent
    private String phone;
    
    public DbRestaurantBranch()
    {
        //empty ctor
        super();
    }

    
    public DbRestaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(DbRestaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    public List<DbUser> getAdmins()
    {
        return admins;
    }

    public void setAdmins(List<DbUser> admins)
    {
        this.admins = admins;
    }

    public List<DbUser> getWaiters()
    {
        return waiters;
    }

    public void setWaiters(List<DbUser> waiters)
    {
        this.waiters = waiters;
    }

    public List<DbUser> getChefs()
    {
        return chefs;
    }

    public void setChefs(List<DbUser> chefs)
    {
        this.chefs = chefs;
    }

    public List<DbTable> getTables()
    {
        return tables;
    }

    public void setTables(List<DbTable> tables)
    {
        this.tables = tables;
    }

    public List<DbCart> getOrders()
    {
        return orders;
    }

    public void setOrders(List<DbCart> orders)
    {
        this.orders = orders;
    }

    public DbMenu getMenu()
    {
        return menu;
    }

    public void setMenu(DbMenu menu)
    {
        this.menu = menu;
    }

    public List<ServiceType> getServices()
    {
        return services;
    }

    public void setServices(List<ServiceType> services)
    {
        this.services = services;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
}
