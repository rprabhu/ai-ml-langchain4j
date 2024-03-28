//JAVA 21
//DEPS dev.langchain4j:langchain4j:0.28.0
//DEPS dev.langchain4j:langchain4j-ollama:0.28.0

import java.io.Console;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;

class OllamaMistralExample {

    private static final String MODEL = "mistral";
    private static final String BASE_URL = "http://localhost:11434";
    private static Duration timeout = Duration.ofSeconds(120);

    public static void main(String[] args) {
        Console console = System.console();
        String model = console.readLine(
                "Welcome, Butler at your service!!\n\nPlease choose your model - Type '1' for the Basic Model and '2' for Streaming Model:");
        String question = console.readLine("\n\nPlease enter your question - 'exit' to quit: ");

        while (!"exit".equalsIgnoreCase(question)) {
            if ("1".equals(model)) {
                basicModel(question);
            } else {
                streamingModel(question);
            }
            question = console.readLine("\n\nPlease enter your question - 'exit' to quit: ");
        }
        System.exit(0);
    }

    static void basicModel(String question) {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .build();
        System.out.println("\n\nPlease wait...\n\n");
        String answer = model.generate(question);
        System.out.println(answer);
    }

    static void streamingModel(String question) {

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .temperature(0.0)
                .build();

        CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
        model.generate(question, new StreamingResponseHandler<AiMessage>() {

            @Override
            public void onNext(String token) {
                System.out.print(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureResponse.complete(response);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        futureResponse.join();
    }

}