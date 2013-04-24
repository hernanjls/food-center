package foodcenter.server.db.modules;


import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

import com.google.appengine.datanucleus.annotations.Unowned;

import foodcenter.service.enums.ServiceType;



@PersistenceCapable
public class DbRestaurant extends AbstractDbObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 6705094027232991722L;

    @Persistent
    @NotNull
    private String name = "";
    
    @Persistent(defaultFetchGroup="true")
    private DbMenu menu = new DbMenu();
    
    @Persistent(defaultFetchGroup="true")
    private List<Byte> iconBytes = new ArrayList<Byte>();
    
    @Persistent
    private String phone = "";
    
    @Persistent(mappedBy = "restaurant", defaultFetchGroup="true")
    private List<DbRestaurantBranch> branches = new ArrayList<DbRestaurantBranch>();
    
    @Persistent(defaultFetchGroup="true")
    @Unowned
    private List<DbUser> admins = new ArrayList<DbUser>();
    
    @Persistent(defaultFetchGroup="true")
    private List<ServiceType> services = new ArrayList<ServiceType>();
    
    public DbRestaurant()
    {
        super();
    }
    
    public DbRestaurant(String name)
    {
        this();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public DbMenu getMenu()
    {
        return menu;
    }

    public void setMenu(DbMenu menu)
    {
        this.menu = menu;
    }

    public List<Byte> getIconBytes()
    {
        return iconBytes;
    }

    public void setIconBytes(List<Byte> iconBytes)
    {
        this.iconBytes = iconBytes;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public List<DbRestaurantBranch> getBranches()
    {
        return branches;
    }

    public void setBranches(List<DbRestaurantBranch> branches)
    {
        this.branches = branches;
    }

    public List<DbUser> getAdmins()
    {
        return admins;
    }

    public void setAdmins(List<DbUser> admins)
    {
        this.admins = admins;
    }

	public List<ServiceType> getServices()
    {
	    return services;
    }

	public void setServices(List<ServiceType> services)
    {
	    this.services = services;
    }
    

}
