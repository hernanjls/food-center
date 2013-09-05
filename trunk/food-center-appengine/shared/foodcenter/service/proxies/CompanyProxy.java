package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbCompany", locator = "foodcenter.server.db.DbObjectLocator")
public interface CompanyProxy extends EntityProxy, AbstractEntityInterface
{
    public final static String[] COMP_WITH = { "branches", };

     public String getId();

     public String getName();

     public void setName(String name);

     public String getPhone();

     public void setPhone(String phone);

     public List<ServiceType> getServices();

     public void setServices(List<ServiceType> services);

     public List<CompanyBranchProxy> getBranches();

     public void setBranches(List<CompanyBranchProxy> branches);

     public List<String> getAdmins();

     public void setAdmins(List<String> admins);
}
