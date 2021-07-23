package com.van.servicesms.dao;

import com.van.servicesms.entity.SmsTemplate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsTemplateDao {

    SmsTemplate selectByTemplateId(String id);
}
