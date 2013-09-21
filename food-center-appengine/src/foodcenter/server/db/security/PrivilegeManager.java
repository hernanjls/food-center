package foodcenter.server.db.security;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbUser;

public class PrivilegeManager
{
    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public static User getUser()
    {
        return userService.getCurrentUser();
    }
    
    public static DbUser getCurrentUser()
    {
        return getDbUser(userService.getCurrentUser().getEmail());
    }

    public static DbUser getDbUser(String email)
    {
        logger.info("getDbUser is called");

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        DbUser res = DbHandler.find(DbUser.class, query, params);
        if (null != res)
        {
            res.setAdmin(userService.isUserAdmin());
        }
        return res;
    }

    /**
     * @param user
     * @return {@link UserPrivilege#Admin}, {@link UserPrivilege#NotPremited} or
     *         {@link UserPrivilege#User}
     */
    public static UserPrivilege getPrivilege(DbUser user)
    {
        if (null == user)
        {
            return UserPrivilege.NotPremited;
        }
        if (user.isAdmin())
        {
            return UserPrivilege.Admin;
        }
        return UserPrivilege.User;
    }

    /**
     * @return {@link UserPrivilege#Admin}, {@link UserPrivilege#NotPremited} or
     *         {@link UserPrivilege#User}
     */
    public static UserPrivilege getPrivilege()
    {
        if (userService.isUserAdmin())
        {
            return UserPrivilege.Admin;
        }
        DbUser user = getCurrentUser();
        return getPrivilege(user);
    }

    public static UserPrivilege getPrivilege(DbRestaurant rest)
    {
        if (userService.isUserAdmin())
        {
            return UserPrivilege.Admin;
        }
        DbUser user = getCurrentUser();
        UserPrivilege result = getPrivilege(user);
        if (UserPrivilege.User != result)
        {
            return result;
        }
        return getPrivilege(rest, user.getEmail());
    }

    public static UserPrivilege getPrivilege(DbRestaurantBranch branch)
    {
        if (userService.isUserAdmin())
        {
            return UserPrivilege.Admin;
        }
        DbUser user = getCurrentUser();
        UserPrivilege result = getPrivilege(user);
        if (UserPrivilege.User != result)
        {
            return result;
        }
        return getPrivilege(branch, user.getEmail());
    }

    public static UserPrivilege getPrivilege(DbCompany comp)
    {
        if (userService.isUserAdmin())
        {
            return UserPrivilege.Admin;
        }
        DbUser user = getCurrentUser();
        UserPrivilege result = getPrivilege(user);
        if (UserPrivilege.User != result)
        {
            return result;
        }
        return getPrivilege(comp, user.getEmail());
    }

    public static UserPrivilege getPrivilege(DbCompanyBranch branch)
    {
        if (userService.isUserAdmin())
        {
            return UserPrivilege.Admin;
        }
        DbUser user = getCurrentUser();
        UserPrivilege result = getPrivilege(user);
        if (UserPrivilege.User != result)
        {
            return result;
        }
        return getPrivilege(branch, user.getEmail());
    }

    /***********************************************************************************/

    private static UserPrivilege getPrivilege(DbRestaurant restaurant, String email)
    {
        if (null == restaurant)
        {
            return UserPrivilege.NotPremited;
        }
        if (restaurant.getAdmins().contains(email))
        {
            return UserPrivilege.RestaurantAdmin;
        }
        return UserPrivilege.User;
    }

    private static UserPrivilege getPrivilege(DbRestaurantBranch branch, String email)
    {
        UserPrivilege p = getPrivilege();
        if (UserPrivilege.User != p)
        {
            return p;
        }

        if (branch.getAdmins().contains(email))
        {
            return UserPrivilege.RestaurantBranchAdmin;
        }

        if (branch.getWaiters().contains(email))
        {
            return UserPrivilege.RestaurantWaiter;
        }

        if (branch.getChefs().contains(email))
        {
            return UserPrivilege.RestaurantChef;
        }

        return UserPrivilege.User;
    }

    private static UserPrivilege getPrivilege(DbCompany company, String email)
    {
        if (null == company)
        {
            return UserPrivilege.NotPremited;
        }
        if (company.getAdmins().contains(email))
        {
            return UserPrivilege.CompanyAdmin;
        }
        return UserPrivilege.User;
    }

    private static UserPrivilege getPrivilege(DbCompanyBranch branch, String email)
    {
        UserPrivilege p = getPrivilege();
        if (UserPrivilege.User != p)
        {
            return p;
        }

        if (branch.getAdmins().contains(email))
        {
            return UserPrivilege.CompanyBranchAdmin;
        }

        return UserPrivilege.User;
    }

}
