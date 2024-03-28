//JAVA 21
//DEPS dev.langchain4j:langchain4j:0.28.0
//DEPS dev.langchain4j:langchain4j-ollama:0.28.0

import java.io.Console;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;

class OllamaMistralChatMemory {

    private static final String MODEL = "mistral";
    private static final String BASE_URL = "http://localhost:11434";
    private static Duration timeout = Duration.ofSeconds(120);

    public static void main(String[] args) {
        beginChatWithChatMemory();
    }

    static void beginChatWithChatMemory() {

        Console console = System.console();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(3);

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .temperature(0.0)
                .build();

        String question = console.readLine(
                "\n\nPlease enter your question - 'exit' to quit: ");
        while (!"exit".equalsIgnoreCase(question)) {

            memory.add(UserMessage.from(question));
            CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
            model.generate(memory.messages(), new StreamingResponseHandler<AiMessage>() {

                @Override
                public void onNext(String token) {
                    System.out.print(token);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    memory.add(response.content());
                    futureResponse.complete(response);
                }

                @Override
                public void onError(Throwable error) {
                    futureResponse.completeExceptionally(error);
                }
            });

            futureResponse.join();
            question = console.readLine("\n\nPlease enter your question - 'exit' to quit: ");
        }
    }

}