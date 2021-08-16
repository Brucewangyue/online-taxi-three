package com.van.servicesms.service;

import org.springframework.stereotype.Service;

@Service
public class LocalLockService implements LockService {

    static boolean status = false;

    boolean getStatus() {
        return status;
    }

    @Override
    synchronized public boolean robOrder(int driverId) throws InterruptedException {
        boolean status = getStatus();
        if (status) {
            System.out.println("local司机:" + driverId + "，抢单失败");
            return false;
        } else {
            LocalLockService.status = true;
            System.out.println("local司机:" + driverId + "，抢单成功");
            return true;
        }
    }
}
