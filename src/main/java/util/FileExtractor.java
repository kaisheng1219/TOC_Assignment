package util;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileExtractor {
    public static String extractDataFromFile(File file) {
        StringBuilder data = new StringBuilder();
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.forEachOrdered(x -> data.append(x).append("\n"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
