package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable
public class DbCourseOrder extends AbstractDbObject
{

    /**
     * 
     */
    private static final long serialVersionUID = 6195953622747507407L;

    private static final Logger logger = LoggerFactory.getLogger(DbCourseOrder.class);

    @Persistent
    private String name;

    @Persistent
    private String info;

    @Persistent
    private Double price;

    @Persistent
    private String courseId;

    @Persistent
    private Integer cnt;

    public DbCourseOrder()
    {
        super();
        logger.trace("new DbCourseOrder()");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public Double getPrice()
    {
        return price;
    }

    public void setPrice(Double price)
    {
        this.price = price;
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
