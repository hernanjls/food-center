package foodcenter.server.db.modules;


import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

@PersistenceCapable
public class DbRestaurant extends DbObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 6705094027232991722L;

    @Persistent
    @NotNull
    private String name;
    
    @Persistent
    private DbMenu menu;
    
    @Persistent
    private char[] icon;
    
    @Persistent
    private String phone;
    
    @Persistent(mappedBy = "restaurant")
    private List<DbRestaurantBranch> branches;
    
    @Persistent
    private List<DbUser> admins;
    
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

    public char[] getIcon()
    {
        return icon;
    }

    public void setIcon(char[] icon)
    {
        this.icon = icon;
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
    

}
