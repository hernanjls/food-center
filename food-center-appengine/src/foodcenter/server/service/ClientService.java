package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.service.enums.ServiceType;

public class ClientService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;

    public static String getLogoutUrl()
    {
        String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";
        return userService.createLogoutURL("/") + logoutRedirectionUrl;
    }

    public static User getCurrentUser()
    {
        return userService.getCurrentUser();
    }

    public static DbUser login(String gcmKey)
    {

        // find user by email

        // else save a new user to the db and return it
        DbUser user = PrivilegeManager.getCurrentUser();
        if (null == user)
        {
            String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";

            user = new DbUser();

            user.setEmail(userService.getCurrentUser().getEmail());
            user.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
            user.setUserId(userService.getCurrentUser().getUserId());

        }
        else
        {
            logger.info("User already exsists");

        }

        logger.info("Login info: " + user.getEmail());

        user.setNickName(userService.getCurrentUser().getNickname());
        user.setAdmin(userService.isUserAdmin());

        if (null != gcmKey)
        {
            user.setGcmKey(gcmKey);
        }

        return DbHandler.save(user);
    }

    public static void logout()
    {
        DbUser user = PrivilegeManager.getCurrentUser();
        ;
        if (null == user)
        {
            return;

        }
        user.setGcmKey("");
        DbHandler.save(user);
    }

    public static DbOrder makeOrder(DbOrder order)
    {
        logger.info("makeOrder is called");
        if (null == order)
        {
            return null;
        }

        // Set the user of the current order
        DbUser user = PrivilegeManager.getCurrentUser();
        if (null == user)
        {
            return null;
        }

        order.setUserId(user.getId());
        
        DbCompanyBranch branch = findUserCompanyBranch(user.getEmail());
        if (null == branch)
        {
            return null;
        }
        
        order.setCompBranchId(branch.getId());
        
        DbCompany comp = findCompanyOfBranch(branch);
        if (null == comp)
        {
            return null;
        }
        
        // This is needed because we are using cross transactions with the company
        PMF.closeThreadLocal();
        PMF.initThreadLocal();
        order.setCompId(comp.getId());

        // Save the order
        return DbHandler.save(order);
    }

    public static List<DbOrder> getOrders(int startIdx, int endIdx)
    {
        DbUser user = PrivilegeManager.getCurrentUser();

        String query = "userId == userIdP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("userIdP", user.getId()));

        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
        sort.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbOrder.class, query, params, sort, startIdx, endIdx);
    }

    /* ************************* Restaurant APIs *************************** */

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        logger.info("getDefaultRestaurants is called");
        List<DbRestaurant> res = DbHandler.find(DbRestaurant.class, 10);
        return res;
    }

    public static DbRestaurant getRestaurantById(String id)
    {
        logger.info("getRestaurantById is called, id: " + id);
        return DbHandler.find(DbRestaurant.class, id);
    }

    public static List<DbRestaurant> findRestaurant(String pattern, List<ServiceType> services)
    {
        StringBuilder query = new StringBuilder();

        ArrayList<DeclaredParameter> declaredParams = new ArrayList<DeclaredParameter>();
        if (null != pattern && pattern.length() > 0)
        {
            query.append("name.startsWith(patternP)");
            declaredParams.add(new DeclaredParameter("patternP", pattern));
        }

        if (null != services && !services.isEmpty())
        {
            if (null != pattern && pattern.length() > 0)
            {
                query.append(" && ");
            }
            query.append("(");

            int n = services.size();
            for (int i = 0; i < n; ++i)
            {
                query.append("services == serviceP" + i);
                if (i < n - 1)
                {
                    query.append(" || ");
                }

                declaredParams.add(new DeclaredParameter("serviceP" + i, services.get(i).toString()));
            }
            query.append(" )");
        }

        return DbHandler.find(DbRestaurant.class, //
                              query.toString(),
                              declaredParams,
                              null, // sort order
                              20); // limit num of results...
    }

    /* ************************* Companies APIs *************************** */

    public static List<DbCompany> getDefaultCompanies()
    {
        logger.info("getDefaultRestaurants is called");
        return DbHandler.find(DbCompany.class, 10);
    }

    public static DbCompany getCompanyById(String id)
    {
        logger.info("getCompanyById is called, id: " + id);
        return DbHandler.find(DbCompany.class, id);
    }

    public static List<DbCompany> findCompany(String pattern, List<ServiceType> services)
    {
        StringBuilder query = new StringBuilder();
        ArrayList<DeclaredParameter> declaredParams = new ArrayList<DeclaredParameter>();
        if (null != pattern && pattern.length() > 0)
        {
            query.append("name.startsWith(patternP)");
            declaredParams.add(new DeclaredParameter("patternP", pattern));
        }

        if (null != services && !services.isEmpty())
        {
            if (null != pattern && pattern.length() > 0)
            {
                query.append(" && ");
            }
            query.append("(");

            int n = services.size();
            for (int i = 0; i < n; ++i)
            {
                query.append("services == serviceP" + i);
                if (i < n - 1)
                {
                    query.append(" || ");
                }

                declaredParams.add(new DeclaredParameter("serviceP" + i, services.get(i).toString()));
            }
            query.append(" )");
        }

        return DbHandler.find(DbCompany.class, //
                              query.toString(),
                              declaredParams,
                              null,
                              20); // limit num of results...
    }

    /* ******************************************************************************* */
    /* **************************** private functions ******************************** */
    /* ******************************************************************************* */
    
    protected static DbCompany findCompanyOfBranch(DbCompanyBranch branch)
    {
        if (null == branch)
        {
            return null;
        }
        
        String query = "branches == branchP";
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", branch));
        return DbHandler.find(DbCompany.class, query, params);
    }
    
    protected static DbCompanyBranch findUserCompanyBranch(String email)
    {
        String query = "workers == emailP";
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));
        return DbHandler.find(DbCompanyBranch.class, query, params);
    }

}
