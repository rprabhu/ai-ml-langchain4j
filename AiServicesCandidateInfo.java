//DEPS dev.langchain4j:langchain4j:0.29.1
//DEPS dev.langchain4j:langchain4j-ollama:0.29.1

import java.time.Duration;
import java.util.List;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

class AiServicesCandidateInfo {

    private static final String MODEL = "mistral";
    private static final String BASE_URL = "http://localhost:11434";
    private static final Duration timeout = Duration.ofSeconds(120);

    class Candidate {
        String firstName;
        String lastName;
        String email;
        String experience;
        String profession;
        String phone;

        @Override
        public String toString() {
            return "Candidate: [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", experience="
                    + experience + ", profession=" + profession + ", phone=" + phone + "]";
        }
    }

    interface CandidateInfoCollector {

        @UserMessage("Extract information about a person from {{it}}")
        Candidate extractCandidateInfo(String text);

        @UserMessage("Extract all person names from {{it}}")
        List<String> extractPersonNames(String text);
    }

    public static void main(String[] args) {

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL)
                .timeout(timeout)
                .build();

        CandidateInfoCollector candidateInfoCollector = AiServices.create(CandidateInfoCollector.class, model);

        String text = """
                I am Arjun Kumar, I have been working at a Software Developer for 5 years.
                Email: arjun@myemail.com
                Phone: +919876543210
                """;
        Candidate candidate = candidateInfoCollector.extractCandidateInfo(text);
        System.out.println(candidate);

        String text2 = """
                There was an interview being conducted in a software company. 
                Arjun and Ananya planned to attend the interview.
                Next morning they went to the venue.
                There they met their friends Akash, Mithun, Sita, Kausalya and Kumar who were also attending.
                The interviewers were Bob and Steve!
                """;

        // Person person = personExtractor.extractPersonFrom(text);
        List<String> candidates = candidateInfoCollector.extractPersonNames(text2);

        System.out.println(candidates);
    }
}