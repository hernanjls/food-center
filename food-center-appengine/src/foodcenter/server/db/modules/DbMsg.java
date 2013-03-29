package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable
public class DbMsg extends DbObject
{	    
    
    /**
     * 
     */
    private static final long serialVersionUID = 566494833043920482L;

    @Persistent
    private String msg;
    
    @Persistent
	private String email;

    
	public DbMsg()
	{
		// empty ctor
	    super();
	}
	
	public DbMsg(String email, String msg)
	{
		this.msg = msg;
		this.setEmail(email);
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
