package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable="true")

//@FetchGroups(value = { //
//  @FetchGroup(name = "DbRestaurantBranch_admins", members = { @Persistent(name = "admins") }), //
//  @FetchGroup(name = "DbRestaurantBranch_waiters", members = { @Persistent(name = "employees") }), //
//  @FetchGroup(name = "DbDbRestaurantBranch_orders", members = { @Persistent(name = "orders") }), //
//})
public class DbCompanyBranch extends AbstractDbGeoObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1303969414427455468L;

	@Persistent()
    private DbCompany company;

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<String> employees = new ArrayList<String>(); // emails

    @Persistent
    private List<ServiceType> services = new ArrayList<ServiceType>();

    @Persistent
    private String phone;

    public DbCompanyBranch()
    {
        // empty ctor
        super();
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

    public List<String> getEmployees()
    {
        return employees;
    }

    public void setEmployees(List<String> employees)
    {
        this.employees = employees;
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
