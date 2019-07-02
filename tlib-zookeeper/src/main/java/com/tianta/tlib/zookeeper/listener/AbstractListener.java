/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册中心的监听器抽象类.
 * <b>@author：</b> mobing <br/>
 * <b>@date：</b> 2016年6月1日 下午2:02:19 <br/>
 * <b>@version: </b>  <br/>
 */
public abstract class AbstractListener implements TreeCacheListener {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);
    @Override
    public final void childEvent(final CuratorFramework client, final TreeCacheEvent event) throws Exception {
    
        logger.debug("childEvent");
        String path = null == event.getData() ? "" : event.getData().getPath();
        if (path.isEmpty()) {
            return;
        }
        dataChanged(client, event, path);
    }
    
    protected abstract void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path);
    
}
