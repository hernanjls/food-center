package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable="true")
//@FetchGroup(name = "DbRestaurantBranch", members = { //
//	@Persistent(name = "menu"),//
//	@Persistent(name = "admins"), //
//	@Persistent(name = "waiters"), //
//	@Persistent(name = "chefs"), //
//	@Persistent(name = "tables"), //
//	@Persistent(name = "orders")
//	
//})
@FetchGroups(value = { //
	@FetchGroup(name = "DbRestaurantBranch_menu", members = { @Persistent(name = "menu") }), //
    @FetchGroup(name = "DbRestaurantBranch_admins", members = { @Persistent(name = "admins") }), //
    @FetchGroup(name = "DbRestaurantBranch_waiters", members = { @Persistent(name = "waiters") }), //
    @FetchGroup(name = "DbRestaurantBranch_chefs", members = { @Persistent(name = "chefs") }), //
    @FetchGroup(name = "DbDbRestaurantBranch_tables", members = { @Persistent(name = "tables") }), //
    @FetchGroup(name = "DbDbRestaurantBranch_orders", members = { @Persistent(name = "orders") }), //
})
public class DbRestaurantBranch extends AbstractDbGeoObject
{
	/**
     * 
     */
	private static final long serialVersionUID = 2314058106724557278L;

	@Persistent()
	private DbRestaurant restaurant;

	@Persistent
	private List<String> admins = new ArrayList<String>();	//emails

	@Persistent
	private List<String> waiters = new ArrayList<String>();	//emails

	@Persistent
	private List<String> chefs = new ArrayList<String>();	//emails

	@Persistent
	private List<DbTable> tables = new ArrayList<DbTable>();

	@Persistent
	private List<DbCart> orders = new ArrayList<DbCart>();

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

	public List<DbTable> getTables()
	{
		return tables;
	}

	public void setTables(List<DbTable> tables)
	{
		this.tables = tables;
	}

	public List<DbCart> getOrders()
	{
		return orders;
	}

	public void setOrders(List<DbCart> orders)
	{
		this.orders = orders;
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

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
