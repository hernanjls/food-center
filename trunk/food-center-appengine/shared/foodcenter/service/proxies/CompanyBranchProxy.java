package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractGeoLocationInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbCompanyBranch", locator = "foodcenter.server.db.DbObjectLocator")
public interface CompanyBranchProxy extends AbstractGeoLocationInterface, EntityProxy
{
    public final static String[] BRANCH_WITH = {};

    public List<String> getAdmins();

    public void setAdmins(List<String> admins);

    public List<String> getWorkers();

    public void setWorkers(List<String> workers);

    public List<ServiceType> getServices();

    public void setServices(List<ServiceType> services);

    public String getPhone();

    public void setPhone(String phone);
}
