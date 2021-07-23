package com.van.serviceuser.service;

import com.van.internalcommon.constant.RedisKeyPrefixConstant;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.util.JwtUtil;
import com.van.serviceuser.dao.PassengerUserDao;
import com.van.serviceuser.entity.PassengerUser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Resource
    PassengerUserDao passengerUserDao;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 登录
     *
     * @param phoneNumber
     * @return
     */
    public ResponseResult auth(String phoneNumber) {
        // 如果数据库没有此用户，插库。可以用分布锁，也可以用 唯一索引。
        // 为什么此时用手机号？
        // 查出用户id
        PassengerUser passengerUser = new PassengerUser();
        passengerUser.setCreateTime(new Date());
        passengerUser.setPassengerGender((byte) 1);
        passengerUser.setPassengerName("Vansama");
        passengerUser.setRegisterDate(new Date());
        passengerUser.setUserState((byte) 1);
        passengerUser.setPassengerPhone(phoneNumber);

        passengerUserDao.insertSelective(passengerUser);

        long passengerId = passengerUser.getId();

        // redis存储token
        String token = JwtUtil.createToken("" + passengerId, new Date());
        stringRedisTemplate.opsForValue().set(RedisKeyPrefixConstant.PASSENGER_LOGIN_TOKEN_APP_KEY_PRE + passengerId,token,30, TimeUnit.DAYS);

        return ResponseResult.success(token);
    }
}
