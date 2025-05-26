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

/**
 * Контролер гри (5-буквена версія на кшталт Wordle).
 * <p>
 * Дозволяє користувачеві вгадувати слово, відображає підсвітку літер (зелений, жовтий, сірий),
 * зберігає стан гри у {@link HttpSession}.
 * </p>
 */
@Controller
public class GameController {

    private final List<String> words;
    private final int maxAttempts = 6;
    private final int wordLength = 5;

    /**
     * Конструктор. Завантажує список слів з файлу `word.txt` (тільки слова з 5 букв).
     */
    public GameController() {
        words = loadWordsFromFile("word.txt");
    }

    /**
     * Завантажує слова з ресурсу класу (файл у classpath).
     *
     * @param filename ім’я файлу зі словами
     * @return список слів довжиною {@code wordLength}
     */
    private List<String> loadWordsFromFile(String filename) {
        List<String> list = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() == wordLength) {
                        list.add(line.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Відображає сторінку гри. Ініціалізує нову гру, якщо ще не почато.
     *
     * @param session сесія користувача
     * @param model   модель для передачі атрибутів у шаблон
     * @return ім’я шаблону гри ("hangman")
     */
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

    /**
     * Обробляє здогад користувача, додає його до спроб, змінює статус гри.
     *
     * @param guess   слово, введене користувачем
     * @param session сесія користувача
     * @return редірект на сторінку гри
     */
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

    /**
     * Скидає поточну гру, починаючи нову.
     *
     * @param session сесія користувача
     * @return редірект на сторінку гри
     */
    @PostMapping("/hangman/reset")
    public String resetGame(HttpSession session) {
        startNewGame(session);
        return "redirect:/hangman";
    }

    /**
     * Ініціалізує нову гру: вибирає випадкове слово, скидає спроби і статус гри.
     *
     * @param session сесія користувача
     */
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

    /**
     * Перевіряє, чи гра завершена (виграна або програна).
     *
     * @param session сесія користувача
     * @return {@code true}, якщо гра завершена
     */
    private boolean isGameOver(HttpSession session) {
        String status = (String) session.getAttribute("gameStatus");
        return status != null && (status.equals("WIN") || status.equals("LOSE"));
    }

    /**
     * Генерує підсвітку букв (зелене, жовте, сіре) для кожної спроби.
     * <ul>
     *     <li>зелений — правильна літера на правильному місці</li>
     *     <li>жовтий — літера є, але не на своєму місці</li>
     *     <li>сірий — літера відсутня</li>
     * </ul>
     *
     * @param attempts   список попередніх спроб
     * @param targetWord правильне слово
     * @return список списків (по літерах) з мапою {@code letter → color}
     */
    private List<List<Map<String, String>>> buildColorFeedback(List<String> attempts, String targetWord) {
        List<List<Map<String, String>>> result = new ArrayList<>();

        for (String attempt : attempts) {
            List<Map<String, String>> feedback = new ArrayList<>();
            boolean[] usedInTarget = new boolean[targetWord.length()];

            // Зелений прохід (точні збіги)
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

            // Жовтий прохід (літери на інших позиціях)
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
