package com.auui.aidada;

import com.auui.aidada.controller.QuestionController;
import com.auui.aidada.model.dto.question.AiGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

@SpringBootTest
public class QuestionControllerTest {

    @Resource
    private QuestionController questionController;

    @Test
    void aiGenerateQuestionSSEVIPTest() throws InterruptedException {
        AiGenerateQuestionRequest request = new AiGenerateQuestionRequest();
        request.setAppId(3L);
        request.setQuestionNumber(10);
        request.setOptionNumber(2);

        questionController.aiGenerateQuestionSSETest(request, false);
        questionController.aiGenerateQuestionSSETest(request, false);
        questionController.aiGenerateQuestionSSETest(request, true);

        Thread.sleep(1000000L);
    }
}
