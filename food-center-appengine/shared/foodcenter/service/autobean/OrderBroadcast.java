package foodcenter.service.autobean;

public interface OrderBroadcast
{
    public String getId();
    public void setId(String id);
    
    public OrderBroadcastType getType();
    public void setType(OrderBroadcastType type);
}
