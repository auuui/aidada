package com.auui.aidada.model.dto.statistic;

import lombok.Data;

/*
* APP提交答案数统计*/
@Data
public class AppAnswerCountDTO {

    private Long appId;

    private Long answerCount;
}
