package foodcenter.server.db.modules;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.server.db.security.UsersManager;
import foodcenter.server.service.ClientService;

@PersistenceCapable//(detachable="true")
public class DbUser extends AbstractDbObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -3783620685863712560L;

    private static final String DEFAULT_ICON_PATH="/images/user.jpg";
    
    @NotPersistent
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

    public DbUser()
    {
        // empty ctor
    }

    @Override
	public void jdoPostLoad()
	{
	    super.jdoPostLoad();
	    
	    logoutUrl = ClientService.getLogoutUrl();
        
	    // Make sure image can be shown!
        if (0 == getImageUrl().length())
        {
            setImageUrl(DEFAULT_ICON_PATH);
        }
        setEditable(ClientService.getCurrentUser().getEmail().equals(email));
        
        setAdmin(UsersManager.isAdmin());
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
}
