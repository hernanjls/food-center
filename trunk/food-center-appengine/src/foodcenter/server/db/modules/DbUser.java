package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable(detachable="true")
public class DbUser extends AbstractDbObject
{

	/**
	 * 
	 */
    private static final long serialVersionUID = -3783620685863712560L;

	@Persistent
	private String logoutUrl;
	
	@Persistent
	private String nickName;
	
	@Persistent
	private String email;
	
	@Persistent
	private String userId;
	
	@NotPersistent
	private Boolean isAdmin;

	@Persistent
	private String gcmKey;
	
	@Persistent
	private List<DbOrder> orders = new ArrayList<DbOrder>();
	
	public DbUser()
	{
		// empty ctor
	}

	public String getLogoutUrl()
	{
		return logoutUrl;
	}
	
    public void setLogoutUrl(String logoutUrl)
    {
        this.logoutUrl = logoutUrl;
    }

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
	
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
    {
        this.email = email;
    }
	
	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
    {
        this.userId = userId;
    }
	
	public Boolean isAdmin()
	{
		return isAdmin;
	}
	
	public void setAdmin(Boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }

	public String getGcmKey()
    {
        return gcmKey;
    }

    public void setGcmKey(String gcmKey)
    {
        this.gcmKey = gcmKey;
    }

    public List<DbOrder> getOrders()
    {
        return orders;
    }
    
    public void setOrders(List<DbOrder> orders)
    {
        this.orders = orders;
    }
}
