package com.example.demo.controller;

import com.example.demo.entity.Happiness;
import com.example.demo.service.HappinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by libin on 2019/3/21.
 */
@RestController
@RequestMapping("/demo")
public class HappinessController {
    @Autowired
    private HappinessService happinessService;

    @RequestMapping("/query")
    public Happiness testQuery(){
        return happinessService.selectService("北京");
    }

    @RequestMapping("/insert")
    public Happiness testInsert(){
        happinessService.insertService();
        return happinessService.selectService("西安");
    }
}

