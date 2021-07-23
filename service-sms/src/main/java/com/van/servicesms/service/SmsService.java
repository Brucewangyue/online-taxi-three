package com.van.servicesms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.internalcommon.constant.ResponseStatusEnum;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.servicesms.SmsTemplateDto;
import com.van.internalcommon.dto.servicesms.request.SmsSendRequest;
import com.van.servicesms.dao.SmsRecordDao;
import com.van.servicesms.dao.SmsTemplateDao;
import com.van.servicesms.entity.SmsRecord;
import com.van.servicesms.entity.SmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    // 一条短信大概80个字节、1024 1k 、
    private final static Map<String, SmsTemplate> smsTemplateCache = new HashMap<>();

    @Resource
    private SmsRecordDao smsRecordDao;

    @Resource
    private SmsTemplateDao smsTemplateDao;

    /**
     * 发送短信
     *
     * @param smsSendRequest
     * @return
     */
    public ResponseResult sendSms(SmsSendRequest smsSendRequest) {
        for (String phoneNumber : smsSendRequest.getReceivers()) {

            SmsRecord smsRecord = new SmsRecord();
            smsRecord.setPhoneNumber(phoneNumber);
            for (SmsTemplateDto template : smsSendRequest.getTemplates()) {
                if (!smsTemplateCache.containsKey(template.getId())) {
                    SmsTemplate smsTemplate = smsTemplateDao.selectByTemplateId(template.getId());
                    smsTemplateCache.put(smsTemplate.getTemplateId(), smsTemplate);
                }

                SmsTemplate smsTemplate = smsTemplateCache.get(template.getId());
                String content = smsTemplate.getTemplateContent();
                for (Map.Entry<String, Object> entry : template.getParamsMap().entrySet()) {
                    content = content.replace("{" + entry.getKey() + "}", "" + entry.getValue());
                }

                try{
                    // 发送短信
                    int result = send(phoneNumber, template.getId(), template.getParamsMap());

                    smsRecord.setPhoneNumber(phoneNumber);
                    smsRecord.setCreateTime(new Date());
                    smsRecord.setOperatorName("");
                    smsRecord.setSendFlag(1);
                    smsRecord.setSendNumber(0);
                    smsRecord.setSendTime(new Date());
                    smsRecord.setSmsContent(content);

                    if (result != ResponseStatusEnum.SUCCESS.getCode()) {
                        throw new Exception("短信发送失败");
                    }
                }catch (Exception e){
                    smsRecord.setSendFlag(0);
                    smsRecord.setSendNumber(1);
                }finally {
                    smsRecordDao.insert(smsRecord);
                }
            }
        }

        return ResponseResult.success();
    }

    private int send(String phoneNumber, String templateId, Map<String, ?> map) throws Exception {

        return sendMsg(phoneNumber, templateId,new ObjectMapper().writeValueAsString(map));
    }

    private int sendMsg(String phoneNumber, String templateCode, String param) {

        /**
         *  供应商 发 短信
         */
        return ResponseStatusEnum.SUCCESS.getCode();

    }
}
