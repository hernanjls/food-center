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
    
    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_PRICE = 1;
    private static final int COLUMN_BUTTON_ADD_COURSE = 2;
    private static final int COLUMN_BUTTON_DEL_COURSE = 2;

    private final MenuCategoryProxy menuCatProxy;
    private final RequestContext requestContext;
    private final Boolean isEditMode;

    public CoursesFlexTable(RequestContext requestContext, MenuCategoryProxy menuCatProxy, Boolean isEditMode)
    {
        super();
        
        this.requestContext = requestContext;
        this.menuCatProxy = menuCatProxy;
        this.isEditMode = isEditMode;
        
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
        this.setText(0, COLUMN_NAME, "name");
        this.setText(0, COLUMN_PRICE, "price");
        if (isEditMode)
        {
            Button addCourseButton = new Button("Add Course");
            addCourseButton.addClickHandler(new AddCourseHandler());
            this.setWidget(0, COLUMN_BUTTON_ADD_COURSE, addCourseButton);
        }
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
        setWidget(row, COLUMN_NAME, name);
        
        TextBox price = new TextBox();
        Double coursePrice = courseProxy.getPrice();
        if (null != coursePrice)
        {
            price.setText(coursePrice.toString());
        }
        setWidget(row, COLUMN_PRICE, price);
        
        if (isEditMode)
        {
            price.addKeyUpHandler(new CoursePriceKeyUpHandler(price, courseProxy));
            
            Button delete = new Button("delete");
            delete.addClickHandler(new DeleteCourseClickHandler(row));
            setWidget(row, COLUMN_BUTTON_DEL_COURSE, delete);
        }
        else
        {
            name.setEnabled(false);
            price.setEnabled(false);            
        }
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
