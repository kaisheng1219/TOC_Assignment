package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileExtractor {
    public static void extractDataFromFile(File file) {
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.forEachOrdered(System.out::println);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
