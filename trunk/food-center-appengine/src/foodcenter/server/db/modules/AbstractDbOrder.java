package foodcenter.server.db.modules;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.server.db.security.UsersManager;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDbOrder extends AbstractDbObject
{

    /**
     * 
     */
    private static final long serialVersionUID = -4769679028694487301L;

    @Persistent
    private String userEmail; // user which made this order

    @Persistent
    private String compId;

    @Persistent
    private String compName = "";

    @Persistent
    private String compBranchId;

    @Persistent
    private String compBranchAddr = "";

    @Persistent
    private String restId;

    @Persistent
    private String restName = "";

    @Persistent
    private String restBranchId;

    @Persistent
    private String restBranchAddr = "";

    public AbstractDbOrder()
    {
        super();
    }

    @Override
    public void jdoPreStore()
    {
        super.jdoPreStore();
        
        if (null == userEmail)
        {
            userEmail = UsersManager.getUser().getEmail();
        }

    }
    
    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public String getCompId()
    {
        return compId;
    }

    public void setCompId(String compId)
    {
        this.compId = compId;
    }

    public String getCompName()
    {
        return compName;
    }

    public void setCompName(String compName)
    {
        this.compName = compName;
    }

    public String getCompBranchId()
    {
        return compBranchId;
    }

    public void setCompBranchId(String compBranchId)
    {
        this.compBranchId = compBranchId;
    }

    public String getCompBranchAddr()
    {
        return compBranchAddr;
    }

    public void setCompBranchAddr(String compBranchAddr)
    {
        this.compBranchAddr = compBranchAddr;
    }

    public String getRestId()
    {
        return restId;
    }

    public void setRestId(String restId)
    {
        this.restId = restId;
    }

    public String getRestName()
    {
        return restName;
    }

    public void setRestName(String restName)
    {
        this.restName = restName;
    }

    public String getRestBranchId()
    {
        return restBranchId;
    }

    public void setRestBranchId(String restBranchId)
    {
        this.restBranchId = restBranchId;
    }

    public String getRestBranchAddr()
    {
        return restBranchAddr;
    }

    public void setRestBranchAddr(String restBranchAddr)
    {
        this.restBranchAddr = restBranchAddr;
    }

}
