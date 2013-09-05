package foodcenter.service;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

import foodcenter.service.requset.AdminServiceRequest;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;
import foodcenter.service.requset.RestaurantChefServiceRequest;
import foodcenter.service.requset.RestaurantWaiterServiceRequest;



public interface FoodCenterRequestFactory extends RequestFactory
{
	// employ/client services
    public ClientServiceRequest getClientService();
    
    //manage service
    public AdminServiceRequest getAdminService();
    
    // services for restaurant/ branches management    
    public RestaurantBranchAdminServiceRequest getRestaurantBranchAdminService();
    public RestaurantAdminServiceRequest getRestaurantAdminService();
    public RestaurantWaiterServiceRequest getRestaurantWaiterService();
    public RestaurantChefServiceRequest getRestaurantChefService();
    
    // services for companies / branches management
    public CompanyBranchAdminServiceRequest getCompanyBranchAdminService();
    public CompanyAdminServiceRequest getCompanyAdminService();
}
