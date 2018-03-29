package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.InsurancePreservationModel;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 15:50
 * 描述：
 * 作者：zhangyunhe
 */
public interface InsurancePreservationMapper {

    int addInsurancePreservation(InsurancePreservationModel insurancePreservationModel);

    List<InsurancePreservationModel> findInsurancePreservationListPrivateCode(InsurancePreservationModel insurancePreservationModel);

}
