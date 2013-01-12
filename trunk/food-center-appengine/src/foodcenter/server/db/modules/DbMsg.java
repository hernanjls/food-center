package foodcenter.server.db.modules;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class DbMsg
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String msg;
	
	public DbMsg()
	{
		// empty ctor
	}
	
	public DbMsg(String msg)
	{
		this.msg = msg;
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
}
