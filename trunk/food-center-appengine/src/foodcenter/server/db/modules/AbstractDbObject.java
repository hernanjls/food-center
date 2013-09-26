package foodcenter.server.db.modules;

import java.io.IOException;
import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.LoadCallback;
import javax.jdo.listener.StoreCallback;

import org.slf4j.LoggerFactory;

import foodcenter.server.FileManager;
import foodcenter.server.db.security.UsersManager;
import foodcenter.server.service.servlet.ImageServlet;
import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class AbstractDbObject implements StoreCallback, Serializable,
                                      AbstractEntityInterface, LoadCallback
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7630576459671031595L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String id = null;

    @Persistent
    private Integer version = 0;

    @NotPersistent
    private Boolean editable;

    @Persistent
    private String imageKey = null;

    @NotPersistent
    private String imageUrl = "";

    
    public AbstractDbObject()
    {
        editable = UsersManager.isAdmin();
        imageUrl = "";
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

    @Override
    public void jdoPostLoad()
    {
        if (null != imageKey)
        {
            buildImageUrl();
        }

    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public Integer getVersion()
    {
        return version;
    }

    @Override
    public Boolean isEditable()
    {
        return editable;
    }
    
    @Override
    public void setEditable(Boolean editable)
    {
        this.editable = editable;
    }
    
    
    public String getImageKey()
    {
        return imageKey;
    }

    public void setImageKey(String imageKey)
    {
        // Delete old image if exists!
        deleteImage();
        
        if (null != imageKey)
        {
            // Set the image key
            this.imageKey = imageKey;
            
            // Set the image url 
            buildImageUrl();
        }
    }

    @Override
    public String getImageUrl()
    {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public void deleteImage()
    {
        if (null != imageKey)
        {
            try
            {
                FileManager.deleteFile(imageKey);
                imageKey = null;
            }
            catch (NullPointerException | IOException e)
            {
                LoggerFactory.getLogger(getClass()).error("Can't delete Image", e);
            }
            imageUrl = "";
        }
    }
    
    private void buildImageUrl()
    {
        imageUrl = ("/blobservlet?" //
            + ImageServlet.BLOB_SERVE_KEY
            + "=" + imageKey);
    }
}
