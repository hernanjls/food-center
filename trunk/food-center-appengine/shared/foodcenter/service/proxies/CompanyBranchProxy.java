package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractGeoLocationInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbCompanyBranch", locator = "foodcenter.server.db.DbObjectLocator")
public interface CompanyBranchProxy extends AbstractGeoLocationInterface, EntityProxy
{

    public CompanyProxy getCompany();

    public void setCompany(CompanyProxy company);

    public List<String> getAdmins();

    public void setAdmins(List<String> admins);

    public List<String> getEmployees();

    public void setEmployees(List<String> employees);

    // public List<String> getOrders();
    //
    // public void setOrders(List<String> orders);

    public List<ServiceType> getServices();

    public void setServices(List<ServiceType> services);

    public String getPhone();

    public void setPhone(String phone);

}
