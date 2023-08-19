package com.project.chip.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chip.models.Solution;
import com.project.chip.models.Video;
import com.project.chip.repos.SolutionRepo;
import com.project.chip.services.openai.OpenAiApiClient;
import com.project.chip.services.openai.chatgpt.CompletionRequest;
import com.project.chip.services.openai.chatgpt.CompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolutionService {
    @Autowired private ObjectMapper jsonMapper;
    @Autowired private OpenAiApiClient client;
    @Autowired private SolutionRepo solutionRepo;

    private String chatWithGpt3(String message) throws Exception {
        var completion = CompletionRequest.defaultWith(message);
        var postBodyJson = jsonMapper.writeValueAsString(completion);
        var responseBody = client.postToOpenAiApi(postBodyJson, OpenAiApiClient.OpenAiService.GPT_3);
        var completionResponse = jsonMapper.readValue(responseBody, CompletionResponse.class);
        return completionResponse.firstAnswer().orElseThrow();
    }

    public String solve(Video video, Long userId, Long videoId, String prompt) {
        Solution solution = new Solution(userId, videoId, prompt);

        try {
            String result = chatWithGpt3(
                    "Первый текст: \"" + video.getFullText() + "\"\nВторой текст: \"" + prompt.trim() + "\"\nНасколько первый текст соответствует второму?\n" +
                            "ОТВЕТ ДАЙ ТОЛЬКО В ПРОЦЕНТАХ (В ЧИСЛЕ) БЕЗ ОПИСАНИЯ и без точки в конце. " +
                            "При расчете считай не только десятками.\n" +
                            "если не понимаешь как оценить соответсвтие напиши \"0%\""

            ).trim();
            solution.setResult(result);
            solutionRepo.save(solution);
            return result;
        } catch (Exception e) {
            System.out.println(e);
        }

        return "Error in communication with OpenAI ChatGPT API.";
    }
}
