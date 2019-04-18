package com.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.service.TestService;
import com.oauth.table.MstUserInfo;

@RestController
@RequestMapping(value = "/user")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "hello!";
    }

    // get login account name
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String getLoginAccount() {
        return this.testService.getLoginAccount();
    }

    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public MstUserInfo signUp(@RequestBody MstUserInfo mstUserInfo) {
        return this.testService.signUp(mstUserInfo);
    }
}
