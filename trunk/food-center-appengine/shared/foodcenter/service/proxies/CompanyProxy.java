package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbCompany", locator = "foodcenter.server.db.DbObjectLocator")
public interface CompanyProxy extends EntityProxy, AbstractEntityInterface
{

    public String getName();

    public void setName(String name);

    public List<Byte> getIconBytes();

    public String getPhone();

    public void setPhone(String phone);

    public List<CompanyBranchProxy> getBranches();

    public void setBranches(List<CompanyBranchProxy> branches);

    public List<String> getAdmins();

    public void setAdmins(List<String> admins);
}
