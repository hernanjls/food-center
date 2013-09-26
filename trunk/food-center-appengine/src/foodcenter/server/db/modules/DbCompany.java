package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

import com.google.appengine.api.users.User;

import foodcenter.server.db.security.UsersManager;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable (detachable="true")
public class DbCompany extends AbstractDbObject
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -3140453310619997005L;

    public static final String DEFAULT_ICON_PATH = "/images/default_company_icon.jpg";

    @Persistent
    @NotNull
    private String name = "";

    @Persistent
    private String phone = "";

    @Persistent(mappedBy = "company")
    @Element(dependent = "true")
    private List<DbCompanyBranch> branches = new ArrayList<DbCompanyBranch>();

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<ServiceType> services = new ArrayList<ServiceType>();

    public DbCompany()
    {
        super();
    }

    public DbCompany(String name)
    {
        this();
        this.name = name;
    }

    @Override
    public void jdoPostLoad()
    {
        super.jdoPostLoad();

        User user = UsersManager.getUser();

        // Set edit permission (this happens in post load before any changes to admins)
        setEditable(UsersManager.isAdmin() || admins.contains(user.getEmail()));
        
        // Make sure image can be shown!
        if (0 == getImageUrl().length())
        {
            setImageUrl(DEFAULT_ICON_PATH);
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public List<DbCompanyBranch> getBranches()
    {
        return branches;
    }

    public void setBranches(List<DbCompanyBranch> branches)
    {
        this.branches = branches;
    }

    public List<String> getAdmins()
    {
        return admins;
    }

    public void setAdmins(List<String> admins)
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
