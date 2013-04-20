package foodcenter.server.db.modules;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @deprecated use {@link DbUser} instead
 */
@Deprecated
@Entity
public class DbUserGcm
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String gcmKey;

    public DbUserGcm()
    {
        // empty ctor
    }

    public DbUserGcm(String email, String gcmKey)
    {
        this.email = email;
        this.gcmKey = gcmKey;
    }

    public Long getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getGcmKey()
    {
        return gcmKey;
    }

    public void setGcmKey(String gcmKey)
    {
        this.gcmKey = gcmKey;
    }

}
