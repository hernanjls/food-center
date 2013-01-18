package foodcenter.server.db.modules;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
@SuppressWarnings("serial")
public class DbMsg implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String msg;
	private String email;
	
	public DbMsg()
	{
		// empty ctor
	}
	
	public DbMsg(String email, String msg)
	{
		this.msg = msg;
		this.setEmail(email);
	}
	
	public Long getId()
	{
		return id;
	}
	
	public String getMsg()
	{
		return msg;
	}
	
	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String getEmail()
    {
	    return email;
    }

	public void setEmail(String email)
    {
	    this.email = email;
    }
}
