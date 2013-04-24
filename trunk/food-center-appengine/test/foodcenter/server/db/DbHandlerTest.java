package foodcenter.server.db;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import foodcenter.server.db.modules.DbCourse;
import foodcenter.server.db.modules.DbMenu;
import foodcenter.server.db.modules.DbMenuCategory;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;

public class DbHandlerTest
{

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private static final int NUM_RESTS = 3;
	private DbRestaurant[] rests = new DbRestaurant[NUM_RESTS];
	private DbHandlerImp db = null;
	@Before
	public void setUp()
	{
		helper.setUp();

		db = new DbHandlerImp();
		
		for (int i = 0; i < NUM_RESTS; ++i)
		{
			rests[i] = createRest("test" + i);
		}
	}

	private DbRestaurant createRest(String name)
	{
		DbCourse course = new DbCourse();
		course.setName("course");
		course.setPrice(12.2);

		List<DbCourse> courses = new LinkedList<DbCourse>();
		courses.add(course);

		DbMenuCategory c = new DbMenuCategory();
		c.setCategoryTitle("x");
		c.setCourses(courses);

		List<DbMenuCategory> cats = new LinkedList<DbMenuCategory>();
		cats.add(c);

		DbMenu menu = new DbMenu();
		menu.setCategories(cats);

		DbRestaurant r = new DbRestaurant();
		r.setName(name);
		r.setMenu(menu);

		return r;
	}

	
	@After
	public void tearDown()
	{
		helper.tearDown();
	}

	@Test
	public void saveFindTest()
	{
		DbRestaurant r = rests[0];
		
		// make sure all the rest fields are valid
		assertNotNull(r.getMenu());
		assertNotNull(r.getMenu().getCategories());
		assertTrue(r.getMenu().getCategories().size() != 0);

		// save the restaurant 
		db.save(r);

		DbRestaurant f = db.find(DbRestaurant.class, r.getId());
		
		assertNotNull(f);
		assertEquals(r.getId(), f.getId());
		assertNotNull(f.getMenu());
		assertNotNull(f.getMenu().getCategories());
		assertTrue(f.getMenu().getCategories().size() != 0);

		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu()));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0)));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0).getCourses().get(0)));
		
		
		f.setName("xzczcx" + Math.random());
		f = db.save(f);
		
		assertNotNull(f);
		assertEquals(r.getId(), f.getId());
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu()));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0)));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0).getCourses().get(0)));
		

		f = db.find(DbRestaurant.class, r.getId());

		assertNotNull(f.getMenu());
		assertNotNull(f.getMenu().getCategories());
		assertTrue(f.getMenu().getCategories().size() != 0);

		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu()));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0)));
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(f.getMenu().getCategories().get(0).getCourses().get(0)));

	}

	@Test
	public void findManyTest()
	{
		db.save(rests[0]);
		db.save(rests[1]);
		db.save(rests[2]);
		
		List<DbRestaurant> result = db.find(DbRestaurant.class, null, null, null, null);
		Collections.sort(result, new DbRestaurantComparator());
		
		for (int i=0; i< NUM_RESTS; ++i)
		{
			assertNotNull(result.get(i));
			
			assertEquals(rests[i].getId(), result.get(i).getId());
			assertEquals(result.get(i).getName(), rests[i].getName());
			
			assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(result.get(i)));
			
			assertNotNull(result.get(i).getMenu());
			assertNotNull(result.get(i).getMenu().getCategories());
			assertTrue(result.get(i).getMenu().getCategories().size() != 0);
		}
	}
	
	@Test
	public void multipleBranchesTest()
	{
		DbRestaurant r = rests[0];
		DbRestaurantBranch b1 = new DbRestaurantBranch();
		DbRestaurantBranch b2 = new DbRestaurantBranch();
		
		b1.setAddress("addr");
		b2.setAddress("addr2");
		
		r.getBranches().add(b1);
		
		
		r = db.save(r);
		
		DbRestaurant result = db.find(DbRestaurant.class, r.getId());
		
		assertNotNull(result.getBranches());
		assertEquals(1, result.getBranches().size());
		
		result.getBranches().add(b2);
		result = db.save(result);
		
		result = db.find(DbRestaurant.class, r.getId());
		assertNotNull(result.getBranches());
		assertEquals(2, result.getBranches().size());
		
	}
}


class DbRestaurantComparator implements Comparator<DbRestaurant>
{

	@Override
    public int compare(DbRestaurant o1, DbRestaurant o2)
    {
	    return o1.getName().compareTo(o2.getName());
    }
	
}
