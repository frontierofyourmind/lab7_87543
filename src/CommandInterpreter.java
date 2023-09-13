package src;

import src.model.User;
import src.model.Vehicle;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Реализует исполнение команд, вводимых пользователем (с помощью консоли или скрипта)
 */
public class CommandInterpreter {
    private TreeSet<Vehicle> vehicles = new TreeSet<>();
    private LocalDateTime initDate;
    private Repository repository;
    private User user;

    public CommandInterpreter() {
        repository = new Repository();
    }

    /**
     * @param command Команда для обработки
     * Используется для обработки команд и перенаправления в соответствующие методы
     */
    public void commandHandler(String command){
        String[] tokens = command.split("\\s+");

        if (user == null) {
            switch (tokens[0]) {
                case "registration" -> registration(tokens);
                case "authorization" -> authorization(tokens);
                default -> System.out.println("Доступны команды registration и authorization");
            }
        }
        else {
            try {
                switch (tokens[0]) {
                    case "registration" -> registration(tokens);
                    case "authorization" -> authorization(tokens);
                    case "help" -> help();
                    case "info" -> info();
                    case "show" -> show();
                    case "add" -> add();
                    case "update" -> update(tokens[1]);
                    case "remove_by_id" -> removeById(tokens[1]);
                    case "clear" -> clear();
                    case "execute_script" -> executeScript(tokens[1]);
                    case "exit" -> exit();
                    case "add_if_max" -> addIfMax();
                    case "add_if_min" -> addIfMin();
                    case "remove_lower" -> removeLower();
                    case "group_counting_by_engine_power" -> groupCountingByEnginePower();
                    case "filter_by_number_of_wheels" -> filterByNumberOfWheels(tokens[1]);
                    case "print_field_ascending_number_of_wheels" -> printFieldAscendingNumberOfWheels();
                    default ->
                            System.out.println("Неизвестная команда. Наберите help чтобы получить список доступных команд");
                }

            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    /**
     * Выводит справку по доступным командам
     */
    private void help() {
        System.out.println("Доступные команды:");
        System.out.println("help - показать доступные команды");
        System.out.println("info - показать информацию о коллекции");
        System.out.println("show - показать все элементы");
        System.out.println("add {элемент} - добавить элемент в коллекцию");
        System.out.println("update id {элемент} - обновить элемент с заданным id");
        System.out.println("remove_by_id id - удалить элемент с заданным id");
        System.out.println("clear - удалить все элементы из коллекции");
        System.out.println("save - сохранить коллекцию в файл");
        System.out.println("execute_script file_name - выполнить команды из файла");
        System.out.println("exit - выйти из программы");
        System.out.println("add_if_max {элемент} - добавить элемент, если его значение больше максимального значения в коллекции");
        System.out.println("add_if_min {элемент} - добавить элемент, если его значение меньше минимального значения в коллекции");
        System.out.println("remove_lower {элемент} - удалить все элементы, которые меньше заданного элемента");
        System.out.println("group_counting_by_engine_power - сгруппировать элементы по мощности двигателя и показать их количество");
        System.out.println("filter_by_number_of_wheels numberOfWheels - показать элементы с заданным количеством колес");
        System.out.println("print_field_ascending_number_of_wheels - показать значения поля 'количество колес' в порядке возрастания");
    }

    /**
     * Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементом
     */
    private void info() {
        System.out.println("Collection type: " + vehicles.getClass().getName());
        System.out.println("Initialization date: " + initDate.toString());
        System.out.println("Number of elements: " + vehicles.size());
    }

    /**
     * Выводит в стандартный поток вывода все элементы коллекции в строковом представлении
     */
    private void show() {
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }


    public void add() {
        Vehicle vehicle = Vehicle.fromUser();
        vehicle.setUserLogin(user.getLogin());
        repository.addVehicle(vehicle);
        vehicles.add(vehicle);
    }

    /**
     * @param idString id элемента, который надо обновить
     *               Обновляет значения элемента с данным id
     */
    public void update(String idString) {
        int id = Integer.parseInt(idString);
        for (Vehicle v : vehicles) {
            if (v.getId() == id) {
                if (v.getUserLogin().equals(user.getLogin())) {
                    Vehicle.modifyFromUser(v);
                    repository.updateVehicle(v);
                }
                else {
                    System.out.println("У вас нет прав на редактирование этого объекта");
                }
                break;
            }
        }
    }

    /**
     * @param idString id элемента, который надо удалить
     *                 Удаляет элемент с соответствующим id
     */
    private void removeById(String idString) {
        long id = Long.parseLong(idString);
        Vehicle vehicle = vehicles.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
        if (vehicle == null) {
            throw new IllegalArgumentException("Element with given id not found.");
        }
        if (vehicle.getUserLogin().equals(user.getLogin())) {
            repository.removeVehicle(id);
            vehicles.remove(vehicle);

            System.out.println("Element removed.");
        }
        else {
            System.out.println("У вас нет прав на удаление этого объекта");
        }
    }

    /**
     * Очищает коллекцию
     */
    private void clear() {
        for (Vehicle v : vehicles) {
            if (v.getUserLogin().equals(user.getLogin())) {
                repository.removeVehicle(v.getId());
            }
        }

        vehicles.removeIf(t -> t.getUserLogin().equals(user.getLogin()));
    }

    /**
     * Возвращает коллекцию, загруженную из CSV-файла
     */
    public void load(){
        List<Vehicle> list = repository.getVehicles();
        vehicles = new TreeSet<>(list);
    }


    /**
     * @param fileName Имя файла со скриптом
     *                 Выполняет команды из файла как если бы они вводились в консоль
     */
    private void executeScript(String fileName) {
        String currentLine;
        try {
            InputStream stream = new FileInputStream(fileName);
            BufferedReader scanner = new BufferedReader(new InputStreamReader(stream));
            try (scanner){
                while ((currentLine = scanner.readLine()) != null){
                    commandHandler(currentLine);
                }
            }
            catch (IOException e){
                handleError(e);
            }
        }
        catch (FileNotFoundException e){
            handleError(e);
        }
    }

    /**
     * Завершает работу программы
     */
    private void exit() {
        System.exit(0);
    }


    public void addIfMax() {
        Vehicle vehicle = Vehicle.fromUser();
        if (vehicles.isEmpty() || vehicles.last().compareTo(vehicle) < 0) {
            repository.addVehicle(vehicle);
            vehicles.add(vehicle);
            System.out.println("Элемент добавлен в коллекцию.");
        } else {
            System.out.println("Элемент не добавлен в коллекцию.");
        }
    }


    public void addIfMin() {
        Vehicle vehicle = Vehicle.fromUser();

        if (vehicles.isEmpty() || vehicles.first().compareTo(vehicle) > 0) {
            repository.addVehicle(vehicle);
            vehicles.add(vehicle);
            System.out.println("Элемент добавлен в коллекцию.");
        } else {
            System.out.println("Элемент не добавлен в коллекцию.");
        }
    }

    public void removeLower() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите мощность: ");
        long power = scanner.nextLong();

        repository.removeLowerEnginePower(power, user);
        vehicles.removeIf(v -> v.getEnginePower() < power && v.getUserLogin().equals(user.getLogin()));
    }

    /**
     * Группирует элементы коллекции по значению поля enginePower, выводит количество элементов в каждой группе
     */
    private void groupCountingByEnginePower() {
        vehicles.stream()
                .collect(Collectors.groupingBy(Vehicle::getEnginePower, Collectors.counting()))
                .forEach((power, count) -> System.out.println("Engine power: " + power + ", count: " + count));
    }

    /**
     * @param numberOfWheelsString
     * Выводит элементы, значения поля numberOfWheels в который равно заданному
     */
    private void filterByNumberOfWheels(String numberOfWheelsString) {
        int numberOfWheels = Integer.parseInt(numberOfWheelsString);
        vehicles.stream()
                .filter(v -> v.getNumberOfWheels() == numberOfWheels)
                .forEach(System.out::println);
    }

    /**
     * Выводит значения поля numberOfWheels всех элементов в порядке возрастания
     */
    private void printFieldAscendingNumberOfWheels() {
        vehicles.stream()
                .map(Vehicle::getNumberOfWheels)
                .sorted()
                .forEach(System.out::println);
    }
    public void handleError(Exception e){
        System.out.println("Произошла ошибка: " + e.getMessage());
        System.out.println("Вы можете повторите ввод команды, или завершить выполнение программы командой exit");
    }

    public void registration(String[] tokens) {
        if (tokens.length != 3) {
            System.out.println("Неверное кол-во аргументов");
        }
        else {
            String login = tokens[1];
            String password = tokens[2];
            if (repository.getUsers().stream().anyMatch(t->t.getLogin().equals(login))) {
                System.out.println("Данные логин занят");
            }
            else {
                password = SecurityUtil.hash224(password);
                User newUser = new User(login, password);
                repository.addUser(newUser);
                System.out.println("Регистрация успешно выполнена");
            }
        }
    }

    public void authorization(String[] tokens) {
        if (tokens.length != 3) {
            System.out.println("Неверное кол-во аргументов");
        }
        else {
            String login = tokens[1];
            String password = SecurityUtil.hash224(tokens[2]);
            Optional<User> optionalUser = repository.getUsers().stream().
                    filter(t->t.getLogin().equals(login) && t.getPassword().equals(password)).findFirst();
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                System.out.println("Авторизация успешно выполнена");
            }
            else {
                System.out.println("Неверный логин или пароль");
            }
        }
    }
}
