/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.listener;

import com.tianta.tlib.zookeeper.curator.IZKRegistryCenter;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * 注册中心的监听器管理者的抽象类.
 * <b>@author：</b> mobing <br/>
 * <b>@date：</b> 2016年6月1日 下午2:01:30 <br/>
 * <b>@version: </b>  <br/>
 */
public abstract class AbstractListenerManager {
    
    private IZKRegistryCenter zk;
    private String cachePath;
    protected AbstractListenerManager(IZKRegistryCenter zk, String cachePath) {
        this.zk = zk;
        this.cachePath = cachePath;
        cacheData(cachePath);
    }
//    protected AbstractListenerManager(IZKRegistryCenter zk) {
//        this.zk = zk;
//      
//    }
    protected AbstractListenerManager() {
//        this.zk = zk;
      
    }

    protected void setZK(IZKRegistryCenter zk){
        this.zk = zk;
    }
    
    protected void cacheData(String cachePath){
        this.cachePath = cachePath;
        zk.addCacheData(cachePath);
    }
    /**
     * 开启监听器.
     */
    public abstract void start();
    
    /**
     * 数据变化监听
     * @param  
     * @return  void
     */
    protected void addDataListener(final TreeCacheListener listener) {
        TreeCache cache = zk.getTreeCache(cachePath);
        cache.getListenable().addListener(listener);
    }
    /**
     * zk服务器连接变化监听
     * @param  
     * @return  void
     */
    protected void addConnectionStateListener(final ConnectionStateListener listener) {
        zk.getZKClient().getConnectionStateListenable().addListener(listener);
    }
}
