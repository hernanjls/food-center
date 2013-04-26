package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable = "true")
//@FetchGroup(name = "DbCompany", members = { //
//@Persistent(name = "iconBytes"), //
//@Persistent(name = "branches"),  //
//@Persistent(name = "admins"), //
//@Persistent(name = "services") //
//})

@FetchGroups(value = { //
  @FetchGroup(name = "DbCompany_iconBytes", members = { @Persistent(name = "iconBytes") }), //
  @FetchGroup(name = "DbCompany_branches", members = { @Persistent(name = "branches") }), //
  @FetchGroup(name = "DbCompany_admins", members = { @Persistent(name = "admins") }), //
  @FetchGroup(name = "DbCompany_services", members = { @Persistent(name = "services") }), //
})

public class DbCompany extends AbstractDbObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -6919498756425045653L;

    @Persistent
    @NotNull
    private String name = "";

    @Persistent
    private List<Byte> iconBytes = new ArrayList<Byte>();

    @Persistent
    private String phone = "";

    @Persistent(mappedBy = "company")
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
