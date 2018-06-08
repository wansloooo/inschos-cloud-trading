package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.client.AgentClient;
import com.inschos.cloud.trading.access.rpc.client.FileClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.*;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.extend.file.FileUpload;
import com.inschos.cloud.trading.model.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 创建日期：2018/3/22 on 11:06
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class InsurancePolicyAction extends BaseAction {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private CarInfoDao carInfoDao;

    @Autowired
    private InsuranceParticipantDao insuranceParticipantDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Autowired
    private OfflineInsurancePolicyDao offlineInsurancePolicyDao;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private FileClient fileClient;

    /**
     * FINISH: 2018/6/8
     * 获取保单列表（商朝）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListForOnlineStore(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListRequest.class);
        InsurancePolicy.GetInsurancePolicyListResponse response = new InsurancePolicy.GetInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.account_uuid = actionBean.accountUuid;

        if (!StringKit.isEmpty(request.searchKey)) {
            request.searchType = "1";
        }

        String s = checkGetInsurancePolicyListParams(request, insurancePolicyModel);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        List<InsurancePolicyModel> insurancePolicyListForOnlineStore = insurancePolicyDao.findInsurancePolicyListForOnlineStore(insurancePolicyModel);
        long total = insurancePolicyDao.findInsurancePolicyCountForOnlineStore(insurancePolicyModel);

        response.data = dealInsurancePolicyResultList(insurancePolicyListForOnlineStore, true, true);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());


        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);

    }

    /**
     * FINISH: 2018/6/8
     * 获取保单列表（业管）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListRequest.class);
        InsurancePolicy.GetInsurancePolicyListResponse response = new InsurancePolicy.GetInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, insurancePolicyModel);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListForManagerSystem(insurancePolicyModel);
        long total = insurancePolicyDao.findInsurancePolicyCountForManagerSystem(insurancePolicyModel);

        response.data = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, false, false);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 导出保单列表（业管）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表Excel文件地址json
     */
    public String downInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListRequest.class);
        InsurancePolicy.DownloadInsurancePolicyListForManagerSystemResponse response = new InsurancePolicy.DownloadInsurancePolicyListForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, insurancePolicyModel);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        String lastId = "0";
        String pageSize = "1000";
        String name = "";
        // 1-个人保单，2-团险保单，3-车险保单
        switch (request.warrantyType) {
            case "1":
                name = "个人";
                break;
            case "2":
                name = "团险";
                break;
            case "3":
                name = "车险";
                break;
        }

        response.startTime = System.currentTimeMillis();

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(name + "-保单列表");

        InsurancePolicy.GetInsurancePolicyItemBean carInsurancePolicyTitle = InsurancePolicy.GetInsurancePolicyItemBean.getCarInsurancePolicyTitle();
        List<ExcelModel<InsurancePolicy.GetInsurancePolicyItemBean>> list = new ArrayList<>();
        ExcelModel<InsurancePolicy.GetInsurancePolicyItemBean> title = new ExcelModel<>(carInsurancePolicyTitle, true, "title");
        title.boldWeight = Font.BOLDWEIGHT_BOLD;
        list.add(title);
        int startRow = 0;

        Map<String, CellStyle> cellStyleMap = ExcelModelKit.getCellStyleMap();
        int i = ExcelModelKit.writeExcel(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow, cellStyleMap);
        startRow += i;
        boolean flag;

        do {
            insurancePolicyModel.page = setPage(lastId, null, pageSize);

            List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListForManagerSystem(insurancePolicyModel);
            List<InsurancePolicy.GetInsurancePolicyItemBean> getInsurancePolicyItemBeans = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, false, false);
            list.clear();

            if (getInsurancePolicyItemBeans != null && !getInsurancePolicyItemBeans.isEmpty()) {
                for (InsurancePolicy.GetInsurancePolicyItemBean getInsurancePolicyItemBean : getInsurancePolicyItemBeans) {
                    list.add(new ExcelModel<>(getInsurancePolicyItemBean));
                }

                i = ExcelModelKit.writeExcel(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow, cellStyleMap);
                startRow += i;

                lastId = getInsurancePolicyItemBeans.get(getInsurancePolicyItemBeans.size() - 1).id;
            }

            flag = getInsurancePolicyItemBeans != null && !getInsurancePolicyItemBeans.isEmpty() && getInsurancePolicyItemBeans.size() >= Integer.valueOf(pageSize);

        } while (flag);

        response.endTime = System.currentTimeMillis();

        response.during = response.endTime - response.startTime;

        ExcelModelKit.autoSizeColumn(sheet, InsurancePolicy.CAR_FIELD_MAP.size());

        byte[] workbookByteArray = ExcelModelKit.getWorkbookByteArray(workbook);

        if (workbookByteArray == null) {
            return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
        }

        FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
        // fileUploadRequest.base64 = Base64.getEncoder().encodeToString(workbookByteArray);
        fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
        fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";
        boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, workbookByteArray);

        if (upload) {
            response.data = fileClient.getFileUrl(fileUploadRequest.fileKey);
            if (StringKit.isEmpty(response.data)) {
                return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
        }

        return json(BaseResponse.CODE_SUCCESS, "获取下载地址成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单详情（业管）
     * {@link #getInsurancePolicyDetail(String warrantyUuid, InsurancePolicy.GetInsurancePolicyDetailResponse response)} 获取保单详情
     *
     * @param actionBean 请求bean
     * @return 保单详情json
     */
    public String getInsurancePolicyDetail(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyDetailRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyDetailRequest.class);
        InsurancePolicy.GetInsurancePolicyDetailResponse response = new InsurancePolicy.GetInsurancePolicyDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        return getInsurancePolicyDetail(request.warrantyUuid, response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单统计
     * {@link #dealPercentageByList} 处理统计数据
     *
     * @param actionBean 请求bean
     * @return 保单统计json
     */
    public String getInsurancePolicyStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String startTime;
        String endTime;

        // 时间范围类型，1-今日，2-本月，3-本年，4-历年
        switch (request.timeRangeType) {
            case "1":
                //noinspection MagicConstant
                calendar.set(year, month, day, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "2":
                //noinspection MagicConstant
                calendar.set(year, month, 1, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "3":
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.YEAR, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "4":
                startTime = "0";
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                calendar.add(Calendar.YEAR, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            default:
                return json(BaseResponse.CODE_FAILURE, "时间范围类型错误", response);
        }
        custWarrantyCostModel.start_time = startTime;
        custWarrantyCostModel.end_time = endTime;
        custWarrantyCostModel.time_range_type = request.timeRangeType;
        custWarrantyCostModel.manager_uuid = actionBean.managerUuid;

        custWarrantyBrokerageModel.start_time = startTime;
        custWarrantyBrokerageModel.end_time = endTime;
        custWarrantyBrokerageModel.time_range_type = request.timeRangeType;
        custWarrantyBrokerageModel.manager_uuid = actionBean.managerUuid;

        List<PremiumStatisticModel> custWarrantyCostStatistic = custWarrantyCostDao.findCustWarrantyCostStatistic(custWarrantyCostModel);
        List<BrokerageStatisticModel> custWarrantyBrokerageStatistic = custWarrantyBrokerageDao.findCustWarrantyBrokerageStatistic(custWarrantyBrokerageModel);

        LinkedHashMap<String, InsurancePolicy.InsurancePolicyStatisticItem> map = new LinkedHashMap<>();

        response.data = new InsurancePolicy.InsurancePolicyStatisticDetail();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        response.data.startTime = startTime;
        response.data.startTimeText = sdf.format(new Date(Long.valueOf(startTime)));
        response.data.endTime = endTime;
        response.data.endTimeText = sdf.format(new Date(Long.valueOf(endTime)));

        BigDecimal premium = new BigDecimal("0.00");
        int count = 0;
        if (custWarrantyCostStatistic != null && !custWarrantyCostStatistic.isEmpty()) {
            for (PremiumStatisticModel premiumStatisticModel : custWarrantyCostStatistic) {
                InsurancePolicy.InsurancePolicyStatisticItem item = new InsurancePolicy.InsurancePolicyStatisticItem(premiumStatisticModel.time_text);
                item.setPremiumStatisticModel(premiumStatisticModel);
                map.put(premiumStatisticModel.time_text, item);
                premium = premium.add(new BigDecimal(premiumStatisticModel.premium));

                if (StringKit.isInteger(premiumStatisticModel.insurance_policy_count)) {
                    count += Integer.valueOf(premiumStatisticModel.insurance_policy_count);
                }
            }
        }

        BigDecimal brokerage = new BigDecimal("0.00");
        if (custWarrantyBrokerageStatistic != null && !custWarrantyBrokerageStatistic.isEmpty()) {
            for (BrokerageStatisticModel brokerageStatisticModel : custWarrantyBrokerageStatistic) {
                InsurancePolicy.InsurancePolicyStatisticItem item = map.get(brokerageStatisticModel.time_text);
                if (item == null) {
                    item = new InsurancePolicy.InsurancePolicyStatisticItem(brokerageStatisticModel.time_text);
                    map.put(brokerageStatisticModel.time_text, item);
                }
                item.setBrokerageStatisticModel(brokerageStatisticModel);
                brokerage = brokerage.add(new BigDecimal(brokerageStatisticModel.brokerage));
            }
        }

        if (map.isEmpty()) {
            response.data.insurancePolicyCount = "0";
            response.data.premium = "0.00";
            response.data.premiumText = "¥0.00";

            response.data.brokerage = "0.00";
            response.data.brokerageText = "¥0.00";
            response.data.brokeragePercentage = "0.0";
            response.data.brokeragePercentageText = "0.00%";

            response.data.averagePremium = "0.00";
            response.data.averagePremiumText = "¥0.00";

        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat moneyFormat = new DecimalFormat("###,###,###,###,##0.00");

            response.data.insurancePolicyCount = String.valueOf(count);
            response.data.premium = decimalFormat.format(premium.doubleValue());
            response.data.premiumText = "¥" + moneyFormat.format(new BigDecimal(response.data.premium));

            response.data.brokerage = decimalFormat.format(brokerage.doubleValue());
            response.data.brokerageText = "¥" + moneyFormat.format(new BigDecimal(response.data.brokerage));

            if (premium.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = brokerage.divide(premium, BigDecimal.ROUND_HALF_DOWN);
                response.data.brokeragePercentage = String.valueOf(divide.doubleValue());
                divide = divide.multiply(new BigDecimal("100"));
                response.data.brokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
            } else {
                response.data.brokeragePercentage = "0.0";
                response.data.brokeragePercentageText = "0.00%";
            }

            BigDecimal bigDecimal = new BigDecimal(response.data.insurancePolicyCount);
            if (map.size() != 0 && bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = premium.divide(bigDecimal, BigDecimal.ROUND_HALF_DOWN);
                response.data.averagePremium = decimalFormat.format(divide.doubleValue());
                response.data.averagePremiumText = "¥" + moneyFormat.format(new BigDecimal(response.data.averagePremium));
            } else {
                response.data.averagePremium = "0.00";
                response.data.averagePremiumText = "¥0.00";
            }

        }

        response.data.insurancePolicyList = dealPercentageByList(map, premium);

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 线下单导入
     * {@link #dealPercentageByList} 处理统计数据
     *
     * @param actionBean 请求bean
     * @return 保单统计json
     */
    public String offlineInsurancePolicyInput(ActionBean actionBean) {
        InsurancePolicy.OfflineInsurancePolicyInputRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.OfflineInsurancePolicyInputRequest.class);
        InsurancePolicy.OfflineInsurancePolicyInputResponse response = new InsurancePolicy.OfflineInsurancePolicyInputResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        String fileUrl = fileClient.getFileUrl(request.fileKey);

        if (StringKit.isEmpty(fileUrl)) {
            return json(BaseResponse.CODE_FAILURE, "无法获取报表文件", response);
        }

        InputStream inputStream = HttpClientKit.downloadFile(fileUrl);

        if (inputStream == null) {
            return json(BaseResponse.CODE_FAILURE, "获取报表文件失败", response);
        }

        response.data = new InsurancePolicy.OfflineInsurancePolicyDetail();
        response.data.list = new ArrayList<>();

        boolean flag;
        Workbook wb;

        List<OfflineInsurancePolicyModel> errorList = new ArrayList<>();
        try {
            wb = WorkbookFactory.create(inputStream);
            String time = String.valueOf(System.currentTimeMillis());

            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    OfflineInsurancePolicyModel offlineInsurancePolicyModel = ExcelModelKit.createModel(OfflineInsurancePolicyModel.class, OfflineInsurancePolicyModel.COLUMN_FIELD_MAP, row);

                    if (offlineInsurancePolicyModel == null) {
                        continue;
                    }

                    if (offlineInsurancePolicyModel.isEmptyLine()) {
                        continue;
                    }

                    if (offlineInsurancePolicyModel.isTitle()) {
                        continue;
                    }

                    if (!offlineInsurancePolicyModel.isEnable()) {
                        offlineInsurancePolicyModel.reason = "缺少必填字段";
                        response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                        errorList.add(offlineInsurancePolicyModel);
                        continue;
                    }

                    OfflineInsurancePolicyModel offlineInsurance = offlineInsurancePolicyDao.findOfflineInsurancePolicyByWarrantyCode(offlineInsurancePolicyModel.warranty_code);

                    if (offlineInsurance != null) {
                        offlineInsurancePolicyModel.reason = "保单号重复";
                        response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                        errorList.add(offlineInsurancePolicyModel);
                    } else {

                        offlineInsurancePolicyModel.manager_uuid = actionBean.managerUuid;
                        offlineInsurancePolicyModel.warranty_uuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
                        offlineInsurancePolicyModel.created_at = time;
                        offlineInsurancePolicyModel.updated_at = time;
                        offlineInsurancePolicyModel.state = "1";

                        long l = offlineInsurancePolicyDao.addOfflineInsurancePolicy(offlineInsurancePolicyModel);
                        if (l <= 0) {
                            offlineInsurancePolicyModel.reason = "添加数据失败";
                            response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                            errorList.add(offlineInsurancePolicyModel);
                        }
                    }
                }
            }

            flag = response.data.list == null || response.data.list.isEmpty();

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!flag && response.data.list != null && !response.data.list.isEmpty()) {
            OfflineInsurancePolicyModel titleModel = OfflineInsurancePolicyModel.getTitleModel();
            errorList.add(0, titleModel);

            List<ExcelModel<OfflineInsurancePolicyModel>> dataList = new ArrayList<>();
            for (OfflineInsurancePolicyModel offlineInsurancePolicyModel : errorList) {
                dataList.add(new ExcelModel<>(offlineInsurancePolicyModel));
            }

            byte[] data = ExcelModelKit.createExcelByteArray(dataList, OfflineInsurancePolicyModel.COLUMN_FIELD_MAP, "导入失败保单数据");

            if (data == null) {
                return json(BaseResponse.CODE_FAILURE, "导入失败", response);
            }

            FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
            fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
            fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";

            boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, data);

            if (upload) {
                response.data.excelFileKey = fileUploadRequest.fileKey;
                response.data.excelFileUrl = fileClient.getFileUrl(fileUploadRequest.fileKey);
                return json(BaseResponse.CODE_FAILURE, "部分导入失败", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "导入失败", response);
            }

        }

        return json((flag ? BaseResponse.CODE_SUCCESS : BaseResponse.CODE_FAILURE), (flag ? "导入成功" : "部分导入失败"), response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单报表
     *
     * @param actionBean 请求bean
     * @return 保单报表json
     */
    public String getInsurancePolicyStatementListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyBrokerageStatisticListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyBrokerageStatisticListRequest.class);
        InsurancePolicy.GetInsurancePolicyBrokerageStatisticListResponse response = new InsurancePolicy.GetInsurancePolicyBrokerageStatisticListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.search = request.searchKey;

        if (StringKit.isEmpty(request.startTime)) {
            long l = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
            custWarrantyCostModel.start_time = String.valueOf(l);
        } else {
            custWarrantyCostModel.start_time = request.startTime;
        }

        if (StringKit.isEmpty(request.endTime)) {
            custWarrantyCostModel.end_time = String.valueOf(System.currentTimeMillis());
        } else {
            custWarrantyCostModel.end_time = request.endTime;
        }

        custWarrantyCostModel.manager_uuid = actionBean.managerUuid;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "20";
        }
        custWarrantyCostModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<BrokerageStatisticListModel> insurancePolicyBrokerageStatisticList = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticList(custWarrantyCostModel);
        long total = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCostModel);
        response.data = new ArrayList<>();
        String lastId = "0";

        if (insurancePolicyBrokerageStatisticList != null && !insurancePolicyBrokerageStatisticList.isEmpty()) {
            for (BrokerageStatisticListModel brokerageStatisticListModel : insurancePolicyBrokerageStatisticList) {
                InsuranceParticipantModel holder = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(brokerageStatisticListModel.warranty_uuid);
                InsurancePolicy.InsurancePolicyBrokerageStatistic insurancePolicyBrokerageStatistic = new InsurancePolicy.InsurancePolicyBrokerageStatistic(brokerageStatisticListModel);
                insurancePolicyBrokerageStatistic.customerName = holder.name;
                insurancePolicyBrokerageStatistic.customerMobile = holder.phone;
                if (StringKit.isInteger(brokerageStatisticListModel.product_id)) {
                    ProductBean product = productClient.getProduct(Long.valueOf(brokerageStatisticListModel.product_id));
                    if (product != null) {
                        insurancePolicyBrokerageStatistic.insuranceName = product.insuranceCoName;
                        insurancePolicyBrokerageStatistic.productName = product.displayName;
                    }
                }
                lastId = insurancePolicyBrokerageStatistic.costId;
                response.data.add(insurancePolicyBrokerageStatistic);
            }
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);

    }

    /**
     * FINISH: 2018/6/8
     * 根据（业管／代理人）和 付款时间 获取保单列表
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListByActualPayTime(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListByActualPayTimeRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListByActualPayTimeRequest.class);
        InsurancePolicy.GetInsurancePolicyListByActualPayTimeResponse response = new InsurancePolicy.GetInsurancePolicyListByActualPayTimeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (!StringKit.isInteger(actionBean.userId)) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        if (StringKit.equals(request.type, "1")) {
            insurancePolicyModel.manager_uuid = actionBean.managerUuid;
        } else if (StringKit.equals(request.type, "2")) {
            insurancePolicyModel.searchType = "2";
            AgentBean agentInfoByPersonIdManagerUuid = agentClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, Long.valueOf(actionBean.userId));
            if (agentInfoByPersonIdManagerUuid == null) {
                return json(BaseResponse.CODE_PARAM_ERROR, "获取保单列表失败", response);
            }
            insurancePolicyModel.agent_id = String.valueOf(agentInfoByPersonIdManagerUuid.id);
        }

        if (!StringKit.isInteger(request.startTime)) {
            insurancePolicyModel.start_time = "";
        } else {
            insurancePolicyModel.start_time = request.startTime;
        }

        if (!StringKit.isInteger(request.endTime)) {
            insurancePolicyModel.end_time = "";
        } else {
            insurancePolicyModel.end_time = request.endTime;
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<InsurancePolicyModel> insurancePolicyListByActualPayTime = insurancePolicyDao.findInsurancePolicyListByActualPayTime(insurancePolicyModel);
        long total = insurancePolicyDao.findInsurancePolicyCountByActualPayTime(insurancePolicyModel);

        response.data = dealInsurancePolicyResultList(insurancePolicyListByActualPayTime, true, true);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    // ====================================================================================================================================================================================


    /**
     * FINISH: 2018/6/8
     * 检查获取保单列表参数
     * <p>
     * {@link #getInsurancePolicyListForOnlineStore}
     * {@link #getInsurancePolicyListForManagerSystem}
     * {@link #downInsurancePolicyListForManagerSystem}
     *
     * @param request              获取保单列表请求
     * @param insurancePolicyModel 获取保单列表数据库查询model
     * @return 是否有参数异常（如果有，此为异常）
     */
    private String checkGetInsurancePolicyListParams(InsurancePolicy.GetInsurancePolicyListRequest request, InsurancePolicyModel insurancePolicyModel) {
//        if (StringKit.isEmpty(request.warrantyType) || !StringKit.isNumeric(request.warrantyType) || Integer.valueOf(request.warrantyType) < 1 || Integer.valueOf(request.warrantyType) > 4) {
//            return "保单类型错误";
//        }
        insurancePolicyModel.type = request.warrantyType;

        if (!StringKit.isEmpty(request.warrantyStatus)) {
            switch (request.warrantyStatus) {
                case "1":
                case "2":
                case "3":
                case "4":
                    insurancePolicyModel.search_warranty_string = request.warrantyStatus;
                    break;
                default:
                    return "保单状态错误";
            }
        }
        insurancePolicyModel.warranty_status = request.warrantyStatus;

        insurancePolicyModel.search = request.searchKey;
        // 1-保单号 2-代理人 3-投保人 4-车牌号
        if (!StringKit.isEmpty(request.searchType) && (!StringKit.isNumeric(request.searchType) || Integer.valueOf(request.searchType) < 1 || Integer.valueOf(request.searchType) > 4)) {
            return "搜索类型错误";
        } else if (StringKit.isEmpty(request.searchType)) {
            request.searchType = "";
            request.searchKey = "";
        }

        if (!StringKit.isEmpty(request.searchType) && StringKit.isEmpty(request.searchKey)) {
            return "搜索关键字";
        }

        insurancePolicyModel.searchType = request.searchType;

        if (!StringKit.isEmpty(request.startTime) && !StringKit.isInteger(request.startTime)) {
            return "下单时间有误";
        }
        insurancePolicyModel.start_time = request.startTime;
        if (!StringKit.isEmpty(request.endTime) && !StringKit.isInteger(request.endTime)) {
            return "下单时间有误";
        }
        insurancePolicyModel.end_time = request.endTime;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        if (StringKit.equals(request.searchType, "2")) {
            getAgentMap(insurancePolicyModel.manager_uuid, request.searchKey, insurancePolicyModel);
        }

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        return null;
    }

    /**
     * FINISH: 2018/6/8
     * 将列表处理为前台数据
     *
     * @param insurancePolicyModelList 保单列表
     * @param needLogo                 是否需要保险公司Logo
     * @param needInsured              是否需要被保险人显示字段
     * @return 前台数据列表
     */
    private List<InsurancePolicy.GetInsurancePolicyItemBean> dealInsurancePolicyResultList(List<InsurancePolicyModel> insurancePolicyModelList, boolean needLogo, boolean needInsured) {
        List<InsurancePolicy.GetInsurancePolicyItemBean> result = new ArrayList<>();
        if (insurancePolicyModelList == null) {
            return result;
        }

        HashMap<String, ProductBean> map = new HashMap<>();
        HashMap<String, Boolean> product = new HashMap<>();
        HashMap<String, String> fileUrl = new HashMap<>();
        HashMap<String, Boolean> file = new HashMap<>();

        if (!insurancePolicyModelList.isEmpty()) {
            for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyModelList) {
                InsurancePolicy.GetInsurancePolicyItemBean model = new InsurancePolicy.GetInsurancePolicyItemBean(policyListByWarrantyStatusOrSearch);

                Boolean aBoolean = product.get(model.productId);
                ProductBean productBean = null;
                if (aBoolean == null) {
                    productBean = productClient.getProduct(Long.valueOf(model.productId));
                    map.put(model.productId, productBean);
                    product.put(model.productId, productBean != null);
                } else {
                    if (aBoolean) {
                        productBean = map.get(model.productId);
                    }
                }

                if (productBean != null) {
                    model.productName = productBean.name;
                    model.insuranceProductName = productBean.displayName;
                    model.insuranceCompanyName = productBean.insuranceCoName;

                    String[] split = productBean.code.split("_");

                    String url = null;
                    if (split.length > 1 && needLogo) {
                        Boolean aBoolean1 = file.get(split[0]);
                        if (aBoolean1 == null) {
                            String fileUrl1 = fileClient.getFileUrl("property_key_" + split[0]);
                            fileUrl.put(split[0], fileUrl1);
                            file.put(split[0], !StringKit.isEmpty(fileUrl1));
                        } else {
                            if (aBoolean1) {
                                url = fileUrl.get(split[0]);
                            }
                        }
                        model.insuranceCompanyLogo = url;
                    }
                }

                if (needInsured) {
                    List<InsuranceParticipantModel> insuranceParticipantInsuredByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantInsuredByWarrantyUuid(model.warrantyUuid);
                    if (insuranceParticipantInsuredByWarrantyUuid != null && !insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
                        int size = insuranceParticipantInsuredByWarrantyUuid.size();
                        String name = insuranceParticipantInsuredByWarrantyUuid.get(0).name;
                        if (size > 1) {
                            model.insuredText = name + "等" + size + "人";
                        } else {
                            model.insuredText = name;
                        }
                    }
                }

//                if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
//
//                } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_PERSON)) {
//
//                } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_TEAM)) {
//
//                }
                result.add(model);
            }
        }

        return result;
    }

    /**
     * FINISH: 2018/6/8
     * 根据关键字获取代理人id
     *
     * @param managerUuid          业管唯一标识
     * @param searchKey            搜索关键字
     * @param insurancePolicyModel 数据库查询model
     * @return 代理人id-bean map
     */
    private Map<String, AgentBean> getAgentMap(String managerUuid, String searchKey, InsurancePolicyModel insurancePolicyModel) {
        List<AgentBean> list = agentClient.getAllBySearchName(managerUuid, searchKey);
        Map<String, AgentBean> agentMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AgentBean agentBean = list.get(i);
                sb.append(agentBean.id);
                agentMap.put(String.valueOf(agentBean.id), agentBean);
                if (i != list.size() - 1) {
                    sb.append(",");
                }
            }
        }
        insurancePolicyModel.agent_id_string = sb.toString();
        return agentMap;
    }

    /**
     * FINISH: 2018/6/8
     * 根据warrantyUuid获取保单详情
     *
     * @param warrantyUuid warrantyUuid
     * @param response     响应response
     * @return 保单详情json
     */
    private String getInsurancePolicyDetail(String warrantyUuid, InsurancePolicy.GetInsurancePolicyDetailResponse response) {
        InsurancePolicyModel insurancePolicyDetailByWarrantyCode = insurancePolicyDao.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
        String str;
        if (insurancePolicyDetailByWarrantyCode != null) {
            List<InsuranceParticipantModel> insuranceParticipantByWarrantyCode = insuranceParticipantDao.findInsuranceParticipantByWarrantyUuid(warrantyUuid);

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            custWarrantyCostModel.warranty_uuid = insurancePolicyDetailByWarrantyCode.warranty_uuid;

            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

            CustWarrantyCostListResult custWarrantyCostListResult = dealCustWarrantyCostList(custWarrantyCostByWarrantyUuid);

            response.data = new InsurancePolicy.GetInsurancePolicyDetail(insurancePolicyDetailByWarrantyCode, custWarrantyCostListResult.premium, custWarrantyCostListResult.payMoney, custWarrantyCostListResult.taxMoney, custWarrantyCostListResult.warrantyStatusForPay, custWarrantyCostListResult.warrantyStatusForPayText);

            response.data.insuredList = new ArrayList<>();
            response.data.beneficiaryList = new ArrayList<>();

            response.data.brokerageList = new ArrayList<>();
            List<CustWarrantyBrokerageModel> custWarrantyBrokerageByWarrantyUuid = custWarrantyBrokerageDao.findCustWarrantyBrokerageByWarrantyUuid(warrantyUuid);
            if (custWarrantyBrokerageByWarrantyUuid != null && !custWarrantyBrokerageByWarrantyUuid.isEmpty()) {
                for (CustWarrantyBrokerageModel custWarrantyBrokerageModel : custWarrantyBrokerageByWarrantyUuid) {
                    if (!StringKit.equals(insurancePolicyDetailByWarrantyCode.warranty_status, InsurancePolicyModel.POLICY_STATUS_INVALID) && !StringKit.equals(insurancePolicyDetailByWarrantyCode.warranty_status, InsurancePolicyModel.POLICY_STATUS_PENDING)) {
                        custWarrantyBrokerageModel.warranty_money = "0.00";
                        custWarrantyBrokerageModel.ins_money = "0.00";
                        custWarrantyBrokerageModel.manager_money = "0.00";
                        custWarrantyBrokerageModel.channel_money = "0.00";
                        custWarrantyBrokerageModel.agent_money = "0.00";
                    }
                    response.data.brokerageList.add(new InsurancePolicy.CustWarrantyBrokerage(custWarrantyBrokerageModel));
                }
            }

            ProductBean product = null;
            if (StringKit.isInteger(insurancePolicyDetailByWarrantyCode.product_id)) {
                product = productClient.getProduct(Long.valueOf(insurancePolicyDetailByWarrantyCode.product_id));
            }

            if (insuranceParticipantByWarrantyCode != null && !insuranceParticipantByWarrantyCode.isEmpty()) {
                if (product != null) {
                    response.data.productName = product.name;
                    response.data.insuranceProductName = product.displayName;
                    response.data.insuranceCompanyName = product.insuranceCoName;

                    String[] split = product.code.split("_");
                    if (split.length > 1) {
                        response.data.insuranceCompanyLogo = fileClient.getFileUrl("property_key_" + split[0]);
                    }
                }

                for (InsuranceParticipantModel insuranceParticipantModel : insuranceParticipantByWarrantyCode) {
                    InsurancePolicy.InsurancePolicyParticipantInfo insurancePolicyParticipantInfo = new InsurancePolicy.InsurancePolicyParticipantInfo(insuranceParticipantModel);
                    if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_POLICYHOLDER)) {
                        response.data.policyHolder = insurancePolicyParticipantInfo;
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_INSURED)) {
                        response.data.insuredList.add(insurancePolicyParticipantInfo);
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_BENEFICIARY)) {
                        response.data.beneficiaryList.add(insurancePolicyParticipantInfo);
                    }
                }
            }

            if (StringKit.equals(insurancePolicyDetailByWarrantyCode.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                // 车险
                CarInfoModel oneByWarrantyCode = carInfoDao.findOneByWarrantyUuid(warrantyUuid);
                response.data.carInfo = new InsurancePolicy.CarInfo(oneByWarrantyCode);
                response.data.bizId = oneByWarrantyCode.biz_id;
                response.data.bjCodeFlag = oneByWarrantyCode.bj_code_flag;
                str = json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
            } else {
                // 其他险
                str = json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "数据不存在", response);
        }

        return str;
    }

    /**
     * FINISH: 2018/6/8
     * 处理保单统计信息
     *
     * @param map     统计item
     * @param premium 保费
     * @return 保单统计item list
     */
    private List<InsurancePolicy.InsurancePolicyStatisticItem> dealPercentageByList(LinkedHashMap<String, InsurancePolicy.InsurancePolicyStatisticItem> map, BigDecimal premium) {
        List<InsurancePolicy.InsurancePolicyStatisticItem> result = new ArrayList<>();
        Set<String> strings = map.keySet();
        if (!strings.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat moneyFormat = new DecimalFormat("###,###,###,###,##0.00");
            for (String string : strings) {
                InsurancePolicy.InsurancePolicyStatisticItem item = map.get(string);

                BigDecimal itemPremium;
                if (StringKit.isEmpty(item.premium) && !StringKit.isNumeric(item.premium)) {
                    itemPremium = new BigDecimal("0.00");
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";
                    item.averagePremium = "0.00";
                    item.averagePremiumText = "¥0.00";
                    item.premiumPercentage = "0.0";
                    item.premiumPercentageText = "0.00%";
                } else {
                    itemPremium = new BigDecimal(item.premium);
                }

                BigDecimal itemBrokerage;
                if (StringKit.isEmpty(item.brokerage) && !StringKit.isNumeric(item.brokerage)) {
                    itemBrokerage = new BigDecimal("0.00");
                    item.brokerage = "0.00";
                    item.brokerageText = "¥0.00";
                    item.averageBrokeragePercentage = "0.0";
                    item.averageBrokeragePercentageText = "0.00%";
                } else {
                    itemBrokerage = new BigDecimal(item.brokerage);
                }

                BigDecimal insurancePolicyCount;
                if (StringKit.isEmpty(item.insurancePolicyCount) && !StringKit.isInteger(item.insurancePolicyCount)) {
                    insurancePolicyCount = new BigDecimal("0");
                    item.insurancePolicyCount = "0";
                } else {
                    insurancePolicyCount = new BigDecimal(item.insurancePolicyCount);
                }

                if (itemPremium.compareTo(BigDecimal.ZERO) != 0) {
                    if (!StringKit.equals(item.insurancePolicyCount, "0")) {
                        BigDecimal divide = itemPremium.divide(insurancePolicyCount, BigDecimal.ROUND_HALF_DOWN);
                        item.averagePremium = moneyFormat.format(divide.doubleValue());
                        item.averagePremiumText = "¥" + item.averagePremium;
                    } else {
                        item.averagePremium = "0.00";
                        item.averagePremiumText = "¥0.00";
                    }

                    if (premium.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal divide = itemPremium.divide(premium, BigDecimal.ROUND_HALF_DOWN);
                        item.premiumPercentage = String.valueOf((divide.doubleValue()));
                        divide = divide.multiply(new BigDecimal("100"));
                        item.premiumPercentageText = decimalFormat.format(divide.doubleValue()) + "%";
                    } else {
                        item.premiumPercentage = "0.0";
                        item.premiumPercentageText = "0.00%";
                    }
                } else {
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";
                    item.averagePremium = "0.00";
                    item.averagePremiumText = "¥0.00";
                    item.premiumPercentage = "0.0";
                    item.premiumPercentageText = "0.00%";
                }

                if (itemPremium.compareTo(BigDecimal.ZERO) != 0 && itemBrokerage.compareTo(BigDecimal.ZERO) != 0 && insurancePolicyCount.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal divide = itemBrokerage.divide(itemPremium, BigDecimal.ROUND_HALF_DOWN).divide(insurancePolicyCount, BigDecimal.ROUND_HALF_DOWN);
                    item.averageBrokeragePercentage = String.valueOf((divide.doubleValue()));
                    divide = divide.multiply(new BigDecimal("100"));
                    item.averageBrokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
                } else {
                    item.averageBrokeragePercentage = "0.0";
                    item.averageBrokeragePercentageText = "0.00%";
                }

                item.premium = decimalFormat.format(itemPremium.doubleValue());
                item.premiumText = "¥" + moneyFormat.format(new BigDecimal(item.premium));
                item.brokerage = decimalFormat.format(itemBrokerage.doubleValue());
                item.brokerageText = "¥" + moneyFormat.format(new BigDecimal(item.brokerage));

                result.add(item);
            }
        }

        return result;
    }

    /**
     * FINISH: 2018/6/8
     * 保单付款结果
     */
    static class CustWarrantyCostListResult {
        // 保费
        BigDecimal premium = new BigDecimal("0.00");
        // 付款金额
        BigDecimal payMoney = new BigDecimal("0.00");
        // 税费
        BigDecimal taxMoney = new BigDecimal("0.00");
        String warrantyStatusForPay = "";
        String warrantyStatusForPayText = "";
    }

    /**
     * FINISH: 2018/6/8
     * 处理保单付款结果
     * {@link CustWarrantyCostListResult}
     *
     * @param list 保单付款记录
     * @return 保单付款结果bean
     */
    private CustWarrantyCostListResult dealCustWarrantyCostList(List<CustWarrantyCostModel> list) {
        CustWarrantyCostListResult custWarrantyCostListResult = new CustWarrantyCostListResult();

        boolean isFindLast = false;
        int size1 = list.size();

        for (int i = size1 - 1; i > -1; i--) {
            CustWarrantyCostModel custWarrantyCostModel1 = list.get(i);
            if (StringKit.isNumeric(custWarrantyCostModel1.premium)) {
                custWarrantyCostListResult.premium = custWarrantyCostListResult.premium.add(new BigDecimal(custWarrantyCostModel1.premium));
            }

            if (StringKit.isNumeric(custWarrantyCostModel1.tax_money)) {
                custWarrantyCostListResult.taxMoney = custWarrantyCostListResult.taxMoney.add(new BigDecimal(custWarrantyCostModel1.tax_money));
            }

            if (StringKit.isNumeric(custWarrantyCostModel1.pay_money)) {
                custWarrantyCostListResult.payMoney = custWarrantyCostListResult.payMoney.add(new BigDecimal(custWarrantyCostModel1.pay_money));
            }

            if (!isFindLast) {
                isFindLast = !StringKit.isEmpty(custWarrantyCostModel1.actual_pay_time);
            }

            if (isFindLast || i == 0) {
                custWarrantyCostListResult.warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                custWarrantyCostListResult.warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
            }
        }

        return custWarrantyCostListResult;
    }

}