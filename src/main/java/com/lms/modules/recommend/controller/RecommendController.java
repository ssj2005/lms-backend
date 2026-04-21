package com.lms.modules.recommend.controller;

import com.lms.common.result.Result;
import com.lms.modules.recommend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/{readerId}")
    public Result<List<Map<String, Object>>> getRecommendations(@PathVariable Long readerId) {
        List<Map<String, Object>> result = recommendService.getRecommendations(readerId);
        return Result.success(result);
    }
}
