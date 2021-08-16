package com.van.servicesms.service;

import org.springframework.stereotype.Service;

@Service
public class NoLockService implements LockService {

    static boolean status = false;

    boolean getStatus() {
        return status;
    }

    @Override
    public boolean robOrder(int driverId) throws InterruptedException {
        if (getStatus()) {
            Thread.sleep(1000);
            System.out.println("司机:" + driverId + "，抢单失败");
            return false;
        } else {
            System.out.println("司机:" + driverId + "，抢单成功");
            return true;
        }
    }
}
