package pool;

import java.util.Hashtable;
import java.util.Iterator;

public abstract class ObjectPool {

    private long expirationTime;

    private Hashtable locked;

    private Hashtable unlocked;

    protected ObjectPool() {
        expirationTime = 30 * 1000; // 30 seconds
        locked = new Hashtable();
        unlocked = new Hashtable();
    }

    abstract Object create();

    abstract boolean validate(Object o);

    abstract void expire(Object o);

    @SuppressWarnings("unchecked")
    synchronized Object checkOut() {
        long now = System.currentTimeMillis();
        Iterator iterator = unlocked.keySet().iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            boolean expired = now - ((Long) unlocked.get(o)) > expirationTime;
            iterator.remove();
            if (expired || !validate(o)) {
                expire(o);
            } else {
                locked.put(o, now);
                return o;
            }
        }

        Object o = create();
        locked.put(o, now);
        return o;
    }

    @SuppressWarnings("unchecked")
    synchronized void checkIn(Object o) {
        locked.remove(o);
        unlocked.put(o, System.currentTimeMillis());
    }
}
