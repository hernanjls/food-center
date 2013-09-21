package foodcenter.service.enums;

public enum OrderStatus
{
    CREATED("Created"),
    DELIVERED("Delivered"),
    CANCELD("Canceld"),
    ;
    
    
    private final String name;
    
    private OrderStatus(String name)
    {
        this.name = name;
    }
    
    public String getName() 
    {
        return name;
    };
    
    public static ServiceType forName(String name)
    {
        for (ServiceType s : ServiceType.values())
        {
            if (s.getName().equals(name))
            {
                return s;
            }
        }
        return null;
    }
}
