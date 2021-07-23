package com.van.serviceuser.dao;

import com.van.serviceuser.entity.PassengerUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PassengerUserDao {
    Integer insertSelective(PassengerUser passengerUser);
}
