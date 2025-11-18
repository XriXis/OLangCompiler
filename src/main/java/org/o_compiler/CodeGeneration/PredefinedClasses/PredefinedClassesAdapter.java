package org.o_compiler.CodeGeneration.PredefinedClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PredefinedClassesAdapter {
    static Path sourceCollectDir = Path.of("src/main/resources/PredefinedClasses/CompiledCode");
    static Path outputCollectDir = Path.of("output");
    static String collectFileName = "predefined_classes.wat";
    static String magicStringStart = ";; start merge";
    static String magicStringEnd = ";; end merge";

    public static void collect() throws IOException {
        Path sourceDir = sourceCollectDir;
        Path outputDir = outputCollectDir;

        // Ensure the output directory exists
        Files.createDirectories(outputDir);

        // Output file path
        Path outputFilePath = outputDir.resolve(collectFileName);

        if (Files.exists(outputFilePath)) {
            Files.delete(outputFilePath);
        }
        Files.write(outputFilePath, "(module\n ".getBytes(), StandardOpenOption.CREATE);

        // List all files in the source directory
        List<Path> fileList = Files.list(sourceDir).sorted()
                .sorted(Comparator.comparing(Path::getFileName))
                .toList();

        fileList.forEach(path -> {
            if (Files.isRegularFile(path)) {
                try {
                    // Read the content of the file
                    String code = new String(Files.readAllBytes(path));

                    // Locate the section of code you are interested in
                    int start = code.indexOf(magicStringStart) + magicStringStart.length();
                    int end = code.lastIndexOf(magicStringEnd);

                    // If valid section found, extract and write it to the output file
                    if (start != -1 && end != -1) {
                        String predefinedClassCode = code.substring(start, end);

                        // Append the predefined class code to the output file
                        try {
                            Files.write(outputFilePath, (predefinedClassCode).getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            throw new RuntimeException("Error writing to output file", e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading file: " + path, e);
                }
            }
        });

        Files.write(outputFilePath, "\n)\n".getBytes(),
                Files.exists(outputFilePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
    }

    public static void include(Path targetFilePath) throws IOException {
        // generated code
        String content = Files.readString(targetFilePath);

        int lastBracketIndex = content.lastIndexOf(')');
        if (lastBracketIndex == -1) {
            throw new IOException("No closing bracket ')' found in file: " + targetFilePath);
        }

        // predefined code
        String includeContent = Files.readString(outputCollectDir.resolve(collectFileName));

        // Формируем новое содержимое
        String newContent = includeContent.substring(0, includeContent.lastIndexOf(')'))
                + content
                + includeContent.substring(includeContent.lastIndexOf(')'));

        // Записываем обратно в файл
        Files.writeString(targetFilePath, newContent, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
