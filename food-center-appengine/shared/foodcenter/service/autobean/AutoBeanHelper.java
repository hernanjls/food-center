package foodcenter.service.autobean;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * In GWT code, use GWT.create(OrderBroadcastAutoBeanFactory.class);
 * In non-GWT code, use AutoBeanFactorySource.create(OrderBroadcastAutoBeanFactory.class);
 * @param factory
 */
public class AutoBeanHelper
{
        
    public static <T> String serializeToJson(T obj)
    {
        if (null == obj)
        {
            return null;
        }
        
        // Retrieve the AutoBean controller
        AutoBean<T> bean = AutoBeanUtils.getAutoBean(obj);

        return AutoBeanCodex.encode(bean).getPayload();
    }

    public static <T> T deserializeFromJson(AutoBeanFactory factory, Class<T> clazz, String json)
    {
        if (null == factory)
        {
            return null;
        }
        
        AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, json);
        return bean.as();
    }

}
