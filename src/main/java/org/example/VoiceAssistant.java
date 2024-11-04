package org.example;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.io.IOException;
import javax.sound.sampled.*;
import org.vosk.Model;
import org.vosk.Recognizer;

public class VoiceAssistant {
    public static void main(String[] args) {
        String modelPath = "/home/german/IdeaProjects/VoiceAssistant/lib/vosk-model-small-ru-0.22"; // Укажите путь к модели

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try {
            Model model = new Model(modelPath);
            Recognizer recognizer = new Recognizer(model, 16000.0f);


            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Начинаем распознавание...");

            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String command = recognizer.getResult();
                    String[] words=command.split(" ");
                    words[words.length-1]=words[words.length-1].replace("\"", "");
                    System.out.println(words[words.length-1]);
                    System.out.println(command);
                    System.out.println(words[words.length-1]);
                    Commands(command, words);
                    speak("Вы сказали: " + command);
                }

            }
        } catch (LineUnavailableException e) {
            System.err.println("Ошибка доступа к аудиолинии: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка модели: " + e.getMessage());
        }
    }
    public static void OpenYouTube() throws IOException {
        Runtime.getRuntime().exec("xdg-open " + "https://www.youtube.com/");
    }
    public static void OpenHDrezka() throws IOException {
        Runtime.getRuntime().exec("xdg-open " + "https://hdrezka.cm/continue/");
    }
    public static void OpenFilm(String [] words) throws IOException {
        try{
        String title = String.join(" ", java.util.Arrays.copyOfRange(words, 6, words.length-1));
        String formattedTitle = title.replace(" ", "+"); // Заменяем пробелы на +
        String url = "https://hdrezka.cm/search/?do=search&subaction=search&q=" + formattedTitle;
        System.out.println(title);
        String command = "xdg-open " + url;
        Runtime.getRuntime().exec(command);
        }
        catch (IllegalArgumentException e){
            System.out.println("Скажите название фильма");
        }
    }
    public static void Commands(String command, String [] words) throws IOException {
        if (command.contains("открой ютуб") || command.contains("включи ютуб"))
            OpenYouTube();
        if (command.contains("включи фильм"))
            OpenFilm(words);
        if (command.contains("открой фильмы"))
            OpenHDrezka();
    }
    private static void speak(String text) {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16"); // Выбор голоса

        if (voice != null) {
            voice.allocate(); // Аллокация ресурсов
            voice.speak(text); // Воспроизведение текста
            voice.deallocate(); // Освобождение ресурсов
        } else {
            System.out.println("Голос не найден.");
        }
    }
}
