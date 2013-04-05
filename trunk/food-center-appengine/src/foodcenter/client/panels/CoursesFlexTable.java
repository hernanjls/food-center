package foodcenter.client.panels;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;

public class CoursesFlexTable extends FlexTable
{

    private final MenuCategoryProxy menuCatProxy;
    private final FoodCenterRequestFactory requestFactory;
    
    public CoursesFlexTable(FoodCenterRequestFactory requestFactory, MenuCategoryProxy menuCatProxy)
    {
        super();
        this.requestFactory = requestFactory;
        this.menuCatProxy = menuCatProxy;
        createHeader();
        List<CourseProxy> courses = menuCatProxy.getCourses();
        if (null == courses)
        {
            courses = new LinkedList<CourseProxy>();
            menuCatProxy.setCourses(courses);
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
        CourseProxy courseProxy = requestFactory.getUserCommonService().create(CourseProxy.class);
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
        name.addKeyPressHandler(new CourseNameKeyPressHandler(name, courseProxy)); 
        TextBox price = new TextBox();  
        price.addKeyPressHandler(new CoursePriceKeyPressHandler(price, courseProxy));
        Button delete = new Button("delete"); 
        delete.addClickHandler(new DeleteCourseClickHandler(row));
        
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
    
    class CourseNameKeyPressHandler implements KeyPressHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;
        
        public CourseNameKeyPressHandler(TextBox titleBox, CourseProxy course)
        {
            this.titleBox = titleBox;
            this.course = course;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            course.setName(titleBox.getText());
        }
        
    }
    
    class CoursePriceKeyPressHandler implements KeyPressHandler
    {
        private final TextBox titleBox;
        private final CourseProxy course;
        
        public CoursePriceKeyPressHandler(TextBox titleBox, CourseProxy course)
        {
            this.titleBox = titleBox;
            this.course = course;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            Double price = 0.0;
            try
            {
                price = Double.parseDouble(titleBox.getText());
            }
            catch (Exception e)
            {
                //TODO log e
            }
            course.setPrice(price);
        }
        
    }
}
