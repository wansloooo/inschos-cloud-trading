package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.InsurancePolicyAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/4/12 on 16:58
 * 描述：
 * 作者：zhangyunhe
 */
@Controller
@RequestMapping("/trade/")
public class InsurancePolicyController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private InsurancePolicyAction insurancePolicyAction;

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_status_list")
    @ResponseBody
    public String getInsurancePolicyStatusList(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyStatusList(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_source_list")
    @ResponseBody
    public String getInsurancePolicySourceList(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicySourceList(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_list_for_online_store")
    @ResponseBody
    public String getInsurancePolicyListForOnlineStore(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyListForOnlineStore(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_detail_for_online_store")
    @ResponseBody
    public String getInsurancePolicyDetailForOnlineStore(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyDetailForOnlineStore(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_list_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyListForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_detail_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyDetailForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyDetailForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_premium_statistic_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyPremiumStatisticForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyPremiumStatisticForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_brokerage_statistic_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyBrokerageStatisticForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyBrokerageStatisticForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_insurance_policy_statistic_detail_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyStatisticForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyStatisticForManagerSystem(actionBean);
    }
}
