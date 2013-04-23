package foodcenter.server.db.modules;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;
import javax.persistence.Transient;

import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDbObject implements StoreCallback, Serializable, AbstractEntityInterface
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8572051412245793349L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
    
    @Persistent()
    private Integer version = 0;
    
    @Transient
    private Boolean editable = false;
    
    public AbstractDbObject()
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
    
    public final String getId()
    {
        return id;
    }
    
    public final Integer getVersion() 
    {  
        return version;  
    }

	public Boolean isEditable()
    {
	    return editable;
    }

	public void setEditable(Boolean editable)
    {
	    this.editable = editable;
    }
    
}
