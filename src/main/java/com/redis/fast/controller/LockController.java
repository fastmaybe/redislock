package com.redis.fast.controller;

import com.redis.fast.service.LockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liulang
 * @Date: 2020/10/12 17:00
 */
@RestController
public class LockController {

    @Autowired
    private LockServiceImpl lockService;

    @RequestMapping("testlock")
    public void test() throws InterruptedException {

        for (int i = 0; i < 5; i++) {
            lockService.asyncMe();
        }

    }
}
