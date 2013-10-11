package foodcenter.client.panels.restaurant.branch.orders;

import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;

public class CoursesInfoPanel extends VerticalPanel
{
    public CoursesInfoPanel(OrderProxy order)
    {
        super();

        List<CourseOrderProxy> courses = order.getCourses();
        if (null == courses || courses.isEmpty())
        {
            return;
        }

        for (CourseOrderProxy c : courses)
        {
            add(new Label(c.getCnt() + "x " + c.getName()));
        }
    }
}
