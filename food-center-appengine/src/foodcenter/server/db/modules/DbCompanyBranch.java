package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.users.User;

import foodcenter.server.db.security.UsersManager;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable = "true")
public class DbCompanyBranch extends AbstractDbGeoObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1303969414427455468L;

    @Persistent //(defaultFetchGroup="true")
    private DbCompany company = null;

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

        User user = UsersManager.getUser();

        // Set edit permission (this happens in post load before any changes to company / admins)
        setEditable(getCompany().isEditable() || admins.contains(user.getEmail().toLowerCase()));
    }

    public DbCompany getCompany()
    {
        return company;
    }

    public void setCompany(DbCompany company)
    {
        this.company = company;
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
