package foodcenter.server.db.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.listener.LoadCallback;
import javax.validation.constraints.NotNull;

import org.slf4j.LoggerFactory;

import foodcenter.server.FileManager;
import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.server.db.security.UserPrivilege;
import foodcenter.server.service.servlet.ImageServlet;
import foodcenter.service.enums.ServiceType;

@PersistenceCapable(detachable = "true")
public class DbRestaurant extends AbstractDbObject implements LoadCallback
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -9053099508081246189L;

    public static final String DEFAULT_ICON_PATH = "images/default_restaurant_icon.png";

    @Persistent
    @NotNull
    private String name = "";

    @Persistent
    private DbMenu menu = new DbMenu();

    @Persistent
    private String imageKey = null;

    @NotPersistent
    private String imageUrl = DEFAULT_ICON_PATH;

    @Persistent
    private String phone = "";

    @Persistent
    // (mappedBy = "restaurant")
    private List<DbRestaurantBranch> branches = new ArrayList<DbRestaurantBranch>();

    @Persistent
    private List<String> admins = new ArrayList<String>(); // emails

    @Persistent
    private List<ServiceType> services = new ArrayList<ServiceType>();

    public DbRestaurant()
    {
        super();
    }

    public DbRestaurant(String name)
    {
        this();
        this.name = name;
    }

    @Override
    public void jdoPostLoad()
    {
        // Set privilege...
        UserPrivilege p = PrivilegeManager.getPrivilege(this);
        boolean b = UserPrivilege.Admin == p || UserPrivilege.RestaurantAdmin == p;
        setEditable(b);

        if (null != imageKey)
        {
            imageUrl = "/blobservlet?" //
                       + ImageServlet.BLOB_SERVE_KEY
                       + "="
                       + imageKey;
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public DbMenu getMenu()
    {
        return menu;
    }

    public void setMenu(DbMenu menu)
    {
        this.menu = menu;
    }

    public String getImageKey()
    {
        return imageKey;
    }

    public void setImageKey(String imageKey)
    {
        this.imageKey = imageKey;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public List<DbRestaurantBranch> getBranches()
    {
        return branches;
    }

    public void setBranches(List<DbRestaurantBranch> branches)
    {
        this.branches = branches;
    }

    public List<String> getAdmins()
    {
        return admins;
    }

    public void setAdmins(List<String> admins)
    {
        this.admins = admins;
    }

    public List<ServiceType> getServices()
    {
        return services;
    }

    public void setServices(List<ServiceType> services)
    {
        this.services = services;
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
        }
    }
}
