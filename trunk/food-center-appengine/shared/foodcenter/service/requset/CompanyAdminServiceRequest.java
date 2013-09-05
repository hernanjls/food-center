package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;

@ServiceName(value = "foodcenter.server.service.CompanyAdminService")
public interface CompanyAdminServiceRequest extends CompanyBranchAdminServiceRequest
{
    public Request<Void> addCompanyBranch(CompanyProxy comp, CompanyBranchProxy branch);

    public Request<Void> removeCompanyBranch(CompanyProxy comp, CompanyBranchProxy branch);

    public Request<CompanyProxy> saveCompany(CompanyProxy comp);

}
