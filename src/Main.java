package src;

import java.io.*;

//user 123

/**
 * Основной класс программы
 */
public class Main {
    private static String filename;
    /**
     * @param args Аргументы командной строки
     *             Основной цикл программы
     */
    public static void main(String[] args) {
        CommandInterpreter interpreter = new CommandInterpreter();
        interpreter.load();
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        try(scanner) {
            while (true) {
                System.out.println("Введите команду:");
                try {
                    String command = scanner.readLine().trim();
                    interpreter.commandHandler(command);
                } catch (Exception e) {
                    interpreter.handleError(e);
                }
            }
        }
        catch (Exception e) {
            interpreter.handleError(e);
        }
    }

}