package foodcenter.service.proxies.interfaces;

public interface AbstractOrderProxy extends AbstractEntityInterface
{

    public String getUserEmail();

    public String getCompId();

    public String getCompBranchId();

    public String getCompName();

    public String getCompBranchAddr();

    public String getRestId();

    public void setRestId(String restId);
    
    public String getRestName();

    public String getRestBranchId();

    public String getRestBranchAddr();
    
    public void setRestBranchId(String restBranchId);

}
