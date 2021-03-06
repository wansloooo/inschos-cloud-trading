package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.service.AgentService;
import com.inschos.common.assist.kit.L;
import com.inschos.common.assist.kit.StringKit;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018/4/26 on 14:13
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class AgentClient {

    @Value("${rpc.remote.agent.host}")
    private String host;

    private final String uri = "/rpc/agent";

    private AgentService getService() {
        return new HproseHttpClient(host + uri).useService(AgentService.class);
    }

    public AgentBean getAgentInfoByPersonIdManagerUuid(String managerUuid, long personId) {
        try {
            AgentService service = getService();
            return service != null ? service.getAgentInfoByPersonIdManagerUuid(managerUuid, personId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public AgentBean getAgentById(long agentId) {
        try {
            AgentService service = getService();
            return service != null ? service.getAgentById(agentId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<AgentBean> getAllBySearchName(String managerUuid, String searchName) {
        try {
            AgentService service = getService();
            return service != null ? service.getAllBySearchName(managerUuid, searchName) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<AgentBean> getAgentListByIds(List<String> agentIds) {
        try {
            AgentService service = getService();

            if (agentIds != null && !agentIds.isEmpty()) {
                List<Long> ids = new ArrayList<>();
                for (String agentId : agentIds) {
                    if (StringKit.isInteger(agentId)) {
                        ids.add(Long.valueOf(agentId));
                    }
                }
                return service != null ? service.getAgentListByIds(ids) : null;
            } else {
                return null;
            }

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
