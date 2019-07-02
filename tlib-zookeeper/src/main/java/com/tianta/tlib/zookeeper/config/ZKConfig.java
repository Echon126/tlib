/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <b>ClassName:</b> BConfigVO.java <br/>
 * <b>Description:</b> 配置文件实体类 <br/>
 * <b>Date:</b> 2015年10月16日 下午3:55:42<br/>
 *
 * @author mobing
 */
@Data
@Configuration
public class ZKConfig {
    @Value("${tlib.zookeeper.server:127.0.0.1:2181}")
    private String serverLists;
    @Value("${tlib.zookeeper.sessionTimeoutMilliseconds:3000}")
    private int sessionTimeoutMilliseconds;
    @Value("${tlib.zookeeper.connectionTimeoutMilliseconds:2000}")
    private int connectionTimeoutMilliseconds;
    @Value("${tlib.zookeeper.digest:''}")
    private String digest;
    @Value("${tlib.zookeeper.maxRetries:0}")
    private int maxRetries;
    @Value("${tlib.zookeeper.baseSleepTimeMilliseconds:0}")
    private int baseSleepTimeMilliseconds;
    @Value("${tlib.zookeeper.maxSleepTimeMilliseconds:0}")
    private int maxSleepTimeMilliseconds;

}
