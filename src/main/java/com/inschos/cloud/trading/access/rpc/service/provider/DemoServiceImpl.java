package com.inschos.cloud.trading.access.rpc.service.provider;


import com.inschos.cloud.trading.access.rpc.service.DemoService;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.data.mapper.AreaMapper;
import com.inschos.cloud.trading.model.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IceAnt on 2018/3/6.
 */
@Service
public class DemoServiceImpl implements DemoService {
//

    @Autowired
    private AreaMapper areaMapper;

    public String sayHello(String name) {
        L.log.debug(" call input: "+name);
        return "hello " + name;
    }


    public String doDemo(String name) {
        L.log.debug(" call doDemo input: "+name);
        return "do "+name;
    }
    public Area findOne(Area area){

        L.log.debug(" call findArea input area: "+(area==null?null:area.name));
        return area==null?null:areaMapper.findOne(area.id);

    }



}
