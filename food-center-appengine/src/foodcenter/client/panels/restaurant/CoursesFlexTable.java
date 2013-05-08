package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;

public class CoursesFlexTable extends FlexTable
{

    private final MenuCategoryProxy menuCatProxy;
    private final RequestContext requestContext;
    private final Boolean isAdmin;

    public CoursesFlexTable(RequestContext requestContext, MenuCategoryProxy menuCatProxy, Boolean isAdmin)
    {
        super();
        
        this.requestContext = requestContext;
        this.menuCatProxy = menuCatProxy;
        this.isAdmin = isAdmin;
        
        if (null == menuCatProxy)
        {
        	return;
        }
        
        createHeader();
        
        List<CourseProxy> courses = menuCatProxy.getCourses();
        if (null == courses)
        {
        	return;
        }
        for (CourseProxy cp : courses)
        {
            printCourseRow(cp);
        }
        
    }

    private void createHeader()
    {
        this.setText(0, 0, "name");
        this.setText(0, 1, "price");
        Button addCourseButton = new Button("Add Course");
        addCourseButton.addClickHandler(new AddCourseHandler());
        this.setWidget(0, 2, addCourseButton);
    }

    public void addCourse()
    {
        CourseProxy courseProxy = requestContext.create(CourseProxy.class);
        menuCatProxy.getCourses().add(courseProxy);
        printCourseRow(courseProxy);
    }

    public void deleteCourse(int row)
    {
        List<CourseProxy> courses = menuCatProxy.getCourses();
        courses.remove(row - 1);
        removeRow(row);
    }

    public void printCourseRow(CourseProxy courseProxy)
    {
        int row = getRowCount();

        TextBox name = new TextBox();
        name.setText(courseProxy.getName());
        name.addKeyUpHandler(new CourseNameKeyUpHandler(name, courseProxy));
        
        TextBox price = new TextBox();
        Double coursePrice = courseProxy.getPrice();
        if (null != coursePrice)
        {
            price.setText(coursePrice.toString());
        }
        price.addKeyUpHandler(new CoursePriceKeyUpHandler(price, courseProxy));
        
        Button delete = new Button("delete");
        delete.addClickHandler(new DeleteCourseClickHandler(row));

        name.setEnabled(isAdmin);
        price.setEnabled(isAdmin);
        delete.setEnabled(isAdmin);

        setWidget(row, 0, name);
        setWidget(row, 1, price);
        setWidget(row, 2, delete);

    }

    class AddCourseHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addCourse();
        }
    }

    class DeleteCourseClickHandler implements ClickHandler
    {

        private final int row;

        public DeleteCourseClickHandler(int row)
        {
            this.row = row;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            deleteCourse(row);
        }
    }

    class CourseNameKeyUpHandler implements KeyUpHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;

        public CourseNameKeyUpHandler(TextBox titleBox, CourseProxy course)
        {
            this.titleBox = titleBox;
            this.course = course;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            course.setName(titleBox.getText());
        }

    }

    class CoursePriceKeyUpHandler implements KeyUpHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;

        public CoursePriceKeyUpHandler(TextBox titleBox, CourseProxy course)
        {
            this.titleBox = titleBox;
            this.course = course;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            Double price = 0.0;
            try
            {
                price = Double.parseDouble(titleBox.getText());
            }
            catch (Exception e)
            {
                // TODO javascript log parse double error ?
            }
            course.setPrice(price);
        }

    }
}
