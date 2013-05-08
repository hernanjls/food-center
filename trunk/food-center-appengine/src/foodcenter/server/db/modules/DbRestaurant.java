package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable = "true")
//@FetchGroups(value = { //
//	@FetchGroup(name = "DbRestaurant_menu", members = { @Persistent(name = "menu") }), //
//    @FetchGroup(name = "DbRestaurant_iconBytes", members = { @Persistent(name = "iconBytes") }), //
//    @FetchGroup(name = "DbRestaurant_branches", members = { @Persistent(name = "branches") }), //
//    @FetchGroup(name = "DbRestaurant_admins", members = { @Persistent(name = "admins") }), //
//    @FetchGroup(name = "DbRestaurant_services", members = { @Persistent(name = "services") }), //
//})
public class DbRestaurant extends AbstractDbObject
{

	/**
	 * 
	 */
    private static final long serialVersionUID = -9053099508081246189L;

	@Persistent
	@NotNull
	private String name = "";

	@Persistent
	private DbMenu menu = new DbMenu();

	@Persistent
	private List<Byte> iconBytes = new ArrayList<Byte>();

	@Persistent
	private String phone = "";

	@Persistent //(mappedBy = "restaurant")
	private List<DbRestaurantBranch> branches = new ArrayList<DbRestaurantBranch>();

	@Persistent
	private List<String> admins = new ArrayList<String>(); // emails

	@Persistent
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
