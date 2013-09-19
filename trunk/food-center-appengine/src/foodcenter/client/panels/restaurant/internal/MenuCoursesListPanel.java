package foodcenter.client.panels.restaurant.internal;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.requset.MenuAdminServiceRequest;

public class MenuCoursesListPanel extends FlexTable implements RedrawablePanel
{

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_INFO = 1;
    private static final int COLUMN_PRICE = 2;
    private static final int COLUMN_BUTTON_ADD_COURSE = 3;
    private static final int COLUMN_BUTTON_DEL_COURSE = 3;

    private final List<CourseProxy> courses;
    private final List<CourseProxy> addedCourses;
    private final List<CourseProxy> deletedCourses;

    private final PanelCallback<CourseProxy, MenuAdminServiceRequest> callback;
    private final boolean isEditMode;


    public MenuCoursesListPanel(List<CourseProxy> courses,
                                List<CourseProxy> addedCourses,
                                List<CourseProxy> deletedCourses,
                                PanelCallback<CourseProxy, MenuAdminServiceRequest> callback,
                                boolean isEditMode)
    {
        super();

        this.courses = courses;
        this.addedCourses = addedCourses;
        this.deletedCourses = deletedCourses;
        this.callback = callback;
        this.isEditMode = isEditMode;

        redraw();
    }

    @Override
    public void redraw()
    {
        clear();

        createHeader();

        int row = getRowCount();
        for (CourseProxy cp : courses)
        {
            if (!deletedCourses.contains(cp))
            {
                printCourseRow(cp, row);
                ++row;
            }
        }

        for (CourseProxy cp : addedCourses)
        {
            if (!deletedCourses.contains(cp))
            {
                printCourseRow(cp, row);
                ++row;
            }
        }
    }

    @Override
    public void close()
    {
        callback.error(this, null, "Closing MenuCourseListPanel ??");

    }

    private void createHeader()
    {
        this.setText(0, COLUMN_NAME, "name");
        this.setText(0, COLUMN_INFO, "info");
        this.setText(0, COLUMN_PRICE, "price");
        if (isEditMode)
        {
            Button addCourseButton = new Button("+Course");
            addCourseButton.addClickHandler(new OnClickAddCourse());
            this.setWidget(0, COLUMN_BUTTON_ADD_COURSE, addCourseButton);
        }
    }

    public void printCourseRow(CourseProxy courseProxy, int row)
    {
        TextBox name = new TextBox();
        name.setText(courseProxy.getName());
        setWidget(row, COLUMN_NAME, name);

        TextBox info = new TextBox();
        info.setText(courseProxy.getInfo());

        setWidget(row, COLUMN_INFO, info);

        TextBox price = new TextBox();
        Double coursePrice = courseProxy.getPrice();
        if (null != coursePrice)
        {
            price.setText(coursePrice.toString());
        }
        setWidget(row, COLUMN_PRICE, price);

        if (isEditMode)
        {
            name.addKeyUpHandler(new OnKeyUpCourseName(name, courseProxy));
            info.addKeyUpHandler(new OnKeyUpCourseInfo(info, courseProxy));
            price.addKeyUpHandler(new OnKeyUpCoursePrice(price, courseProxy));

            Button delete = new Button("-", new OnClickDelCourse(courseProxy));
            setWidget(row, COLUMN_BUTTON_DEL_COURSE, delete);
        }
        else
        {
            name.setEnabled(false);
            info.setEnabled(false);
            price.setEnabled(false);
        }
    }

    class OnClickAddCourse implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.createNew(MenuCoursesListPanel.this, callback);
        }
    }

    class OnClickDelCourse implements ClickHandler
    {

        private final CourseProxy course;

        public OnClickDelCourse(CourseProxy course)
        {
            this.course = course;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.del(MenuCoursesListPanel.this, course);
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

    class OnKeyUpCourseInfo implements KeyUpHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;

        public OnKeyUpCourseInfo(TextBox titleBox, CourseProxy course)
        {
            this.titleBox = titleBox;
            this.course = course;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            course.setInfo(titleBox.getText());
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
