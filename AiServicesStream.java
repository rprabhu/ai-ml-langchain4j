//DEPS dev.langchain4j:langchain4j:0.29.1
//DEPS dev.langchain4j:langchain4j-ollama:0.29.1

import java.io.Console;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

class AiServicesStream {

    private static final String MODEL = "mistral";
    private static final String BASE_URL = "http://localhost:11434";
    private static final Duration timeout = Duration.ofSeconds(120);
    private static String question;

    interface ChatMinion {
        @SystemMessage("Answer in a sarcastic tone.")
        TokenStream chat(String message);
    }

    public static void main(String[] args) {

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .temperature(0.0)
                .build();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        ChatMinion minion = AiServices.builder(ChatMinion.class)
                .streamingChatLanguageModel(model)
                .chatMemory(memory)
                .build();
        Console console = System.console();
        question = console.readLine("\n\nPlease enter your question: ");
        Set<String> set = Set.of("exit", "quit");
        while (!set.contains(question.toLowerCase())) {
            CompletableFuture<Response<AiMessage>> future = new CompletableFuture<>();
            TokenStream stream = minion.chat(question);

            stream.onNext(System.out::print)
                    .onComplete(response -> {
                        future.complete(response);
                    })
                    .onError(error -> {
                        future.completeExceptionally(error);
                    })
                    .start();
            future.join();
            question = console.readLine("\n\nPlease enter your question: ");
        }
        System.exit(0);
    }
}