package foodcenter.service.request;

import org.junit.After;
import org.junit.Before;

import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;
import com.google.web.bindery.requestfactory.server.testing.InProcessRequestTransport;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;

import foodcenter.client.service.WebRequestUtils;
import foodcenter.server.AbstractGAETest;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

public abstract class AbstractRequestTest extends AbstractGAETest
{
    protected FoodCenterRequestFactory rf = null;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();
        rf = createRF(FoodCenterRequestFactory.class);
    };

    @Override
    @After
    public void tearDown()
    {
        super.tearDown();
    }

    protected final <T extends RequestFactory> T createRF(Class<T> requestFactoryClass)
    {
        ServiceLayer serviceLayer = ServiceLayer.create();
        SimpleRequestProcessor processor = new SimpleRequestProcessor(serviceLayer);
        T factory = RequestFactorySource.create(requestFactoryClass);
        factory.initialize(new SimpleEventBus(), new InProcessRequestTransport(processor));
        return factory;
    }

    protected CourseProxy createCourse(RequestContext service, String name, double price)
    {
        CourseProxy course = service.create(CourseProxy.class);
        course.setName(name);
        course.setPrice(price);
        return course;
    }

    protected RestaurantBranchProxy createRestBranch(RequestContext service,
                                                     int numBranchMenuCats,
                                                     int numBranchMenuCatCourses)
    {
        RestaurantBranchProxy branch = WebRequestUtils.createRestaurantBranchProxy(service);
        branch.setAddress("addr" + Math.random());
        for (int j = 0; j < numBranchMenuCats; ++j)
        {

            MenuCategoryProxy category = WebRequestUtils.createMenuCategoryProxy(service);
            category.setCategoryTitle("branch" + Math.random());

            branch.getMenu().getCategories().add(category);
            for (int k = 0; k < numBranchMenuCatCourses; ++k)
            {
                CourseProxy course = createCourse(service,
                                                  "branch_course" + Math.random(),
                                                  12.2 + 10 * Math.random());
                category.getCourses().add(course);
            }
        }
        return branch;
    }

    protected RestaurantProxy createRest(RequestContext service,
                                         String name,
                                         int menuCats,
                                         int menuCatCourses,
                                         int numBranches,
                                         int numBranchMenuCats,
                                         int numBranchMenuCatCourses)
    {

        RestaurantProxy r = WebRequestUtils.createRestaurantProxy(service);

        r.setName(name);

        for (int i = 0; i < menuCats; ++i)
        {
            MenuCategoryProxy category = WebRequestUtils.createMenuCategoryProxy(service);
            category.setCategoryTitle("rest" + Math.random());

            r.getMenu().getCategories().add(category);

            for (int j = 0; j < menuCatCourses; ++j)
            {
                CourseProxy course = service.create(CourseProxy.class);
                course.setName("course" + Math.random());
                course.setPrice(12.2 + 10 * Math.random());
                category.getCourses().add(course);
            }
        }

        for (int i = 0; i < numBranches; ++i)
        {
            RestaurantBranchProxy branch = createRestBranch(service,
                                                            numBranchMenuCats,
                                                            numBranchMenuCatCourses);

            r.getBranches().add(branch);
            // branch.setRestaurant(r);
        }

        return r;
    }

    protected OrderProxy createOrder(RequestContext rContext,
                                     RestaurantBranchProxy branch,
                                     int maxCourses)
    {
        OrderProxy order = WebRequestUtils.createOrder(rContext);
        int cnt = 0;

        int numCats = branch.getMenu().getCategories().size();
        for (int i = 0; i < numCats; ++i)
        {
            MenuCategoryProxy cat = branch.getMenu().getCategories().get(i);
            int numCourses = cat.getCourses().size();
            for (int j = 0; j < numCourses; ++j)
            {
                CourseProxy course = cat.getCourses().get(j);
                order.getCourses().add(course.getId());
                cnt++;
                if (cnt >= maxCourses)
                {
                    return order;
                }
            }
        }
        return order;
    }

    protected CompanyBranchProxy createCompBranch(RequestContext service)
    {
        CompanyBranchProxy branch = WebRequestUtils.createCompanyBranchProxy(service);
        branch.setAddress("addr" + Math.random());
        return branch;
    }

    protected CompanyProxy createComp(RequestContext service, String name, int numBranches)
    {

        CompanyProxy r = WebRequestUtils.createCompanyProxy(service);
        r.setName(name);

        for (int i = 0; i < numBranches; ++i)
        {
            CompanyBranchProxy branch = createCompBranch(service);
            r.getBranches().add(branch);
        }

        return r;
    }

}
