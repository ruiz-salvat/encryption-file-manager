package Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileStorage {

    public static boolean saveFile(String filePath, String content)
    {
        File file = new File(filePath);
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String readFile(String filePath) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();

        File file = new File(filePath);
        Scanner reader = new Scanner(file);

        // Set first line
        if (reader.hasNextLine())
            content.append(reader.nextLine());

        while (reader.hasNextLine()) {
            content.append("\n").append(reader.nextLine());
        }

        reader.close();

        return content.toString();
    }
}
