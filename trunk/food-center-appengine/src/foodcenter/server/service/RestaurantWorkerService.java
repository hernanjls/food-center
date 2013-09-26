package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.modules.DbChannelToken;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.UsersManager;
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
            logger.error(ServiceError.INVALID_ORDER_ID + orderId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + orderId);
        }
        String branchId = order.getRestBranchId();
        checkBranchChef(branchId);

        order.setStatus(OrderStatus.CANCELD);
        order = DbHandler.save(order);
        if (null == order)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save order");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
        notifyUser(order);

        return order;
    }

    public static DbOrder deliverOrder(String orderId)
    {
        logger.info("deliver order, orderId=" + orderId);
        
        DbOrder order = DbHandler.find(DbOrder.class, orderId);
        if (null == order)
        {
            logger.error(ServiceError.INVALID_ORDER_ID + orderId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + orderId);
        }
        String branchId = order.getRestBranchId();
        checkBranchChef(branchId);
        
        order.setStatus(OrderStatus.DELIVERED);

        order = DbHandler.save(order);
        if (null == order)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save order");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }

        notifyUser(order);
        return order;
    }

    public static List<DbOrder> getPendingOrders(String branchId)
    {
        logger.info("get pending orders, branchId=" + branchId);

        checkBranchChef(branchId);        
        
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
        if (null == orderId)
        {
            logger.error(ServiceError.INVALID_ORDER_ID + orderId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + orderId);
        }
        
        DbOrder order = DbHandler.find(DbOrder.class, orderId);
        if (null == order)
        {
            logger.error(ServiceError.INVALID_ORDER_ID + orderId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + orderId);
        }

        String branchId = order.getRestBranchId();
        checkBranchChef(branchId);

        return order;
    }

    public static String createChannel(String branchId)
    {
        logger.info("create channel: " + branchId);
        if (null == branchId)
        {
            logger.error(ServiceError.INVALID_ORDER_ID + branchId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + branchId);
        }
        checkBranchChef(branchId);
        
        String key = UsersManager.getUser().getUserId();

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
            logger.error(ServiceError.DATABASE_ISSUE + " create channel token");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
        
        return channelToken.getToken();
    }

    private static void notifyUser(DbOrder order)
    {
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

    private static void checkBranchChef(String branchId)
    {
        if (null == branchId)
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }

        DbRestaurantBranch branch = DbHandler.find(DbRestaurantBranch.class, branchId);
        if (null == branch)
        {
            logger.warn(ServiceError.INVALID_REST_BRANCH_ID + " " + branchId);
            throw new ServiceError(ServiceError.INVALID_REST_BRANCH_ID + branchId);
        }
        if (!branch.isChef())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
    }

}
