package com.yupi.aidada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yupi.aidada.model.dto.question.QuestionContentDTO;
import com.yupi.aidada.model.entity.App;
import com.yupi.aidada.model.entity.Question;
import com.yupi.aidada.model.entity.ScoringResult;
import com.yupi.aidada.model.entity.UserAnswer;
import com.yupi.aidada.model.vo.QuestionVO;
import com.yupi.aidada.service.QuestionService;
import com.yupi.aidada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/*
 * 自定义得分类评分策略
 * */
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CutomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        //1.根据id查询题目和题目结果信息 (按分数降序排列)
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        //2.统计用户总得分
        int totalScore=0;

        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        //遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            //遍历答案列表
            for (String answer : choices) {
                //遍历题目中的选项
                for(QuestionContentDTO.Option option:questionContentDTO.getOptions()){
                    //如果答案和选项的key匹配
                    if (option.getKey().equals(answer)){
                        int score= Optional.of(option.getScore()).orElse(0);
                       totalScore+=score;
                    }
                }
            }
        }
        //3. 遍历得分结果，找到用第一个用户得分范围，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore>=scoringResult.getResultScoreRange()){
                maxScoringResult=scoringResult;
                break;
            }
        }
        //4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer=new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
