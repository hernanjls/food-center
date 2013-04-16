package foodcenter.server.service.common;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.server.db.modules.AbstractDbObject;

@PersistenceCapable
public class LoginInfo extends AbstractDbObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8798412992264727451L;

	@Persistent
	private String logoutUrl;
	
	@Persistent
	private String nickName;
	
	@Persistent
	private String email;
	
	@Persistent
	private String userId;
	
	@Persistent
	private Boolean isAdmin;

	public LoginInfo()
	{
		// empty ctor
	}

	public String getLogoutUrl()
	{
		return logoutUrl;
	}

	public String getNickName()
	{
		return nickName;
	}

	public String getEmail()
	{
		return email;
	}

	public String getUserId()
	{
		return userId;
	}

	public Boolean isAdmin()
	{
		return isAdmin;
	}

	public void setLogoutUrl(String logoutUrl)
	{
		this.logoutUrl = logoutUrl;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public void setAdmin(Boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}

}
