package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class DbChannelToken extends AbstractDbObject
{

    private static final long serialVersionUID = 7709293965188132272L;

    @Persistent
    private String key = "";

    @Persistent
    private String branchId = "";
    
    @Persistent
    private String token = "";
    
    public DbChannelToken()
    {
        super();
    }

    public DbChannelToken(String key, String branchId, String token)
    {
        this.key = key;
        this.branchId = branchId;
        this.token = token;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    
    public String getBranchId()
    {
        return branchId;
    }

    public void setBranchId(String branchId)
    {
        this.branchId = branchId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
