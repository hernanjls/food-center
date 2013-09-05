package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.server.db.security.UserPrivilege;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable //(detachable="true")
public class DbRestaurantBranch extends AbstractDbGeoObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -1838782438427806774L;

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<String> waiters = new ArrayList<String>(); // emails

    @Persistent
    private List<String> chefs = new ArrayList<String>(); // emails

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

        // Set privilege...
        UserPrivilege p = PrivilegeManager.getPrivilege(this);
        if (UserPrivilege.Admin == p || UserPrivilege.RestaurantAdmin == p
            || UserPrivilege.RestaurantBranchAdmin == p)
        {
            setEditable(true);
        }
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
