package foodcenter.server.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import foodcenter.server.db.modules.DbCourse;
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
			rests[i] = createRest("test" + i, 1, 1, 1, 0, 0);
		}
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
		validateRestaurant(r, r, false, false);

		// save the restaurant
		db.save(r);
		validateRestaurant(r, r, true, true);

		DbRestaurant f = db.find(DbRestaurant.class, r.getId());
		validateRestaurant(r, f, true, true);

		f = db.save(f);
		validateRestaurant(r, f, true, true);

		f.setName("xzczcx" + Math.random());
		System.out.println("Object state: " + JDOHelper.getObjectState(f) + ", r: " + JDOHelper.getObjectState(r));
		f = db.save(f);
		validateRestaurant(r, f, true, false);

		f = db.find(DbRestaurant.class, r.getId());
		validateRestaurant(r, f, true, false);
	}

	@Test
	public void findManyTest()
	{
		db.save(rests[0]);
		db.save(rests[1]);
		db.save(rests[2]);

		List<DbRestaurant> result = db.find(DbRestaurant.class, null, null, null, null);
		Collections.sort(result, new DbRestaurantComparator());

		for (int i = 0; i < NUM_RESTS; ++i)
		{
			validateRestaurant(rests[i], result.get(i), true, true);
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
		assertEquals(2, result.getBranches().size());

		result.getBranches().add(b2);
		result = db.save(result);

		result = db.find(DbRestaurant.class, r.getId());
		assertNotNull(result.getBranches());
		assertEquals(2, result.getBranches().size());
	}

	@Test
	public void restaurantWithMenuAndBranchMenuTest()
	{
		DbRestaurant r = new DbRestaurant("test");

		for (int i = 0; i < 2; ++i)
		{
			DbMenuCategory mc = new DbMenuCategory("cat" + i);
			mc.getCourses().add(new DbCourse("course1_" + i, 13.4));
			mc.getCourses().add(new DbCourse("course2_" + i, 13.4));
			r.getMenu().getCategories().add(mc);
		}

		for (int i = 0; i < 2; ++i) // add 2 branches
		{
			DbRestaurantBranch b = new DbRestaurantBranch();
			b.setAddress("addr");
			for (int j = 0; j < 2; ++j) // 2 categories for each branch
			{
				DbMenuCategory mc = new DbMenuCategory("branchcat" + i);
				for (int k = 0; k < 2; ++k) // 2 courses for each category
				{
					mc.getCourses().add(new DbCourse("course1_" + i + j + k, 13.4 + 5 * Math.random()));
				}
				b.getMenu().getCategories().add(mc);
			}
			r.getBranches().add(b);

		}

		assertEquals(2, r.getMenu().getCategories().size());
		db.save(r);

		DbRestaurant res = db.find(DbRestaurant.class, r.getId());
		validateRestaurant(r, res, true, true);

		assertNotNull(res.getMenu());
		assertNotNull(res.getMenu().getCategories());
		assertEquals(2, res.getMenu().getCategories().size());

		for (int i = 0; i < 2; ++i)
		{
			assertNotNull(res.getMenu().getCategories().get(i));
			assertNotNull(res.getMenu().getCategories().get(i).getCourses());
			assertEquals(2, res.getMenu().getCategories().get(i).getCourses().size());
		}

		for (int i = 0; i < 2; ++i)
		{
			assertNotNull(res.getBranches().get(i).getMenu());
			assertNotNull(res.getBranches().get(i).getMenu().getCategories());
			assertEquals(2, res.getBranches().get(i).getMenu().getCategories().size());

			for (int j = 0; j < 2; ++j)
			{
				assertNotNull(res.getBranches().get(i).getMenu().getCategories().get(j));
				assertNotNull(res.getBranches().get(i).getMenu().getCategories().get(j).getCourses());
				assertEquals(2, res.getBranches().get(i).getMenu().getCategories().get(j).getCourses().size());
			}
		}
	}

	@Test
	public void addMenuCategoryTest()
	{
		DbRestaurant r = createRest("test", 2, 2, 2, 2, 2);	
		db.save(r);
		
		DbRestaurantBranch toAdd = new DbRestaurantBranch();
		toAdd.setAddress("safdasda");
		r.getBranches().add(toAdd);
		
		db.save(r);
		assertEquals(3, r.getBranches().size());
		
		DbRestaurant res = db.find(DbRestaurant.class, r.getId());
		assertEquals(3, res.getBranches().size());
	}

	private DbRestaurant createRest(String name, //
	    int numMenuCats, //
	    int numMenuCourses, //
	    int numBranches, //
	    int numBranchMenuCats, //
	    int numBranchMenuCourses)
	{

		DbRestaurant r = new DbRestaurant();

		for (int i=0 ; i< numMenuCats; ++i)
		{
			DbMenuCategory category = new DbMenuCategory("rest" + Math.random());
			r.getMenu().getCategories().add(category);
			
			for (int j=0; j< numMenuCourses; ++j)
			{
				category.getCourses().add(new DbCourse("course" + Math.random(), 12.2 + 10* Math.random()));
			}
		}
		

		for (int i=0; i<numBranches; ++i)
		{
			DbRestaurantBranch branch = new DbRestaurantBranch();
			r.getBranches().add(branch);
			
			branch.setAddress("addr" + Math.random());
			for (int j=0; j< numBranchMenuCats; ++j)
			{
				DbMenuCategory category = new DbMenuCategory("branch" + Math.random());
				branch.getMenu().getCategories().add(category);
				for (int k=0; k< numBranchMenuCourses; ++k)
				{
					category.getCourses().add(new DbCourse("branch_course" + Math.random(), 12.2 + 10* Math.random()));
				}	
			}
		}
		

		return r;
	}

	public void validateRestaurant(DbRestaurant ref, DbRestaurant o, boolean isValidateId, boolean isValidateName)
	{
		assertNotNull(o);
		if (isValidateId)
		{
			assertEquals(ref.getId(), o.getId());
		}
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o));

		assertNotNull(o.getName());
		if (isValidateName)
		{
			assertEquals(ref.getName(), o.getName());
		}

		assertNotNull(o.getMenu());
		if (isValidateId)
		{
			assertNotNull(o.getMenu().getId());
		}
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o.getMenu()));

		assertNotNull(o.getMenu().getCategories());
		assertNotSame(0, o.getMenu().getCategories().size());
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o.getMenu().getCategories().get(0)));

		assertNotSame(0, o.getMenu().getCategories().get(0).getCourses().size());
		assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o.getMenu().getCategories().get(0).getCourses().get(0)));
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
