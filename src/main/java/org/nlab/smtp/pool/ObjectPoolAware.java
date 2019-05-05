package org.nlab.smtp.pool;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ObjectPoolAware {

  void setObjectPool(SmtpConnectionPool objectPool);
}
