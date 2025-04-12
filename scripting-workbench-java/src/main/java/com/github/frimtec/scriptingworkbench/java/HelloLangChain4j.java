package com.github.frimtec.scriptingworkbench.java;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class HelloLangChain4j {
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.chat("What is the meaning of life?");
        System.out.println(answer);
    }
}
