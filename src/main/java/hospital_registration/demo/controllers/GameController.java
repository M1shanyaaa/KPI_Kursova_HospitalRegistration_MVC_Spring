package hospital_registration.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class GameController {

    private final List<String> words;
    private final int maxAttempts = 6;
    private final int wordLength = 5;

    public GameController() {
        words = loadWordsFromFile("word.txt");
    }

    private List<String> loadWordsFromFile(String filename) {
        List<String> list = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() == wordLength) {  // беремо тільки слова з 5 букв
                        list.add(line.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @GetMapping("/hangman")
    public String showGame(HttpSession session, Model model) {
        if (session.getAttribute("currentWord") == null) {
            startNewGame(session);
        }

        String currentWord = (String) session.getAttribute("currentWord");
        List<String> attempts = (List<String>) session.getAttribute("attempts");
        if (attempts == null) attempts = new ArrayList<>();

        List<List<Map<String, String>>> feedback = buildColorFeedback(attempts, currentWord);
        String gameStatus = (String) session.getAttribute("gameStatus");
        if (gameStatus == null) gameStatus = "PLAY";

        model.addAttribute("attemptsFeedback", feedback);
        model.addAttribute("attemptsLeft", maxAttempts - attempts.size());
        model.addAttribute("gameStatus", gameStatus);
        model.addAttribute("word", gameStatus.equals("LOSE") ? currentWord : "");
        return "hangman";
    }

    @PostMapping("/hangman/guessWord")
    public String guessWord(@RequestParam String guess, HttpSession session) {
        String currentWord = (String) session.getAttribute("currentWord");
        List<String> attempts = (List<String>) session.getAttribute("attempts");
        if (attempts == null) attempts = new ArrayList<>();

        guess = guess.toLowerCase(Locale.ROOT);

        if (guess.length() == wordLength && !isGameOver(session)) {
            attempts.add(guess);
            session.setAttribute("attempts", attempts);

            if (guess.equals(currentWord)) {
                session.setAttribute("gameStatus", "WIN");
            } else if (attempts.size() >= maxAttempts) {
                session.setAttribute("gameStatus", "LOSE");
            } else {
                session.setAttribute("gameStatus", "PLAY");
            }
        }

        return "redirect:/hangman";
    }

    @PostMapping("/hangman/reset")
    public String resetGame(HttpSession session) {
        startNewGame(session);
        return "redirect:/hangman";
    }

    private void startNewGame(HttpSession session) {
        Random random = new Random();
        String word;
        do {
            word = words.get(random.nextInt(words.size()));
        } while (word.length() != wordLength);

        session.setAttribute("currentWord", word);
        session.setAttribute("attempts", new ArrayList<String>());
        session.setAttribute("gameStatus", "PLAY");
    }

    private boolean isGameOver(HttpSession session) {
        String status = (String) session.getAttribute("gameStatus");
        return status != null && (status.equals("WIN") || status.equals("LOSE"));
    }

    private List<List<Map<String, String>>> buildColorFeedback(List<String> attempts, String targetWord) {
        List<List<Map<String, String>>> result = new ArrayList<>();

        for (String attempt : attempts) {
            List<Map<String, String>> feedback = new ArrayList<>();
            boolean[] usedInTarget = new boolean[targetWord.length()];

            // GREEN pass
            for (int i = 0; i < attempt.length(); i++) {
                Map<String, String> letterInfo = new HashMap<>();
                char ch = attempt.charAt(i);
                if (targetWord.charAt(i) == ch) {
                    letterInfo.put("letter", String.valueOf(ch));
                    letterInfo.put("color", "green");
                    usedInTarget[i] = true;
                } else {
                    letterInfo.put("letter", String.valueOf(ch));
                    letterInfo.put("color", "gray");
                }
                feedback.add(letterInfo);
            }

            // YELLOW pass
            for (int i = 0; i < attempt.length(); i++) {
                char ch = attempt.charAt(i);
                if (feedback.get(i).get("color").equals("gray")) {
                    for (int j = 0; j < targetWord.length(); j++) {
                        if (!usedInTarget[j] && targetWord.charAt(j) == ch) {
                            feedback.get(i).put("color", "yellow");
                            usedInTarget[j] = true;
                            break;
                        }
                    }
                }
            }

            result.add(feedback);
        }

        return result;
    }
}
