package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/26 on 11:59
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInfoModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 流水号
     */
    public String biz_id;

    /**
     * 第三方业务号
     */
    public String thp_biz_id;

    /**
     * 车险类型，1-强险，2-商业险
     */
    public String insurance_type;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 车牌号
     */
    public String car_code;

    /**
     * 车主姓名
     */
    public String name;

    /**
     * 车主身份证号
     */
    public String code;

    /**
     * 车主手机号
     */
    public String phone;

    /**
     * 车架号
     */
    public String frame_no;

    /**
     * 发动机号
     */
    public String engine_no;

    /**
     * 发改委编码
     */
    public String vehicle_fgw_code;

    /**
     * 发改委名称
     */
    public String vehicle_fgw_name;

    /**
     * 品牌型号编码
     */
    public String brand_code;

    /**
     * 投保时是否未上牌（0:否 1:是）
     */
    public String is_not_car_code;

    /**
     * 是否过户车（0:否 1:是）
     */
    public String is_trans;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

}
