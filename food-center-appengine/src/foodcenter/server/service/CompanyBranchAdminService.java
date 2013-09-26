package foodcenter.server.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.security.UsersManager;

public class CompanyBranchAdminService
{
    
    private static Logger logger = LoggerFactory.getLogger(CompanyAdminService.class);
    
    public static DbCompanyBranch saveCompanyBranch(DbCompanyBranch branch)
    {
        if (!branch.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        DbCompanyBranch res = DbHandler.save(branch);
        if (null == res)
        {
            logger.warn(ServiceError.DATABASE_ISSUE + " save comp branch");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
        return res;
    }
    
    
    public static List<DbOrder> getOrders(String branchId, Date from, Date to)
    {
        checkBranchAdmin(branchId);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        from = calendar.getTime();
        
        calendar.setTime(to);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        to = calendar.getTime();
        
        logger.info("get orders, comp branchId=" + branchId
                    + ", from: "
                    + from.toString()
                    + ", to: "
                    + to.toString());
        

        String query = "compBranchId == branchIdP && date >= fromP && date <= toP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("branchIdP", branchId));
        params.add(new DeclaredParameter("fromP", from));
        params.add(new DeclaredParameter("toP", to));

        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
        sort.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbOrder.class, query, params, sort, Integer.MAX_VALUE);
    }
    
    private static void checkBranchAdmin(String branchId)
    {
        if (null == branchId)
        {
            logger.warn(ServiceError.INVALID_COMP_BRANCH_ID + branchId);
            throw new ServiceError(ServiceError.INVALID_COMP_BRANCH_ID + branchId);
        }

        DbCompanyBranch branch = DbHandler.find(DbCompanyBranch.class, branchId);
        if (null == branch)
        {
            logger.warn(ServiceError.INVALID_COMP_BRANCH_ID + branchId);
            throw new ServiceError(ServiceError.INVALID_COMP_BRANCH_ID + branchId);
        }
        if (!branch.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
    }
}
