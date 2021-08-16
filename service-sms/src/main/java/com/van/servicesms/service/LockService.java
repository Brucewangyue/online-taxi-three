package com.van.servicesms.service;

import org.springframework.stereotype.Service;

//@Service
public interface LockService {
    boolean robOrder(int driverId) throws InterruptedException;
}
