package com.van.serviceverificationcode.service;

import com.van.internalcommon.constant.IdentityConstant;
import com.van.internalcommon.constant.RedisKeyPrefixConstant;
import com.van.internalcommon.constant.ResponseStatusEnum;
import com.van.internalcommon.dto.ResponseResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyCodeService {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public VerifyCodeService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public String generate(int identity, String phoneNumber) {
        // 校验 三档验证。乌云 安全检测。业务方控制，不能无限制发短信。
        // redis 1分钟发了3次，限制你5分钟不能发。。1小时发了10次，限制24小时内不能发。
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        String key = getRedisKey(identity, phoneNumber);
        stringRedisTemplate.opsForValue().set(key, code, 2, TimeUnit.MINUTES);

        return code;
    }

    public ResponseResult<?> verify(int identity, String phoneNumber, String code) {
        //三档验证

        // 比对code
        String resp = stringRedisTemplate.opsForValue().get(getRedisKey(identity, phoneNumber));
        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(resp) && resp.equals(code))
            return ResponseResult.success();

        // 记录错误验证码输入次数

        return ResponseResult.error(ResponseStatusEnum.VERIFY_CODE_ERROR.getCode(), ResponseStatusEnum.VERIFY_CODE_ERROR.getValue());
    }

    /**
     * 三档校验
     * @param phoneNumber
     * @param code
     */
    private void checkCodeThreeLimit(String phoneNumber,String code){

    }

    /**
     * 手机号发送短信时间限制
     */
    private void checkPhoneSendCodeTimeLimit(){

    }

    private String getRedisKey(int identity, String phoneNumber) {
        String keyPrefix = identity == IdentityConstant.PASSENGER
                ? RedisKeyPrefixConstant.PASSENGER_LOGIN_CODE_KEY_PRE
                : RedisKeyPrefixConstant.DRIVER_LOGIN_CODE_KEY_PRE;

        return keyPrefix + phoneNumber;
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
//        testEfficiency();
    }

    private static void testEfficiency() {
        System.out.println(Math.random());
        System.out.println(Math.random() * 9);
        System.out.println(Math.random() * 9 + 1);
        System.out.println(Math.pow(10, 5));
        System.out.println((Math.random() * 9 + 1) * Math.pow(10, 5));
        System.out.println(String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, 5))));

        System.out.println(new Random().nextInt(100000));

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String aa = (Math.random() + "").substring(2, 8);
        }
        long end1 = System.currentTimeMillis();
        System.out.println("结果1：" + (end1 - start1));

        long start0 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int aa = (int) ((Math.random() * 9 + 1) * Math.pow(10, 5));
        }
        long end0 = System.currentTimeMillis();
        System.out.println("结果2：" + (end0 - start0));

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int aa = (int) ((Math.random() * 9 + 1) * 100000);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("结果3：" + (end2 - start2));
    }
}
