package com.auui.aidada.controller;

import com.auui.aidada.common.BaseResponse;
import com.auui.aidada.common.ErrorCode;
import com.auui.aidada.common.ResultUtils;
import com.auui.aidada.exception.ThrowUtils;
import com.auui.aidada.mapper.UserAnswerMapper;
import com.auui.aidada.model.dto.statistic.AppAnswerCountDTO;
import com.auui.aidada.model.dto.statistic.AppAnswerResultCountDTO;
import com.auui.aidada.service.UserAnswerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {


    @Resource
    private UserAnswerMapper userAnswerMapper;

    /*
     * 热门应用及回答数统计*/
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /*回答结果分布统计*/
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
