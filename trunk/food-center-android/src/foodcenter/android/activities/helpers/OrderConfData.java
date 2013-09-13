package foodcenter.android.activities.helpers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;

public class OrderConfData
{
    public static final String CACHE_KEY = OrderConfData.class.getSimpleName();

    private ServiceType service;
    private String restId;
    private String restBranchId;

    /** position -> counter */
    private final Map<Integer, Integer> counters = new TreeMap<Integer, Integer>();
    private final List<CourseProxy> courses = new LinkedList<CourseProxy>();

    public void addCourse(CourseProxy course, int cnt)
    {
        courses.add(course);
        int pos = courses.size() - 1;
        counters.put(pos, cnt);
    }

    public void setService(ServiceType service)
    {
        this.service = service;
    }

    public ServiceType getService()
    {
        return service;
    }

    public String getRestId()
    {
        return restId;
    }

    public void setRestId(String restId)
    {
        this.restId = restId;
    }

    public String getRestBranchId()
    {
        return restBranchId;
    }

    public void setRestBranchId(String branchId)
    {
        this.restBranchId = branchId;
    }

    public List<CourseProxy> getCourses()
    {
        return courses;
    }

    public Map<Integer, Integer> getCounters()
    {
        return counters;
    }
}
