/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.curator;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.tianta.tlib.zookeeper.config.ZKConfig;
import com.tianta.tlib.zookeeper.exceptions.BZKCuratorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * zk注册中心实现 <b>Date:</b> 2016年4月15日 下午4:43:50<br/>
 *
 * @author mobing
 */
public class ZKRegistryCenter implements IZKRegistryCenter {
    private static final Logger logger = LoggerFactory.getLogger(ZKRegistryCenter.class);

    private static final String ROOT_PATH = "/";

    private static final String CHAR_SET = "UTF-8";

    private final Map<String, TreeCache> cachedMap = new HashMap<String, TreeCache>();

    private CuratorFramework client;

    private String nameSpace;

//    @Autowired
    private ZKConfig zkConfig;

    private boolean inited;

    public ZKRegistryCenter() {
    }

    public ZKRegistryCenter(String nameSpace, ZKConfig zkConfig) {
        this.nameSpace = nameSpace;
        this.zkConfig = zkConfig;
    }

    @Override
    public void init() {
        if (inited) {
            return;
        }
        logger.info("zookeeper registry center init, server lists is: {}.", zkConfig.getServerLists());
        Builder builder = CuratorFrameworkFactory.builder().connectString(zkConfig.getServerLists())
                .retryPolicy(new ExponentialBackoffRetry(zkConfig.getBaseSleepTimeMilliseconds(),
                        zkConfig.getMaxRetries(), zkConfig.getMaxSleepTimeMilliseconds()));
        if (StringUtils.isNotEmpty(nameSpace)) {
            builder.namespace(nameSpace);
        }

        if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
            builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
        }
        if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
            builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
        }
        if (!Strings.isNullOrEmpty(zkConfig.getDigest())) {
            builder.authorization("digest", zkConfig.getDigest().getBytes(Charset.forName(CHAR_SET)))
                    .aclProvider(new ACLProvider() {

                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(final String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }
        client = builder.build();
        client.start();
        logger.info("zookeeper client has started!");
        inited = true;
        logger.info("zookeeper registry center init finished!");
        try {
            client.blockUntilConnected();
        } catch (final Exception ex) {
            logger.error("zookeeper registry center fail to init!", ex);

            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public void close() {
        for (Entry<String, TreeCache> each : cachedMap.entrySet()) {
            each.getValue().close();
        }
        waitForCacheClose();
        CloseableUtils.closeQuietly(client);
    }

    /**
     * 等待500ms, cache先关闭再关闭client, 否则会抛异常 因为异步处理, 可能会导致client先关闭而cache还未关闭结束.
     * 等待Curator新版本解决这个bug.
     * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
     */
    private void waitForCacheClose() {
        try {
            Thread.sleep(500L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String getDataFromCached(final String key) {
        TreeCache cache = findTreeCache(key);
        if (null == cache) {
            return getData(key);
        }
        ChildData resultInCache = cache.getCurrentData(key);
        if (null != resultInCache) {
            return null == resultInCache.getData() ? null
                    : new String(resultInCache.getData(), Charset.forName(CHAR_SET));
        }
        return getData(key);
    }

    private TreeCache findTreeCache(final String key) {
        for (Entry<String, TreeCache> entry : cachedMap.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String getData(final String key) {
        try {

            if (!isExisted(key)) {
                return null;
            }
            return new String(client.getData().forPath(key), Charset.forName(CHAR_SET));
        } catch (final Exception ex) {
            logger.error("ZK registry center getData error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public List<String> getChildrenKeys(final String key) {
        try {
            if (!isExisted(key)) {
                return null;
            }
            List<String> result = client.getChildren().forPath(key);
            Collections.sort(result, new Comparator<String>() {

                @Override
                public int compare(final String o1, final String o2) {
                    return o2.compareTo(o1);
                }
            });
            return result;
        } catch (final Exception ex) {
            logger.error("ZK registry center getChildrenKeys error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public boolean isExisted(final String key) {
        try {
            if (StringUtils.isEmpty(key)) {
//                Assert.notNull(key);
            }

            return null != client.checkExists().forPath(key);
        } catch (final Exception ex) {
            logger.error("ZK registry center isExisted error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public void persist(final String key, final String value) {
        try {

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                Assert.notNull(key, value);
            }
            if (!isExisted(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key,
                        value.getBytes());
            } else {
                update(key, value);
            }
        } catch (final Exception ex) {
            logger.error("ZK registry center persist error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public void persist(final String key) {
        try {
            if (!isExisted(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key);
            } else {
                update(key);
            }
        } catch (final Exception ex) {
            logger.error("ZK registry center persist error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public void update(final String key) {
        try {
            client.inTransaction().check().forPath(key).and().setData()
                    .forPath(key).and().commit();
        } catch (final Exception ex) {
            logger.error("ZK registry center update error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public void update(final String key, final String value) {
        try {
            client.inTransaction().check().forPath(key).and().setData()
                    .forPath(key, value.getBytes(Charset.forName(CHAR_SET))).and().commit();
        } catch (final Exception ex) {
            logger.error("ZK registry center update error!", ex);
            throw new BZKCuratorException(ex);
        }
    }

    @Override
    public String persistEphemeral(final String key, final String value) {
        String data = null;
        try {
            if (isExisted(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            if (value == null) {
                data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key);
            } else {
                data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key,
                        value.getBytes(Charset.forName(CHAR_SET)));
            }

        } catch (final Exception ex) {
            logger.error("ZK registry center persistEphemeral error!", ex);
            throw new BZKCuratorException(ex);
        }
        return data;

    }

    @Override
    public String persistEphemeral(final String key) {
        return persistEphemeral(key, null);
    }

    @Override
    public String persistEphemeralSequential(final String key, final String value) {
        String data = null;
        try {
            if (value == null) {
                data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
            } else {
                data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key, value.getBytes(Charset.forName(CHAR_SET)));
            }

        } catch (final Exception ex) {
            logger.error("ZK registry center persistEphemeralSequential error!", ex);
            throw new BZKCuratorException(ex);
        }
        return data;
    }

    @Override
    public String persistEphemeralSequential(final String key) {

        return persistEphemeralSequential(key, null);
    }

    @Override
    public void remove(final String key) {
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(key);
//            client.delete().forPath(key);

        } catch (final Exception ex) {
            logger.error("ZK registry center remove error!", ex);
            throw new BZKCuratorException(ex);
        }
    }


    @Override
    public long getRegistryCenterTime(final String key) {
        long result = 0L;
        try {
            String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(key);

            if (!path.startsWith(ROOT_PATH)) {
                path = ROOT_PATH + path;
            }
            result = client.checkExists().forPath(path).getCtime();
        } catch (final Exception ex) {
            logger.error("ZK registry center getRegistryCenterTime error!", ex);
            throw new BZKCuratorException(ex);
        }
        Preconditions.checkState(0L != result, "Cannot get registry center time.");
        return result;
    }

    @Override
    public CuratorFramework getZKClient() {
        return client;
    }

    @Override
    public void addCacheData(final String cachePath) {
        TreeCache cache = new TreeCache(client, cachePath);
        try {
            cache.start();
        } catch (final Exception ex) {
            cache.close();
            throw new BZKCuratorException(ex);
        }
        cachedMap.put(cachePath, cache);
    }

    @Override
    public TreeCache getTreeCache(final String cachePath) {
        return cachedMap.get(cachePath);
    }
}
