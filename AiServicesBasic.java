//DEPS dev.langchain4j:langchain4j:0.29.1
//DEPS dev.langchain4j:langchain4j-ollama:0.29.1

import java.io.Console;
import java.time.Duration;
import java.util.Set;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

class AiServicesBasic {

    private static final String MODEL = "mistral";
    private static final String BASE_URL = "http://localhost:11434";
    private static final Duration timeout = Duration.ofSeconds(120);

    interface ChatMinion {
        String chat(String message);
    }

    public static void main(String[] args) {

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .temperature(0.2)
                .timeout(timeout)
                .build();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        ChatMinion minion = AiServices.builder(ChatMinion.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();
        Console console = System.console();
        String question = console.readLine("\n\nPlease enter your question: ");

        Set<String> set = Set.of("exit", "quit");
        while (!set.contains(question)) {
            String response = minion.chat(question);
            System.out.println(response);
            question = console.readLine("\n\nPlease enter your question: ");
        }

    }
}