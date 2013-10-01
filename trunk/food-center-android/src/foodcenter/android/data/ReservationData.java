package foodcenter.android.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foodcenter.android.ObjectStore;

public class ReservationData
{

    /** {@link ObjectStore} key to store and retrieve this order */
    public static final String CACHE_KEY = ReservationData.class.getSimpleName();

    /** Restaurant id */
    private String restId;
    
    /** Restaurant branch id */
    private String restBranchId;

    /** Start of acceptable date to restaurant */
    private Date fromDate;
    
    /** End of acceptable date to come to restaurant */
    private Date toDate;
    
    /** Hold the list of users mails for the reservation */
    private final List<String> users = new ArrayList<String>();
    
    /** get the restaurant id */
    public String getRestId()
    {
        return restId;
    }

    /** set the restaurant id */
    public void setRestId(String restId)
    {
        this.restId = restId;
    }

    /** get the restaurant branch id */
    public String getRestBranchId()
    {
        return restBranchId;
    }

    /** set the restaurant branch id */
    public void setRestBranchId(String branchId)
    {
        this.restBranchId = branchId;
    }

    /** 
     * Get the list of users mails in this reservation <br>
     * add a user directly to this list. 
     */
    public List<String> getUsers()
    {
        return users;
    }

    /** Get start of acceptable date to restaurant */
    public Date getFromDate()
    {
        return fromDate;
    }

    /** Set start of acceptable date to restaurant */
    public void setFromDate(Date fromDate)
    {
        this.fromDate = fromDate;
    }

    /** Get end of acceptable date to restaurant */
    public Date getToDate()
    {
        return toDate;
    }

    /** Set end of acceptable date to restaurant */
    public void setToDate(Date toDate)
    {
        this.toDate = toDate;
    }

}
