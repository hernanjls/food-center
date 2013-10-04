package foodcenter.service.requset;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.TableReservationProxy;

@ServiceName(value = "foodcenter.server.service.RestaurantWorkerService")
public interface RestaurantChefServiceRequest extends RequestContext
{

    public Request<List<OrderProxy>> getPendingOrders(String branchId);
    public Request<List<TableReservationProxy>> getPendingReservations(String branchId);
    
    public Request<OrderProxy> cancelOrder(String orderId);
    public Request<TableReservationProxy> declineReservation(String reservationId);
    
    public Request<OrderProxy> deliverOrder(String orderId);
    public Request<TableReservationProxy> confirmReservation(String reservationId, int hr, int min);
    
    public Request<String> createChannel(String branchId);
    
    public Request<OrderProxy> getOrderById(String orderId);
    public Request<TableReservationProxy> getReservationById(String reservationId);

}
