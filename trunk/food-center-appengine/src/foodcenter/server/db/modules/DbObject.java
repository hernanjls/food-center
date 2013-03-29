package foodcenter.server.db.modules;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class DbObject implements StoreCallback, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8572051412245793349L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent()
    private Integer version = 0;
    
    public DbObject()
    {
        // empty ctor
    }
    
    /**
     * Auto-increment version # whenever persisted
     */
    @Override
    public void jdoPreStore() 
    {
        this.version++;
    };
    
    public final Long getId()
    {
        return id;
    }
    
    public final Integer getVersion() 
    {  
        return version;  
    }

}
