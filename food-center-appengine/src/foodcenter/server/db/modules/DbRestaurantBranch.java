package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unowned;

import foodcenter.service.enums.ServiceType;

@PersistenceCapable
public class DbRestaurantBranch extends AbstractDbGeoObject
{
	/**
     * 
     */
	private static final long serialVersionUID = 2314058106724557278L;

	@Persistent(defaultFetchGroup = "true")
	private DbRestaurant restaurant;

	@Persistent(defaultFetchGroup = "true")
	@Unowned
	private List<DbUser> admins = new ArrayList<DbUser>();;

	@Persistent(defaultFetchGroup = "true")
	@Unowned
	private List<DbUser> waiters = new ArrayList<DbUser>();;

	@Persistent(defaultFetchGroup = "true")
	@Unowned
	private List<DbUser> chefs = new ArrayList<DbUser>();

	@Persistent(defaultFetchGroup = "true")
	private List<DbTable> tables = new ArrayList<DbTable>();

	@Persistent(defaultFetchGroup = "true")
	private List<DbCart> orders = new ArrayList<DbCart>();

	@Persistent(defaultFetchGroup = "true")
	private DbMenu menu;

	@Persistent(defaultFetchGroup = "true")
	private List<ServiceType> services = new ArrayList<ServiceType>();

	@Persistent(defaultFetchGroup = "true")
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

	public List<DbUser> getAdmins()
	{
		return admins;
	}

	public void setAdmins(List<DbUser> admins)
	{
		this.admins = admins;
	}

	public List<DbUser> getWaiters()
	{
		return waiters;
	}

	public void setWaiters(List<DbUser> waiters)
	{
		this.waiters = waiters;
	}

	public List<DbUser> getChefs()
	{
		return chefs;
	}

	public void setChefs(List<DbUser> chefs)
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
