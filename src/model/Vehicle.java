package src.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.io.BufferedReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;


/**
 * Класс, экземпляры которого хранятся в коллекции
 */
public class Vehicle implements Comparable<Vehicle> {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long enginePower; //Поле может быть null, Значение поля должно быть больше 0
    private int numberOfWheels; //Значение поля должно быть больше 0
    private VehicleType type; //Поле может быть null
    private FuelType fuelType; //Поле может быть null
    private String userLogin;


    private static Set<Long> usedIds = new HashSet<Long>(); // хранилище уже использованных id

    /**
     * @param id id, которое нужно добавить в множество использованных
     *           Добавляет id в список использованных
     */
    public static void updateId(Long id){
        usedIds.add(id);
    }

    // Конструктор с параметрами
    public Vehicle(String name, Coordinates coordinates, Long enginePower, int numberOfWheels, VehicleType type, FuelType fuelType) {
        this.name = name;
        this.coordinates = coordinates;
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.type = type;
        this.fuelType = fuelType;

        // Установка id и даты создания
        this.id = generateId();
        this.creationDate = LocalDateTime.now();
    }

    public Vehicle(Long id, String name, Coordinates coordinates, LocalDateTime creationDate, Long enginePower, int numberOfWheels, VehicleType type, FuelType fuelType) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.type = type;
        this.fuelType = fuelType;
    }

    public Vehicle() {

    }

    /**
     * Генератор новых уникальных id
     */
    // Метод для генерации уникального id
    private Long generateId() {
//        Long newId = null;
//        do {
//            newId = (long) (Math.random() * Long.MAX_VALUE);
//        } while (newId <= 0 || usedIds.contains(newId));
        Long newId = 0L;
        while (usedIds.contains(newId)){
            newId += 1L;
        }
        usedIds.add(newId);
        return newId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id){
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Long enginePower) {
        if (enginePower != null && enginePower <= 0) {
            throw new IllegalArgumentException("Engine power must be greater than 0");
        }
        this.enginePower = enginePower;
    }

    public int getNumberOfWheels() {
        return numberOfWheels;
    }

    public void setNumberOfWheels(int numberOfWheels) {
        if (numberOfWheels <= 0) {
            throw new IllegalArgumentException("Number of wheels must be greater than 0");
        }
        this.numberOfWheels = numberOfWheels;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }


    /**
     * Реализует сравнение элементов для возможности сортировки
     * Сравнение происходит по полю enginePower, при его совпадении - по дате создания
     */
    @Override
    public int compareTo(Vehicle o) {
        int result = this.getEnginePower().compareTo(o.getEnginePower());
        if (result == 0){
            result = this.getCreationDate().compareTo(o.getCreationDate());
        }
        return result;
    }

    /**
     * Строковое представление экземпляра класса
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", enginePower=" + enginePower +
                ", numberOfWheels=" + numberOfWheels +
                ", type=" + type +
                ", fuelType=" + fuelType +
                '}';
    }

    /**
     * Изменяет значения полей класса, сохраняя генерируемые автоматически
     */
    public static void modifyFromUser(Vehicle vehicle){
        Scanner scanner = new Scanner(System.in);

        //спросить что поменять 1.Название 2.Мощность
        System.out.println("Что хотите поменять?(варианты:1 - имя, 2 - координаты, 3 - мощность, 4-  число колес, 5 - тип, 6 - топливо) : ");
        //здесь должно считываться слово в переменную change, обозначающее что мы хотим поменять
        int change = Integer.parseInt(scanner.nextLine());
        if (change == 1) {
            System.out.println("Введите имя: ");
            String name = "";
            while (name.isEmpty()) {
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Введите корректное значение. Поле не может быть пустым.");
                }
            }
            vehicle.setName(name);
        }
        else if (change == 2) {
            Integer coordX = null;
            System.out.println("Введите координату X, X <= 970: ");
            try {
                coordX = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
            while (coordX == null || coordX > 970) {
                System.out.println("Ошибка! Попробуйте еще раз.");
                System.out.println("Введите координату X, X <= 970: ");
                try {
                    coordX = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод: " + e.getMessage());
                }
            }


            Integer coordY = null;
            System.out.println("Введите координату Y, Y > -988: ");
            try {
                coordY = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
            while (coordY == null || coordY <= -988) {
                System.out.println("Ошибка! Попробуйте еще раз.");
                System.out.println("Введите координату Y, Y > -988: ");
                try {
                    coordY = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод: " + e.getMessage());
                }
            }

            Coordinates coordinates = new Coordinates(coordX, coordY);
            vehicle.setCoordinates(coordinates);
        }
        else if (change == 3) {
            Long enginePower = null;
            System.out.println("Введите мощность двигателя: ");
            try {
                String input = scanner.nextLine();
                enginePower = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
            while (enginePower <= 0) {
                System.out.println("Ошибка! Попробуйте еще раз.");
                System.out.println("Введите мощность двигателя: ");
                try {
                    String input = scanner.nextLine();
                    if (Objects.equals(input, "")) break;
                    enginePower = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод: " + e.getMessage());
                }
            }
            vehicle.setEnginePower(enginePower);
        }
        else if (change == 4) {
            Integer numberOfWheels = null;
            System.out.println("Введите количество колёс: ");
            try {
                numberOfWheels = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
            while (numberOfWheels == null || numberOfWheels <= 0) {
                System.out.println("Ошибка! Попробуйте еще раз.");
                System.out.println("Введите количество колёс: ");
                try {
                    numberOfWheels = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод: " + e.getMessage());
                }

            }
            vehicle.setNumberOfWheels(numberOfWheels);
        }
        else if (change == 5) {
            VehicleType vehicleType = null;
            while (true) {
                System.out.println("Возможные виды транспорта:");
                for (VehicleType VT : VehicleType.values()) {
                    System.out.println(VT.name() + " ");
                }
                System.out.println();
                System.out.println("Введите вид транспорта: ");
                try {
                    String input = scanner.nextLine();
                    if (Objects.equals(input, "")) break;
                    vehicleType = VehicleType.valueOf(input.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка! Попробуйте еще раз.");
                }
            }
            vehicle.setType(vehicleType);
        }
        else if (change == 6) {
            FuelType fuelType = null;
            while (true) {
                System.out.println("Возможные варианты топлива:");
                for (FuelType FT : FuelType.values()) {
                    System.out.println(FT.name() + " ");
                }
                System.out.println();
                System.out.println("Введите тип топлива: ");
                try {
                    String input = scanner.nextLine();
                    if (input == "") break;
                    fuelType = FuelType.valueOf(input.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка! Попробуйте еще раз.");
                }
            }
            vehicle.setFuelType(fuelType);
        }
    }

    /**
     * Создаёт новый экземпляр класса на основе введённых данных
     */
    public static Vehicle fromUser(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя: ");
        String name = scanner.nextLine().trim();
        while (name == "") {
            System.out.println("Введите корректное значение. Поле не может быть пустым.");
            name = scanner.nextLine().trim();
        }


        Integer coordX = null;
        System.out.println("Введите координату X, X <= 970: ");
        try {
            coordX = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Неправильный ввод: " + e.getMessage());
        }
        while (coordX == null || coordX > 970) {
            System.out.println("Ошибка! Попробуйте еще раз.");
            System.out.println("Введите координату X, X <= 970: ");
            try {
                coordX = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
        }


        Integer coordY = null;
        System.out.println("Введите координату Y, Y > -988: ");
        try {
            coordY = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Неправильный ввод: " + e.getMessage());
        }
        while (coordY == null || coordY <= -988) {
            System.out.println("Ошибка! Попробуйте еще раз.");
            System.out.println("Введите координату Y, Y > -988: ");
            try {
                coordY = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
        }

        Coordinates coordinates = new Coordinates(coordX, coordY);

        Long enginePower = null;
        System.out.println("Введите мощность двигателя: ");
        try {
            String input = scanner.nextLine();
            enginePower = Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Неправильный ввод: " + e.getMessage());
        }
        while (enginePower <= 0) {
            System.out.println("Ошибка! Попробуйте еще раз.");
            System.out.println("Введите мощность двигателя: ");
            try {
                String input = scanner.nextLine();
                if (input == "") break;
                enginePower = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }
        }

        Integer numberOfWheels = null;
        System.out.println("Введите количество колёс: ");
        try {
            numberOfWheels = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Неправильный ввод: " + e.getMessage());
        }
        while (numberOfWheels == null || numberOfWheels <= 0) {
            System.out.println("Ошибка! Попробуйте еще раз.");
            System.out.println("Введите количество колёс: ");
            try {
                numberOfWheels = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод: " + e.getMessage());
            }

        }

        VehicleType vehicleType = null;
        while (true) {
            System.out.println("Возможные виды транспорта:");
            for (VehicleType VT : VehicleType.values()){
                System.out.println(VT.name() + " ");
            }
            System.out.println();
            System.out.println("Введите вид транспорта: ");
            try {
                String input = scanner.nextLine();
                if (input == "") break;
                vehicleType = VehicleType.valueOf(input.toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка! Попробуйте еще раз.");
            }
        }

        FuelType fuelType = null;
        while (true) {
            System.out.println("Возможные варианты топлива:");
            for (FuelType FT : FuelType.values()){
                System.out.println(FT.name() + " ");
            }
            System.out.println();
            System.out.println("Введите тип топлива: ");
            try {
                String input = scanner.nextLine();
                if (input == "") break;
                fuelType = FuelType.valueOf(input.toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка! Попробуйте еще раз.");
            }
        }

        return new Vehicle(name, coordinates, enginePower, numberOfWheels, vehicleType, fuelType);
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}