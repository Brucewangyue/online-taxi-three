package com.van.servicesms.dao;

import com.van.servicesms.entity.SmsRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsRecordDao {
    Integer insert(SmsRecord record);
}
