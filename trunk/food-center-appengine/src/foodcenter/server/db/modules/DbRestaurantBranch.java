package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.users.User;

import foodcenter.server.db.security.UsersManager;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable //(detachable="true")
public class DbRestaurantBranch extends AbstractDbGeoObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -1838782438427806774L;

    @Persistent
    private DbRestaurant restaurant = null;

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<String> waiters = new ArrayList<String>(); // emails

    @Persistent
    private List<String> chefs = new ArrayList<String>(); // emails

    @NotPersistent
    private Boolean chef = false;

    @Persistent
    private List<DbTable> tables = new ArrayList<DbTable>();
    
    @Persistent
    private DbMenu menu = new DbMenu();

    @Persistent
    private List<ServiceType> services = new ArrayList<ServiceType>();

    @Persistent
    private String phone;

    public DbRestaurantBranch()
    {
        // empty ctor
        super();
    }

    @Override
    public void jdoPostLoad()
    {
        super.jdoPostLoad();

        User user = UsersManager.getUser();
     
        // Set edit permission (this happens in post load before any changes to rest / admins)
        setEditable(getRestaurant().isEditable() || admins.contains(user.getEmail()));
        
        // Set chef's privilege
        setChef(isEditable() || chefs.contains(user.getEmail()));
    }

    public DbRestaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(DbRestaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    public List<String> getAdmins()
    {
        return admins;
    }

    public void setAdmins(List<String> admins)
    {
        this.admins = admins;
    }

    public List<String> getWaiters()
    {
        return waiters;
    }

    public void setWaiters(List<String> waiters)
    {
        this.waiters = waiters;
    }

    public List<String> getChefs()
    {
        return chefs;
    }

    public void setChefs(List<String> chefs)
    {
        this.chefs = chefs;
    }

    public boolean isChef()
    {
        return chef;
    }

    public void setChef(boolean chef)
    {
        this.chef = chef;
    }

    public List<DbTable> getTables()
    {
        return tables;
    }

    public void setTables(List<DbTable> tables)
    {
        this.tables = tables;
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
}
