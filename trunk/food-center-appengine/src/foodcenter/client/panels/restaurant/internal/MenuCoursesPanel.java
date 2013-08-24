package foodcenter.client.panels.restaurant.internal;

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

public class MenuCoursesPanel extends FlexTable
{

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_PRICE = 1;
    private static final int COLUMN_BUTTON_ADD_COURSE = 2;
    private static final int COLUMN_BUTTON_DEL_COURSE = 2;

    private final MenuCategoryProxy menuCatProxy;
    private final RequestContext requestContext;
    private final Boolean isEditMode;

    public MenuCoursesPanel(MenuCategoryProxy menuCatProxy)
    {
        this(menuCatProxy, null);
    }

    public MenuCoursesPanel(MenuCategoryProxy menuCatProxy, RequestContext requestContext)
    {
        super();

        this.requestContext = requestContext;
        this.menuCatProxy = menuCatProxy;
        this.isEditMode = (null != requestContext);

        redraw();
    }

    private void redraw()
    {
        clear();
        
        createHeader();

        List<CourseProxy> courses = menuCatProxy.getCourses();
        if (null == courses)
        {
            return;
        }
        
        int row = getRowCount();
        for (CourseProxy cp : courses)
        {
            printCourseRow(cp, row);
            ++row;
        }
    }

    private void createHeader()
    {
        this.setText(0, COLUMN_NAME, "name");
        this.setText(0, COLUMN_PRICE, "price");
        if (isEditMode)
        {
            Button addCourseButton = new Button("Add Course");
            addCourseButton.addClickHandler(new OnClickAddCourse());
            this.setWidget(0, COLUMN_BUTTON_ADD_COURSE, addCourseButton);
        }
    }

    public void addCourse(int row)
    {
        CourseProxy courseProxy = requestContext.create(CourseProxy.class);
        menuCatProxy.getCourses().add(courseProxy);
        printCourseRow(courseProxy, row);
    }

    public void deleteCourse(int row)
    {
        List<CourseProxy> courses = menuCatProxy.getCourses();
        courses.remove(row - 1);
        redraw();
    }

    public void printCourseRow(CourseProxy courseProxy, int row)
    {
        TextBox name = new TextBox();
        name.setText(courseProxy.getName());
        name.addKeyUpHandler(new OnKeyUpCourseName(name, courseProxy));
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
            price.addKeyUpHandler(new OnKeyUpCoursePrice(price, courseProxy));

            Button delete = new Button("delete", new OnClickDelCourse(row));
            setWidget(row, COLUMN_BUTTON_DEL_COURSE, delete);
        }
        else
        {
            name.setEnabled(false);
            price.setEnabled(false);
        }
    }

    class OnClickAddCourse implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addCourse(getRowCount());
        }
    }

    class OnClickDelCourse implements ClickHandler
    {

        private final int row;

        public OnClickDelCourse(int row)
        {
            this.row = row;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            deleteCourse(row);
        }
    }

    class OnKeyUpCourseName implements KeyUpHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;

        public OnKeyUpCourseName(TextBox titleBox, CourseProxy course)
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

    class OnKeyUpCoursePrice implements KeyUpHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;

        public OnKeyUpCoursePrice(TextBox titleBox, CourseProxy course)
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
