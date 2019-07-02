/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;

import java.util.List;

/**
 * 注册中心. <b>Date:</b> 2016年4月15日 下午4:23:39<br/>
 *
 * @author mobing
 */
public interface IZKRegistryCenter {
    /**
     * 直接获取操作注册中心的原生客户端. 如：Zookeeper或Redis等原生客户端.
     *
     * @return 注册中心的原生客户端
     */
    public CuratorFramework getZKClient();

    /**
     * 初始化注册中心.
     */
    public void init();

    /**
     * 关闭注册中心.
     */
    public void close();

    /**
     * 获取数据是否存在.
     *
     * @param key 键
     * @return 数据是否存在
     */
    public boolean isExisted(String key);

    /**
     * 持久化注册数据.
     *
     * @param key   键
     * @param value 值
     */
    public void persist(String key, String value);

    public void persist(String key);

    /**
     * 更新注册数据.
     *
     * @param key   键
     * @param value 值
     */
    public void update(String key, String value);

    public void update(String key);

    /**
     * 删除注册数据.
     *
     * @param key 键
     */
    public void remove(String key);

    /**
     * 获取注册中心当前时间.
     *
     * @param key 用于获取时间的键
     * @return 注册中心当前时间
     */
    public long getRegistryCenterTime(String key);

    /**
     * 获取注册数据.
     *
     * @param key 键
     * @return 值
     */
    public String getDataFromCached(String key);

    /**
     * 直接从注册中心而非本地缓存获取数据.
     *
     * @param key 键
     * @return 值
     */
    public String getData(String key);

    /**
     * 获取子节点名称集合.
     *
     * @param key 键
     * @return 子节点名称集合
     */
    public List<String> getChildrenKeys(String key);

    /**
     * 持久化临时注册数据.
     *
     * @param key   键
     * @param value 值
     */
    public String persistEphemeral(String key, String value);

    public String persistEphemeral(String key);

    /**
     * 持久化临时顺序注册数据.
     *
     * @param key 键
     */
    public String persistEphemeralSequential(String key);

    public String persistEphemeralSequential(String key, String value);

    /**
     * 添加本地缓存.
     *
     * @param cachePath 需加入缓存的路径
     */
    public void addCacheData(String cachePath);

    /**
     * 获取注册中心数据缓存对象.
     *
     * @param cachePath 缓存的节点路径
     * @return 注册中心数据缓存对象
     */
    public TreeCache getTreeCache(String cachePath);

}
