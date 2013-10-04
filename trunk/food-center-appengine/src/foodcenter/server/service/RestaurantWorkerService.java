package foodcenter.server.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.modules.DbChannelToken;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbTableReservation;
import foodcenter.server.db.security.UsersManager;
import foodcenter.service.autobean.OrderBroadcastType;
import foodcenter.service.enums.OrderStatus;
import foodcenter.service.enums.TableReservationStatus;

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
        CommonServices.broadcastToRestaurant(order, OrderBroadcastType.ORDER);
        
        return order;
    }

    public static DbTableReservation declineReservation(String reservationId)
    {
        logger.info("decline reservation, reservationId=" + reservationId);
        
        DbTableReservation reservation = DbHandler.find(DbTableReservation.class, reservationId);
        if (null == reservation)
        {
            logger.error(ServiceError.INVALID_RESERVATION_ID+ reservationId);
            throw new ServiceError(ServiceError.INVALID_RESERVATION_ID+ reservationId);
        }
        String branchId = reservation.getRestBranchId();
        checkBranchChef(branchId); //TODO change to checkBranchWaiter

        reservation.setStatus(TableReservationStatus.DECLINED);
        reservation = DbHandler.save(reservation);
        if (null == reservation)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save order");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
        notifyUsers(reservation);
        CommonServices.broadcastToRestaurant(reservation, OrderBroadcastType.TABLE);
        
        return reservation;
        
    }
    public static DbTableReservation confirmReservation(String reservationId, int hr, int min)
    {
        logger.info("confirm reservation, reservationId=" + reservationId);
        
        DbTableReservation reservation = DbHandler.find(DbTableReservation.class, reservationId);
        if (null == reservation)
        {
            logger.error(ServiceError.INVALID_RESERVATION_ID + reservationId);
            throw new ServiceError(ServiceError.INVALID_RESERVATION_ID + reservationId);
        }
        String branchId = reservation.getRestBranchId();
        checkBranchChef(branchId); // TODO change to checkBranchWaiter
        
        reservation.setStatus(TableReservationStatus.CONFIRMED);
        Date from = reservation.getFromDate();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(from.getTime());
        c.set(Calendar.HOUR, hr);
        c.set(Calendar.MINUTE, min);
        
        reservation.setConfirmationDate(c.getTime());
        reservation = DbHandler.save(reservation);
        if (null == reservation)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " confirm reservation");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }

        notifyUsers(reservation);
        CommonServices.broadcastToRestaurant(reservation, OrderBroadcastType.TABLE);
        return reservation;
        
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
        CommonServices.broadcastToRestaurant(order, OrderBroadcastType.ORDER);
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

    public static List<DbTableReservation> getPendingReservations(String branchId)
    {
        logger.info("get pending orders, branchId=" + branchId);

        checkBranchChef(branchId);        //TODO change to checkBranchWaiter
        
        // perform the query
        String query = "(restBranchId == restBranchIdP)";
        query += "&& (status == statusP)";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("restBranchIdP", branchId));
        params.add(new DeclaredParameter("statusP", TableReservationStatus.CREATED.toString()));

        ArrayList<SortOrder> orders = new ArrayList<SortOrder>();
        orders.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbTableReservation.class, query, params, orders, Integer.MAX_VALUE);
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

    public static DbTableReservation getReservationById(String reservationId)
    {
        logger.info("get order by id, orderId=" + reservationId);
        if (null == reservationId)
        {
            logger.error(ServiceError.INVALID_ORDER_ID + reservationId);
            throw new ServiceError(ServiceError.INVALID_ORDER_ID + reservationId);
        }
        
        DbTableReservation reservation = DbHandler.find(DbTableReservation.class, reservationId);
        if (null == reservation)
        {
            logger.error(ServiceError.INVALID_RESERVATION_ID + reservationId);
            throw new ServiceError(ServiceError.INVALID_RESERVATION_ID + reservationId);
        }

        String branchId = reservation.getRestBranchId();
        checkBranchChef(branchId); //TODO change to checkBranchWaiter

        return reservation;
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

    
    private static void notifyUsers(DbTableReservation reservation)
    {
        logger.info("notify users, reservationId=" + reservation.getId() + ", order status=" + reservation.getStatus());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        StringBuilder builder = new StringBuilder();
        builder.append("Table reservations: ");
        builder.append(reservation.getStatus().getName() + "\n");
        Date d = reservation.getConfirmationDate();
        builder.append("at time: " + dateFormatter.format(d) + "\n");
        builder.append(reservation.getRestName() + "\n");
        builder.append("addr: " + reservation.getRestBranchAddr() + "\n");
        builder.append("Participents:\n");
        builder.append(" " + reservation.getUserEmail() + "\n");
        for (String s : reservation.getUsers())
        {
            builder.append(" " + s + "\n");
        }

        List<String> emails = new LinkedList<String>();
        emails.add(reservation.getUserEmail());
        emails.addAll(reservation.getUsers());
        
        CommonServices.broadcastToUsers(builder.toString(), emails.toArray(new String[0]));
    }
    
    private static void notifyUser(DbOrder order)
    {
        logger.info("notify user, orderId=" + order.getId() + ", order status=" + order.getStatus());
        
        StringBuilder builder = new StringBuilder();
        builder.append("Your order from ");
        builder.append(order.getRestName());
        builder.append(" is ");
        builder.append(order.getStatus().getName());

        CommonServices.broadcastToUsers(builder.toString(), order.getUserEmail());
    }

    private static void checkBranchChef(String branchId)
    {
        if (null == branchId)
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " "
                        + UsersManager.getUser().getEmail().toLowerCase());
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
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail().toLowerCase());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
    }

}
