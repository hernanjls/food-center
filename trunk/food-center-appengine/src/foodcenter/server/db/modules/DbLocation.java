package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class DbLocation extends DbObject
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1868308657772882817L;
	
	private double x;
    private double y;
    
    public DbLocation()
    {
        super();
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
    

}
