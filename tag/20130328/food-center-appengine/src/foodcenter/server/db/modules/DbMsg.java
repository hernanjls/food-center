package foodcenter.server.db.modules;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Version;



@Entity
@SuppressWarnings("serial")
public class DbMsg implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Version
	@Column(name = "version")
	private Long version = 0L;
	
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
	
	/**
     * Auto-increment version # whenever persisted
     */
    @PrePersist
    void onPersist()
    {
        this.version++;
    }
	
    public Long getVersion() 
	{  
	    return version;  
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
