package foodcenter.client.callbacks.search;

import java.util.ArrayList;
import java.util.List;

import foodcenter.service.enums.ServiceType;

public class CompanySearchOptions implements SearchOptions
{

    private String pattern;
    private List<ServiceType> services;
    
    public CompanySearchOptions()
    {
        pattern = "";
        setServices(new ArrayList<ServiceType>());
    }
    
    @Override
    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }
    
    public List<ServiceType> getServices()
    {
        return services;
    }

    public void setServices(List<ServiceType> services)
    {
        this.services = services;
    }

    
}
