package foodcenter.server.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.server.db.modules.DbMenu;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;

public class DbHandlerTest extends AbstractDbTest
{

	private static final int NUM_RESTS = 3;
	private DbRestaurant[] rests = new DbRestaurant[NUM_RESTS];

	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		for (int i = 0; i < NUM_RESTS; ++i)
		{
			rests[i] = createRest("test" + i, 1, 1, 1, 0, 0);
		}
	}

	@After
	@Override
	public void tearDown()
	{
		super.tearDown();
	}

	@Test
	public void saveFindTest()
	{
		DbRestaurant r = rests[0];
		validateRestaurant(r, r, false, false);

		// save the restaurant
		DbHandler.save(r);
		// validateRestaurant(r, r, true, true);

		tearDownPMF();
		setUpPMF();

		DbRestaurant f = DbHandler.find(DbRestaurant.class, r.getId());

		validateRestaurant(r, f, true, true);

		f = DbHandler.save(f);
		validateRestaurant(r, f, true, true);

		tearDownPMF();
		setUpPMF();

		f = DbHandler.find(DbRestaurant.class, r.getId());
		f.setName("xzczcx" + Math.random());
		System.out.println("Object state: " + JDOHelper.getObjectState(f) + ", r: " + JDOHelper.getObjectState(r));

		f = DbHandler.save(f);

		validateRestaurant(r, f, true, false);

		tearDownPMF();
		setUpPMF();

		f = DbHandler.find(DbRestaurant.class, r.getId());

		validateRestaurant(r, f, true, false);

	}

	@Test
	public void findManyTest()
	{
		DbHandler.save(rests[0]);
		DbHandler.save(rests[1]);		
		DbHandler.save(rests[2]);
		
		// make sure each restaurant can be collected using a new transaction
		tearDownPMF();
		setUpPMF();

		DbRestaurant f0 = DbHandler.find(DbRestaurant.class, rests[0].getId());
		validateRestaurant(rests[0], f0, true, false);

		tearDownPMF();
		setUpPMF();

		DbRestaurant f1 = DbHandler.find(DbRestaurant.class, rests[1].getId());
		validateRestaurant(rests[1], f1, true, false);

		tearDownPMF();
		setUpPMF();

		DbRestaurant f2 = DbHandler.find(DbRestaurant.class, rests[2].getId());
		validateRestaurant(rests[2], f2, true, false);

		// get all rests in one transaction
		tearDownPMF();
		setUpPMF();

		DbRestaurant g0 = DbHandler.find(DbRestaurant.class, rests[0].getId());
		validateRestaurant(rests[0], g0, true, false);

		DbRestaurant g1 = DbHandler.find(DbRestaurant.class, rests[1].getId());
		validateRestaurant(rests[1], g1, true, false);

		DbRestaurant g2 = DbHandler.find(DbRestaurant.class, rests[2].getId());
		validateRestaurant(rests[2], g2, true, false);

		tearDownPMF();
		setUpPMF();

		List<DbRestaurant> result = DbHandler.find(DbRestaurant.class, null, null, null, null);
		assertEquals(3, result.size());
		Collections.sort(result, new DbRestaurantComparator());

		assertEquals(NUM_RESTS, result.size());

		for (int i =0; i < NUM_RESTS; ++i)
		{
			validateRestaurant(rests[i], result.get(i), true, true);
		}
	}

	@SuppressWarnings("unchecked")
    @Test
	public void testQuery()
	{
		DbHandler.save(rests[0]);
		tearDownPMF();
		setUpPMF();
		
		DbRestaurant g0 = DbHandler.find(DbRestaurant.class, rests[0].getId());
		validateRestaurant(rests[0], g0, true, false);
		
		tearDownPMF();
		setUpPMF();
		
		validateRestaurant(rests[0], g0, true, false);
		
		tearDownPMF();
		setUpPMF();
		
		PersistenceManager pm = PMF.get();
		Extent<DbRestaurant> extent = pm.getExtent(DbRestaurant.class);
		int size = 0;
		for (DbRestaurant r: extent)
		{
			assertNotNull(r);
			++size;
		}
		assertEquals(1, size);
		
		
		tearDownPMF();
		setUpPMF();
		
		pm = PMF.get();
		Query q = pm.newQuery(DbRestaurant.class);
		List<DbRestaurant> test = (List<DbRestaurant>) q.execute();

		assertEquals(1, test.size());
		
		

	}
	@Test
	public void addMenuCategoryTest()
	{
		DbRestaurant r = createRest("test", 2, 2, 2, 2, 2);
		DbHandler.save(r);

		DbRestaurantBranch toAdd = new DbRestaurantBranch();
		toAdd.setAddress("safdasda");
		r.getBranches().add(toAdd);

		DbHandler.save(r);
		assertEquals(3, r.getBranches().size());

		DbRestaurant res = DbHandler.find(DbRestaurant.class, r.getId());
		assertEquals(3, res.getBranches().size());
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

		r = DbHandler.save(r);

		DbRestaurant result = DbHandler.find(DbRestaurant.class, r.getId());

		assertNotNull(result.getBranches());
		assertEquals(2, result.getBranches().size());

		result.getBranches().add(b2);
		result = DbHandler.save(result);

		result = DbHandler.find(DbRestaurant.class, r.getId());
		assertNotNull(result.getBranches());
		assertEquals(3, result.getBranches().size());
	}

	@Test
	public void restaurantWithMenuAndBranchMenuTest()
	{

		int menuCats = 3;
		int menuCatCourses = 4;
		int numBranches = 2;
		int numBranchMenuCats = 3;
		int numBranchMenuCatCourses = 8;
		DbRestaurant r = createRest("rest", //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);

		assertEquals(menuCats, r.getMenu().getCategories().size());
		DbHandler.save(r);

		DbRestaurant res = DbHandler.find(DbRestaurant.class, r.getId());
		validateRestaurant(r, res, true, true);

		validateRestHirarchy(res, //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);
	}

	@Test
	public void restaurantWithMenuAndBranchMenuAddBranchTest()
	{

		int menuCats = 3;
		int menuCatCourses = 4;
		int numBranches = 2;
		int numBranchMenuCats = 3;
		int numBranchMenuCatCourses = 8;
		DbRestaurant r = createRest("rest", //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);
		DbHandler.save(r);

		r = DbHandler.find(DbRestaurant.class, r.getId());

		DbRestaurant r2 = createRest("rest", //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);

		r.getBranches().add(r2.getBranches().get(0));
		DbHandler.save(r);

		DbRestaurant res = DbHandler.find(DbRestaurant.class, r.getId());

		validateRestHirarchy(res, //
		    menuCats, //
		    menuCatCourses, //
		    numBranches + 1, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);
	}

	@Test
	public void findWithLessThanMaxResults()
	{
		int menuCats = 3;
		int menuCatCourses = 4;
		int numBranches = 2;
		int numBranchMenuCats = 3;
		int numBranchMenuCatCourses = 8;
		DbRestaurant r = createRest("rest", //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);

		DbRestaurant r2 = createRest("rest", //
		    0, //
		    menuCatCourses, //
		    numBranches, //
		    0, //
		    numBranchMenuCatCourses);

		DbHandler.save(r);	
		DbHandler.save(r2);

		List<DbRestaurant> list = DbHandler.find(DbRestaurant.class, 10);
		assertEquals(2, list.size());
		assertNotNull(list);
		
		tearDownPMF();
		setUpPMF();
		
		list = DbHandler.find(DbRestaurant.class, 10);
		assertNotNull(list);
		assertEquals(2, list.size());
		

	}

	@Test
	public void twoEntitiesOnOneTransaction()
	{

		int menuCats = 3;
		int menuCatCourses = 4;
		int numBranches = 2;
		int numBranchMenuCats = 3;
		int numBranchMenuCatCourses = 8;

		DbRestaurant r = createRest("rest", //
		    menuCats, //
		    menuCatCourses, //
		    numBranches, //
		    numBranchMenuCats, //
		    numBranchMenuCatCourses);

		DbHandler.save(r);
		

		List<DbRestaurant> list = DbHandler.find(DbRestaurant.class, 10);
		assertNotNull(list);
		assertEquals(1, list.size());
		
		List<DbMenu> menus = DbHandler.find(DbMenu.class, 10);
		assertNotNull(menus);
		assertEquals(3, menus.size());
		
		
	}
	
	
	public void validateRestaurant(DbRestaurant ref, DbRestaurant o, boolean isValidateId, boolean isValidateName)
	{
		assertNotNull(o);
		if (isValidateId)
		{
			assertEquals(ref.getId(), o.getId());
		}
		logger.info(o.getClass() + " : " + JDOHelper.getObjectState(o));
		// assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o));

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
		logger.info(o.getMenu().getClass() + " : " + JDOHelper.getObjectState(o.getMenu()));
		// assertEquals(ObjectState.TRANSIENT, JDOHelper.getObjectState(o.getMenu()));

		assertNotNull(o.getMenu().getCategories());
		assertNotSame(0, o.getMenu().getCategories().size());
		logger.info(o.getMenu().getCategories().get(0).getClass() + " : " + JDOHelper.getObjectState(o.getMenu().getCategories().get(0)));
		// assertEquals(ObjectState.TRANSIENT,
		// JDOHelper.getObjectState(o.getMenu().getCategories().get(0)));

		assertNotSame(0, o.getMenu().getCategories().get(0).getCourses().size());
		logger.info(o.getMenu().getCategories().get(0).getCourses().get(0).getClass() + " : "
		    + JDOHelper.getObjectState(o.getMenu().getCategories().get(0).getCourses().get(0)));
		// assertEquals(ObjectState.TRANSIENT,
		// JDOHelper.getObjectState(o.getMenu().getCategories().get(0).getCourses().get(0)));
	}

	private void validateRestHirarchy(DbRestaurant rest, //
	    int menuCats, //
	    int menuCatCourses, //
	    int numBranches, //
	    int numBranchMenuCats, //
	    int numBranchMenuCatCourses)
	{
		// validate rest menu
		assertNotNull(rest.getMenu());
		assertNotNull(rest.getMenu().getCategories());
		assertEquals(menuCats, rest.getMenu().getCategories().size());

		for (int i = 0; i < menuCats; ++i)
		{
			assertNotNull(rest.getMenu().getCategories().get(i).getCourses());
			assertEquals(menuCatCourses, rest.getMenu().getCategories().get(i).getCourses().size());
			for (int j = 0; j < menuCatCourses; ++j)
			{
				assertNotNull(rest.getMenu().getCategories().get(i).getCourses().get(j));
			}
		}

		// validate branches
		assertNotNull(rest.getBranches());
		assertEquals(numBranches, rest.getBranches().size());
		for (int i = 0; i < numBranches; ++i)
		{
			assertNotNull(rest.getBranches().get(i));
			assertNotNull(rest.getBranches().get(i).getMenu());
			assertNotNull(rest.getBranches().get(i).getMenu().getCategories());
			assertEquals(numBranchMenuCats, rest.getBranches().get(i).getMenu().getCategories().size());
			for (int j = 0; j < numBranchMenuCats; ++j)
			{
				assertNotNull(rest.getBranches().get(i).getMenu().getCategories().get(j));
				assertNotNull(rest.getBranches().get(i).getMenu().getCategories().get(j).getCourses());
				assertEquals(numBranchMenuCatCourses, rest.getBranches().get(i).getMenu().getCategories().get(j).getCourses().size());
				for (int k = 0; k < numBranchMenuCatCourses; ++k)
				{
					assertNotNull(rest.getBranches().get(i).getMenu().getCategories().get(j).getCourses().get(k));
				}

			}
		}
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
