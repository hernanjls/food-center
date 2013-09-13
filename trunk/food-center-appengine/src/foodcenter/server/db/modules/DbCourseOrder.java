package foodcenter.server.db.modules;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable
@Inheritance(customStrategy="complete-table")   //store it in new table
public class DbCourseOrder extends DbCourse
{

    /**
     * 
     */
    private static final long serialVersionUID = -5866545173812383595L;

    private static final Logger logger = LoggerFactory.getLogger(DbCourseOrder.class);

    @Persistent
    private String courseId;

    @Persistent
    private int cnt;

    public DbCourseOrder()
    {
        super();
        logger.trace("new DbCourseOrder()");
    }

    public String getCourseId()
    {
        return courseId;
    }

    public void setCourseId(String courseId)
    {
        this.courseId = courseId;
    }

    public int getCnt()
    {
        return cnt;
    }

    public void setCnt(int cnt)
    {
        this.cnt = cnt;
    }
}
