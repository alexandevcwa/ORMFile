package com.ormfile.io;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrmFIleIO {
    public static void WriteLines(@NotNull File file, @NotNull ArrayList<String> lstLines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        for (String line : lstLines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public static void WriteLine(@NotNull File file, @NotNull String line) throws IOException {
        FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.write(line);
        writer.newLine();
        writer.close();
    }

    public static void UpdateLine(@NotNull File file, @NotNull String line) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String result = null;

        List<String> recordsList = null;

        while ((result = reader.readLine()) != null) {
            if (result.equals(line)) {
                result = line;
            }
            recordsList.add(result);
        }

        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String value : recordsList) {
            writer.write(value);
        }
        writer.close();
    }
}
