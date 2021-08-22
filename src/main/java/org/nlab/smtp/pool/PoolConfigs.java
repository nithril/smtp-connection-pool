package org.nlab.smtp.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * Ease pool configuration with default pool configuration
 * <p>
 * Created by nlabrot on 12/06/17.
 */
public class PoolConfigs {

    /**
     * Default {@link GenericObjectPoolConfig} config
     * See default parameters {@link GenericObjectPoolConfig}
     *
     * @return
     */
    public static GenericObjectPoolConfig defaultConfig() {
        return new GenericObjectPoolConfig();
    }

    /**
     * Default {@link GenericObjectPoolConfig} config
     * {@link GenericObjectPoolConfig#getTestOnBorrow} : true
     * minIdle: 0
     * maxIdle: 8
     * maxTotal: 8
     * maxWaitMillis: 10000
     * minEvictableIdleTimeMillis: 5 minutes
     * timeBetweenEvictionRunsMillis: 10 seconds
     *
     * @return
     */
    public static GenericObjectPoolConfig standardConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setTestOnBorrow(true);
        config.setMinIdle(0);
        config.setMaxIdle(8);
        config.setMaxTotal(8);

        config.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(5));
        config.setTimeBetweenEvictionRunsMillis(10000);


        config.setMaxWaitMillis(10000);
        return config;
    }


    /**
     * @param minIdle
     * @param maxIdle
     * @param maxTotal
     * @param maxWaitMillis
     * @param minEvictableIdleTimeMillis
     * @param timeBetweenEvictionRunsMillis
     * @return
     */
    public static GenericObjectPoolConfig standardConfig(int minIdle, int maxIdle, int maxTotal, int maxWaitMillis, int minEvictableIdleTimeMillis, int timeBetweenEvictionRunsMillis) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setTestOnBorrow(true);
        config.setMinIdle(minIdle);
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);

        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);


        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }
}
