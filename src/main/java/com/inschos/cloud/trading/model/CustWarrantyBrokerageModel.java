package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public class CustWarrantyBrokerageModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 归属账号uuid
     */
    public String manager_uuid;

    /**
     * 缴费ID
     */
    public String cost_id;

    /**
     * 渠道ID
     */
    public String channel_id;

    /**
     * 代理人ID
     */
    public String agent_id;

    /**
     * 保单佣金
     */
    public String warranty_money;

    /**
     * 业管佣金
     */
    public String manager_money;

    /**
     * 渠道佣金
     */
    public String channel_money;

    /**
     * 代理人佣金
     */
    public String agent_money;

    /**
     * 保单佣金比例
     */
    public String warranty_rate;

    /**
     * 业管佣金比例
     */
    public String manager_rate;

    /**
     * 渠道佣金比例
     */
    public String channel_rate;

    /**
     * 代理人佣金比例
     */
    public String agent_rate;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    public String account_uuid;

    public String start_time;
    public String end_time;
    public String time_range_type;

    public Page page;


    public CustWarrantyBrokerageModel() {

    }

    public CustWarrantyBrokerageModel(String warranty_uuid, String manager_uuid, String channel_id, String agent_id, String time) {
        this.warranty_uuid = warranty_uuid;
        this.manager_uuid = manager_uuid;
        this.channel_id = channel_id;
        this.agent_id = agent_id;
        this.created_at = time;
        this.updated_at = time;
    }

    public void setBrokerage() {
        this.warranty_money = "23.00";
        this.manager_money = "20.00";
        this.channel_money = "18.00";
        this.agent_money = "15.00";
    }

    public void setBrokerageRate() {
        this.warranty_rate = "0.23";
        this.manager_rate = "0.20";
        this.channel_rate = "0.18";
        this.agent_rate = "0.15";
    }

}
