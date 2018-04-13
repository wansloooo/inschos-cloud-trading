package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.InsurancePolicyModel;

import java.util.List;

/**
 * 创建日期：2018/3/22 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public interface InsurancePolicyMapper {

    int addInsurancePolicy(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyProPolicyNoByWarrantyId (InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearch(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountByWarrantyStatus(InsurancePolicyModel insurancePolicyModel);

    InsurancePolicyModel findInsurancePolicyDetailByWarrantyCode(String warrantyCode);

    // NOTENABLED: 2018/4/12
    List<InsurancePolicyModel> findInsurancePolicyListByUserIdAndStatus(InsurancePolicyModel insurancePolicyModel);

    // NOTENABLED: 2018/4/12
    List<InsurancePolicyModel> findInsurancePolicyListByOtherInfo(InsurancePolicyModel insurancePolicyModel);

    // NOTENABLED: 2018/4/12
    int updateInsurancePolicyUnionOrderCode(InsurancePolicyModel insurancePolicyModel);

    // NOTENABLED: 2018/4/12
    int updateInsurancePolicyWarrantyCode(InsurancePolicyModel insurancePolicyModel);

    // NOTENABLED: 2018/4/12
    String findInsurancePolicyPrivateCodeByUnionOrderCode(String unionOrderCode);

}
