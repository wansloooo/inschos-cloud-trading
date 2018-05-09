package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.MyBean;
import com.inschos.cloud.trading.access.rpc.bean.MyBean2;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductInfo;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:51
 * 描述：
 * 作者：zhangyunhe
 */
public interface ProductService {

    List<ProductBean> getPlatformProductAll(long managerUuid, int categoryId);

    ProductBean getProduct(long productId);

}
