package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.modules.DbChannelToken;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.service.enums.OrderStatus;

public class RestaurantWorkerService extends ClientService
{

    private static final Logger logger = LoggerFactory.getLogger(RestaurantWorkerService.class);
    public static DbOrder cancelOrder(String orderId)
    {
        logger.info("cancel order, orderId=" + orderId);
        
        DbOrder order = DbHandler.find(DbOrder.class, orderId);
        if (null == order)
        {
            return null;
        }
        String restId = order.getRestId();
        String branchId = order.getRestBranchId();
        if (!isBranchOrdersPrivilage(restId, branchId))
        {
            return null;
        }

        order.setStatus(OrderStatus.CANCELD);
        order = DbHandler.save(order);

        notifyUser(order);

        return order;
    }

    public static DbOrder deliverOrder(String orderId)
    {
        logger.info("deliver order, orderId=" + orderId);
        
        DbOrder order = DbHandler.find(DbOrder.class, orderId);
        if (null == order)
        {
            return null;
        }
        String branchId = order.getRestBranchId();
        String restId = order.getRestId();
        if (!isBranchOrdersPrivilage(restId, branchId))
        {
            return null;
        }

        order.setStatus(OrderStatus.DELIVERED);

        order = DbHandler.save(order);

        notifyUser(order);
        return order;
    }

    public static List<DbOrder> getPendingOrders(String branchId)
    {
        if (!isBranchOrdersPrivilage(null, branchId))
        {
            return new ArrayList<DbOrder>();
        }
        
        logger.info("get pending orders, branchId=" + branchId);
        
        // perform the query
        String query = "(restBranchId == restBranchIdP)";
        query += "&& (status == statusP)";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("restBranchIdP", branchId));
        params.add(new DeclaredParameter("statusP", OrderStatus.CREATED.toString()));

        ArrayList<SortOrder> orders = new ArrayList<SortOrder>();
        orders.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbOrder.class, query, params, orders, Integer.MAX_VALUE);
    }

    public static DbOrder getOrderById(String orderId)
    {
        logger.info("get order by id, orderId=" + orderId);
        
        DbOrder order = DbHandler.find(DbOrder.class, orderId);
        if (null == order)
        {
            return null;
        }

        String branchId = order.getRestBranchId();
        if (!isBranchOrdersPrivilage(null, branchId))
        {
            return null;
        }

        return order;
    }

    public static String createChannel(String branchId)
    {
        logger.info("create channel: " + branchId);
        
        if (!isBranchOrdersPrivilage(null, branchId))
        {
            return null;
        }

        String key = PrivilegeManager.getUser().getUserId();

        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String token = channelService.createChannel(key);

        String query = "key == keyP";
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("keyP", key));

        DbChannelToken channelToken = DbHandler.find(DbChannelToken.class, query, params);
        if (null == channelToken)
        {
            channelToken = DbHandler.save(new DbChannelToken(key, branchId, token));
        }
        else if (!branchId.equals(channelToken.getBranchId()) || !key.equals(channelToken.getKey())
                 || !token.equals(channelToken.getToken()))
        {
            channelToken.setBranchId(branchId);
            channelToken.setKey(key);
            channelToken.setToken(token);
            channelToken = DbHandler.save(channelToken);
        }

        if (null == channelToken)
        {
            return null;
        }
        return channelToken.getToken();
    }

    private static void notifyUser(DbOrder order)
    {
        if (null == order)
        {
            return;
        }

        logger.info("notify user, orderId=" + order.getId() + ", order status=" + order.getStatus());
        
        StringBuilder builder = new StringBuilder();
        builder.append("Your order from ");
        builder.append(order.getRestName());
        builder.append(" is ");
        builder.append(order.getStatus().getName());

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", order.getUserEmail()));

        DbUser user = DbHandler.find(DbUser.class, query, params);
        // user should never be null!
        GCMSender.send(builder.toString(), user.getGcmKey(), 5);

    }

    public static boolean isBranchOrdersPrivilage(String restId, String branchId)
    {
        if (null == branchId)
        {
            return false;
        }

        DbRestaurantBranch branch = DbHandler.find(DbRestaurantBranch.class, branchId);
        if (null == branch)
        {
            return false;
        }

        // make sure user is elligable to get branch orders
        User user = PrivilegeManager.getUser();
        boolean isChef = (null != branch.getChefs()) && branch.getChefs().contains(user.getEmail());
        if (branch.isEditable() || isChef)
        {
            return true;
        }

        DbRestaurant rest = null;
        if (null != restId)
        {
            rest = DbHandler.find(DbRestaurant.class, restId);
        }
        else
        {
            String query = "branches == branchP";
            ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
            params.add(new DeclaredParameter("branchP", branch));
            rest = DbHandler.find(DbRestaurant.class, query, params);
        }

        return (null != rest && rest.isEditable());
    }

}
