package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.modules.DbChannelToken;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.UsersManager;
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

        // else save a new user to the db and return it
        DbUser user = UsersManager.getDbUser();
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
        DbUser user = UsersManager.getDbUser();
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
        User user = UsersManager.getUser();
        order.setUserEmail(user.getEmail());
        String rBranchId = order.getRestBranchId();
        if (null == rBranchId)
        {
            logger.debug(ServiceError.INVALID_REST_BRANCH_ID + rBranchId);
            throw new ServiceError(ServiceError.INVALID_REST_BRANCH_ID + rBranchId);
        }
        DbRestaurantBranch rBranch = DbHandler.find(DbRestaurantBranch.class, rBranchId);
        if (null == rBranch)
        {
            logger.debug(ServiceError.INVALID_REST_BRANCH_ID + rBranchId);
            throw new ServiceError(ServiceError.INVALID_REST_BRANCH_ID + rBranchId);
        }
        order.setRestBranchAddr(rBranch.getAddress());

        String restId = order.getRestId();
        DbRestaurant rest = rBranch.getRestaurant();
        if (null == restId || null == rest || !rest.getId().equals(restId))
        {
            logger.debug(ServiceError.INVALID_REST_ID + restId);
            throw new ServiceError(ServiceError.INVALID_REST_ID + restId);
        }

        order.setRestName(rest.getName());

        DbCompanyBranch cBranch = findUserCompanyBranch(user.getEmail());

        order.setCompBranchId(cBranch.getId());
        order.setCompBranchAddr(cBranch.getAddress());

        DbCompany comp = cBranch.getCompany();
        if (null == comp)
        {
            logger.warn(ServiceError.COMPANY_NOT_ASSOCIATED_TO_BRANCH );
            throw new ServiceError(ServiceError.COMPANY_NOT_ASSOCIATED_TO_BRANCH);
        }
        order.setCompId(comp.getId());
        order.setCompName(comp.getName());

        // Save the order (using 1 transaction!)
        order = DbHandler.save(order);
        if (null == order)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save order");
            throw new IllegalAccessError(ServiceError.DATABASE_ISSUE);
        }
        broadcastOrder(order);

        return order;
    }

    public static List<DbOrder> getOrders(Integer startIdx, Integer endIdx)
    {
        String userEmail = UsersManager.getUser().getEmail();

        String query = "userEmail == userEmailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("userEmailP", userEmail));

        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
        sort.add(new SortOrder("date", SortOrderDirection.DESC));

        List<DbOrder> res = DbHandler.find(DbOrder.class, query, params, sort, startIdx, endIdx);
        return res;
    }

    /* ************************* Restaurant APIs *************************** */

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        logger.info("getDefaultRestaurants is called");
        List<DbRestaurant> res = DbHandler.find(DbRestaurant.class, 10);
        if (null == res)
        {
            throw new ServiceError(ServiceError.DEFAULT_RESTS_NOT_FOUND);
        }
        return res;
    }

    public static DbRestaurant getRestaurantById(String id)
    {
        logger.info("getRestaurantById is called, id: " + id);
        DbRestaurant rest = DbHandler.find(DbRestaurant.class, id);
        if (null == rest)
        {
            logger.warn(ServiceError.INVALID_REST_ID + id);
            throw new ServiceError(ServiceError.INVALID_REST_ID + id);
        }
        return rest;
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

        List<DbRestaurant> res = DbHandler.find(DbRestaurant.class, //
                              query.toString(),
                              declaredParams,
                              null, // sort order
                              20); // limit num of results...
        if (null == res)
        {
            throw new ServiceError(ServiceError.REST_PATTERN_NOT_FOUND);
        }
        return res;
    }

    /* ************************* Companies APIs *************************** */

    public static List<DbCompany> getDefaultCompanies()
    {
        logger.info("getDefaultRestaurants is called");
        List<DbCompany> res = DbHandler.find(DbCompany.class, 10);
        if (null == res)
        {
            logger.debug(ServiceError.DEFAULT_COMPS_NOT_FOUND);
            throw new ServiceError(ServiceError.DEFAULT_COMPS_NOT_FOUND);
        }
        return res;
        
    }

    public static DbCompany getCompanyById(String id)
    {
        logger.info("getCompanyById is called, id: " + id);
        DbCompany res = DbHandler.find(DbCompany.class, id);
        if (null == res)
        {
            logger.warn(ServiceError.INVALID_COMP_ID + id);
            throw new ServiceError(ServiceError.INVALID_COMP_ID + id);
        }
        return res;
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

        List<DbCompany> res = DbHandler.find(DbCompany.class, //
                              query.toString(),
                              declaredParams,
                              null,
                              20); // limit num of results...
        
        if (null == res)
        {
            throw new ServiceError(ServiceError.COMP_PATTERN_NOT_FOUND);
        }
        return res;
    }

    /* ******************************************************************************* */
    /* **************************** private functions ******************************** */
    /* ******************************************************************************* */

    protected static void broadcastOrder(DbOrder order)
    {
        String query = "branchId == branchIdP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("branchIdP", order.getRestBranchId()));

        List<DbChannelToken> tokens = DbHandler.find(DbChannelToken.class,
                                                     query,
                                                     params,
                                                     null,
                                                     Integer.MAX_VALUE);
        if ((null == tokens) || tokens.isEmpty())
        {
            return;
        }

        ChannelService channelService = ChannelServiceFactory.getChannelService();
        for (DbChannelToken t : tokens)
        {
            channelService.sendMessage(new ChannelMessage(t.getKey(), order.getId()));
        }
    }

    protected static DbCompanyBranch findUserCompanyBranch(String email)
    {
        String query = "workers == emailP";
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));
        DbCompanyBranch res = DbHandler.find(DbCompanyBranch.class, query, params);
        if (null == res)
        {
            throw new ServiceError(ServiceError.USER_COMPNAY_NOT_FOUND + email);
        }
        return res;
    }

}
