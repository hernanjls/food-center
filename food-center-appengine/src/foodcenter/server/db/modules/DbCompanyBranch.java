package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.server.db.security.UserPrivilege;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable //(detachable="true")
public class DbCompanyBranch extends AbstractDbGeoObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1303969414427455468L;

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<String> workers = new ArrayList<String>(); // emails

    @Persistent
    private List<ServiceType> services = new ArrayList<ServiceType>();

    @Persistent
    private String phone;

    public DbCompanyBranch()
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

    public List<String> getWorkers()
    {
        return workers;
    }

    public void setWorkers(List<String> workers)
    {
        this.workers = workers;
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
