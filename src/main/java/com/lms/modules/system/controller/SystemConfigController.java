package com.lms.modules.system.controller;

import com.lms.common.result.Result;
import com.lms.modules.system.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public Result<Map<String, String>> getAllConfigs() {
        Map<String, String> result = systemConfigService.getAllConfigs();
        return Result.success(result);
    }

    @PutMapping
    public Result<Void> updateConfig(@RequestBody Map<String, String> configs) {
        systemConfigService.batchUpdateConfigs(configs);
        return Result.success();
    }
}
