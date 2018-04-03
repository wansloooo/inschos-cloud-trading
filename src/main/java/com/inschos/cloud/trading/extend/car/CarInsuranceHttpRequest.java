package com.inschos.cloud.trading.extend.car;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inschos.cloud.trading.assist.kit.HttpClientKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建日期：2018/3/29 on 15:35
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInsuranceHttpRequest<Request extends CarInsuranceRequest, Response extends CarInsuranceResponse> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CarInsuranceRequestEntity request;
    private Class<Response> cls;
    private String url;

    public CarInsuranceHttpRequest(String url, Request request, Class<Response> cls) {
        this.url = url;
        this.request = new CarInsuranceRequestEntity<Request>();
        this.cls = cls;
        if (request != null) {
            this.request.data = request;
            this.request.sign = SignatureTools.sign(JsonKit.bean2Json(request), SignatureTools.CAR_RSA_PRIVATE_KEY);
        }

        this.request.sendTime = sdf.format(new Date(System.currentTimeMillis()));
    }

    public Response post() {
        Response response;
        try {
            String result = HttpClientKit.post(url, JsonKit.bean2Json(request));

            response = JsonKit.json2Bean(result, cls);

            if (response != null) {
                response.verify = verifySignature(result);
            }

            return response;
        } catch (IOException e) {
            // e.printStackTrace();
            response = (Response) new CarInsuranceResponse();
            response.state = CarInsuranceResponse.RESULT_FAIL;
            response.msg = "请求失败";
            response.verify = false;
            return response;
        }
    }

    private boolean verifySignature(String responseJson) {
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(responseJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean flag = false;
        if (jsonNode != null) {
            JsonNode signNode = jsonNode.get("sign");
            String sign = signNode.textValue();
            String content = jsonNode.get("data").toString();
            flag = SignatureTools.verify(content, sign, SignatureTools.CAR_RSA_PUBLIC_KEY);
        }
        return flag;
    }
}
