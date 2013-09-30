package foodcenter.service.enums;

public enum TableReservationStatus
{
    CREATED("Created"),
    CONFIRMED("Confirmed"),
    DECLINED("Declined"),
    ;
    
    
    private final String name;
    
    private TableReservationStatus(String name)
    {
        this.name = name;
    }
    
    public String getName() 
    {
        return name;
    };
    
    public static TableReservationStatus forName(String name)
    {
        for (TableReservationStatus s : TableReservationStatus.values())
        {
            if (s.getName().equals(name))
            {
                return s;
            }
        }
        return null;
    }

}
