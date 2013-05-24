package foodcenter.server.db;

import foodcenter.server.AbstractGAETest;
import foodcenter.server.db.modules.DbCourse;
import foodcenter.server.db.modules.DbMenuCategory;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;

public abstract class AbstractDbTest extends AbstractGAETest
{

	protected DbRestaurant createRest(String name, //
	    int numMenuCats, //
	    int numMenuCourses, //
	    int numBranches, //
	    int numBranchMenuCats, //
	    int numBranchMenuCourses)
	{

		DbRestaurant r = new DbRestaurant();

		for (int i = 0; i < numMenuCats; ++i)
		{
			DbMenuCategory category = new DbMenuCategory("rest" + Math.random());
			r.getMenu().getCategories().add(category);

			for (int j = 0; j < numMenuCourses; ++j)
			{
				category.getCourses().add(new DbCourse("course" + Math.random(), 12.2 + 10 * Math.random()));
			}
		}

		for (int i = 0; i < numBranches; ++i)
		{
			DbRestaurantBranch branch = new DbRestaurantBranch();
			r.getBranches().add(branch);

			branch.setAddress("addr" + Math.random());
			for (int j = 0; j < numBranchMenuCats; ++j)
			{
				DbMenuCategory category = new DbMenuCategory("branch" + Math.random());
				branch.getMenu().getCategories().add(category);
				for (int k = 0; k < numBranchMenuCourses; ++k)
				{
					category.getCourses().add(new DbCourse("branch_course" + Math.random(), 12.2 + 10 * Math.random()));
				}
			}
		}

		return r;
	}

}
