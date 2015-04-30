package org.nlab.smtp.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ObjectPoolAware<T> {

    void setObjectPool(ObjectPool<T> objectPool);

    ObjectPool<T> getObjectPool();
}
