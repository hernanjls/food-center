package foodcenter.android.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

import foodcenter.android.ObjectStore;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;

/**
 * Allow passing the order information to another activity <br>
 * using {@link ObjectStore} without passing {@link RequestFactory}.
 */
public class OrderData
{
    /** {@link ObjectStore} key to store and retrieve this order */
    public static final String CACHE_KEY = OrderData.class.getSimpleName();

    /** service type */
    private ServiceType service; 
    
    /** restaurant id */
    private String restId;
    
    /** restaurant branch id */
    private String restBranchId;

    /** position -> counter */
    private final Map<Integer, Integer> counters = new TreeMap<Integer, Integer>();
    private final List<CourseProxy> courses = new LinkedList<CourseProxy>();

    /** adds a course and fix counters */
    public void addCourse(CourseProxy course, int cnt)
    {
        courses.add(course);
        int pos = courses.size() - 1;
        counters.put(pos, cnt);
    }

    /** set the service type of this order */
    public void setService(ServiceType service)
    {
        this.service = service;
    }

    /** get the service type of this order */
    public ServiceType getService()
    {
        return service;
    }

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
     * get the courses in this order <br>
     * use {@link #addCourse(CourseProxy, int)} to add course
     */
    public List<CourseProxy> getCourses()
    {
        return courses;
    }

    /** get counters of orders (position -> counter) */
    public Map<Integer, Integer> getCounters()
    {
        return counters;
    }
}
