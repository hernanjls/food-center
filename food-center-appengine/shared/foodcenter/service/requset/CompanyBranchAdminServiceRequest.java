package foodcenter.service.requset;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.OrderProxy;

@ServiceName(value="foodcenter.server.service.CompanyBranchAdminService")
public interface CompanyBranchAdminServiceRequest extends RequestContext
{

    public Request<CompanyBranchProxy> saveCompanyBranch(CompanyBranchProxy branch);
    
    public Request<List<OrderProxy>> getOrders(String branchId, Date from, Date to);
}
