package foodcenter.server;

import javax.jdo.PersistenceManager;

public class ThreadLocalPM
{
    private static ThreadLocal<PersistenceManager> holder = new ThreadLocal<PersistenceManager>();

    private ThreadLocalPM()
    {
    }

    public static PersistenceManager get()
    {
        return holder.get();
    }

    public static void set(PersistenceManager pm)
    {
        holder.set(pm);
    }
}
