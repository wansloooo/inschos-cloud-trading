package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.ChannelIdBean;

/**
 * 创建日期：2018/4/20 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public interface BrokerageService {

    String getBrokerageByChannelIdForManagerSystem(ChannelIdBean bean);
}
