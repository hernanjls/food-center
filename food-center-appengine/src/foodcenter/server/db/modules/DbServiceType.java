package foodcenter.server.db.modules;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public enum DbServiceType
{
    TAKE_AWAY,
    TABLE,
    DELIVERY
}
