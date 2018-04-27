package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.MyBean;
import com.inschos.cloud.trading.access.rpc.bean.MyBean2;
import com.inschos.cloud.trading.access.rpc.bean.ProductInfo;
import com.inschos.cloud.trading.access.rpc.service.ProductService;
import com.inschos.cloud.trading.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:53
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ProductClient {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/rpc/product";


    private ProductService getService(){
        return new HproseHttpClient(host + uri).useService(ProductService.class);
    }

    public List<ProductInfo> listProduct() {
        try {
            ProductService service = getService();
            return service != null ? service.listProduct() : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductInfo product_byId(String product_id) {
        try {
            ProductService service = getService();
            return service != null ? service.product_byId(product_id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public String addCompany(List<MyBean> list) {
        try {
            ProductService service = getService();
            return service != null ? service.addCompany(list) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<MyBean> listCompany() {
        try {
            ProductService service = getService();
            return service != null ? service.listCompany() : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public String addProduct(List<MyBean2> list) {
        try {
            ProductService service = getService();
            return service != null ? service.addProduct(list) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
