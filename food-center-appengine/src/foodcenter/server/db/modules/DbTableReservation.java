package foodcenter.server.db.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import foodcenter.service.enums.TableReservationStatus;

@PersistenceCapable
public class DbTableReservation extends AbstractDbOrder
{

    /**
     * 
     */
    private static final long serialVersionUID = -4697560980672152593L;

    
    @Persistent
    private Date fromDate = null;
    
    @Persistent
    private Date toDate = null;
        
    @Persistent
    private Date confirmationDate = null;
    
    @Persistent
    private TableReservationStatus status;
    
    @Persistent
    private List<String> users = new ArrayList<String>();
    
    public DbTableReservation()
    {
        super();
    }

    @Override
    public void jdoPreStore()
    {
        super.jdoPreStore();
        
        // Set the status for new reservations
        if (null == status)
        {
            status = TableReservationStatus.CREATED;
        }        
    }
    
    public Date getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(Date fromDate)
    {
        this.fromDate = fromDate;
    }

    public Date getToDate()
    {
        return toDate;
    }

    public void setToDate(Date toDate)
    {
        this.toDate = toDate;
    }

    public Date getConfirmationDate()
    {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate)
    {
        this.confirmationDate = confirmationDate;
    }

    public TableReservationStatus getStatus()
    {
        return status;
    }

    public void setStatus(TableReservationStatus status)
    {
        this.status = status;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public void setUsers(List<String> users)
    {
        this.users = users;
    }

}
