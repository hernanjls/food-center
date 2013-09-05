package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.CompanyBranchProxy;

@ServiceName(value="foodcenter.server.service.CompanyBranchAdminService")
public interface CompanyBranchAdminServiceRequest extends RequestContext
{

    public Request<CompanyBranchProxy> saveCompanyBranch(CompanyBranchProxy branch);
}
