package com.lms.modules.recommend.controller;

import com.lms.common.result.Result;
import com.lms.modules.recommend.service.ScanService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/scan")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @PostMapping
    public Result<Map<String, Object>> scan(@RequestBody ScanRequest request) {
        Map<String, Object> result = scanService.scanBarcode(request.getBarcode());
        return Result.success(result);
    }

    @Data
    public static class ScanRequest {
        private String barcode;
    }
}
