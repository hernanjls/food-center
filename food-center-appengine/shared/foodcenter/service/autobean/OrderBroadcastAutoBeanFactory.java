package foodcenter.service.autobean;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * In GWT code, use GWT.create(OrderBroadcastAutoBeanFactory.class);
 * In non-GWT code, use AutoBeanFactorySource.create(OrderBroadcastAutoBeanFactory.class);
 */
public interface OrderBroadcastAutoBeanFactory extends AutoBeanFactory
{
    // Declare the factory type
    AutoBean<OrderBroadcast> order();
}
