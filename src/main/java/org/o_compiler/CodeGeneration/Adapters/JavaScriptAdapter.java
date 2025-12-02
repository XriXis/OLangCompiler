package org.o_compiler.CodeGeneration.Adapters;

import java.io.*;

public class JavaScriptAdapter {
    public static void executeFile(String fileToExecute) {
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "src/main/java/org/o_compiler/CodeGeneration/JavaScript/run.js", fileToExecute);
            pb.directory(new File("."));

            // Перенаправляем вывод
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Читаем вывод в реальном времени
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Ждем завершения
            int exitCode = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        executeFile("output/out.wat");
    }
}