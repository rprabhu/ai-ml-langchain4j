//DEPS dev.langchain4j:langchain4j:0.29.1
//DEPS dev.langchain4j:langchain4j-open-ai:0.29.1

import java.util.Map;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

class AiServicesWordAnalysis {

    enum WordAnalysis {
        OFFENSIVE, BAD, NEUTRAL, GOOD
    }

    interface WordModerator {
        @UserMessage("Analyze the profanity of {{it}}")
        WordAnalysis analyzeWords(String text);

        @UserMessage("Does {{it}} have a profanity?")
        boolean isProfane(String text);

        @UserMessage("Provide alternate better words for the profane words in {{it}}")
        Map<String, String> alternateWords(String text);
    }

    public static void main(String[] args) {

        ChatLanguageModel model = OpenAiChatModel.withApiKey("demo");

        WordModerator moderator = AiServices.create(WordModerator.class, model);

        WordAnalysis analysis =moderator.analyzeWords("He is shit");
        System.out.println("Analysis: " + analysis);

        boolean isProfane = moderator.isProfane("He is a shit");
        System.out.println("Is Profane: " + isProfane);

        Map<String, String> replacements = moderator.alternateWords("He is not intelligent but a shit and dumbo");
        System.out.println(replacements);
    }
}