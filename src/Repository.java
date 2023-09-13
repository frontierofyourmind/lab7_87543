package src;

import src.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/studs";
    static final String USER = "postgres";
    static final String PASS = "1234";

    public Connection createConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver is not found");
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

        }  catch (SQLException e) {
            throw new RuntimeException("Connection Failed");
        }

        return connection;
    }

    public List<Vehicle> getVehicles() {
        Connection connection = createConnection();
        Statement statement = null;
        List<Vehicle> vehicles = new ArrayList<>();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM vehicle");
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                int x = resultSet.getInt("coord_x");
                int y = resultSet.getInt("coord_y");
                LocalDateTime date = resultSet.getTimestamp("creation_date").toLocalDateTime();
                long enginePower = resultSet.getLong("engine_power");
                int numberWheels = resultSet.getInt("number_wheels");
                int vehicleType = resultSet.getInt("vehicle_type");
                VehicleType vehicleTypeEnum = VehicleType.values()[vehicleType];
                int fuelType = resultSet.getInt("fuel_type");
                FuelType fuelTypeEnum = FuelType.values()[fuelType];
                String userLogin = resultSet.getString("user_login");

                Vehicle vehicle = new Vehicle(id, name, new Coordinates(x, y), date, enginePower, numberWheels, vehicleTypeEnum, fuelTypeEnum);
                vehicle.setUserLogin(userLogin);
                vehicles.add(vehicle);
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        Connection connection = createConnection();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO vehicle VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING ID");
            statement.setString(1, vehicle.getName());
            statement.setInt(2, vehicle.getCoordinates().getX());
            statement.setInt(3, vehicle.getCoordinates().getY());
            statement.setTimestamp(4, Timestamp.valueOf(vehicle.getCreationDate()));
            statement.setLong(5, vehicle.getEnginePower());
            statement.setInt(6, vehicle.getNumberOfWheels());
            statement.setInt(7, vehicle.getType().ordinal());
            statement.setInt(8, vehicle.getFuelType().ordinal());
            statement.setString(9, vehicle.getUserLogin());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            long id = resultSet.getLong(1);
            vehicle.setId(id);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateVehicle(Vehicle vehicle) {
        Connection connection = createConnection();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("UPDATE vehicle set name = ?, coord_x = ?, coord_y = ?, " +
                    "engine_power = ?, number_wheels = ?, vehicle_type = ?, fuel_type = ? WHERE id = ?");
            statement.setString(1, vehicle.getName());
            statement.setInt(2, vehicle.getCoordinates().getX());
            statement.setInt(3, vehicle.getCoordinates().getY());
            statement.setLong(4, vehicle.getEnginePower());
            statement.setInt(5, vehicle.getNumberOfWheels());
            statement.setInt(6, vehicle.getType().ordinal());
            statement.setInt(7, vehicle.getFuelType().ordinal());
            statement.setLong(8, vehicle.getId());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeVehicle(long id) {
        Connection connection = createConnection();

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("DELETE FROM vehicle WHERE id = ?");
            statement.setLong(1, id);

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAllVehicles() {
        Connection connection = createConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM vehicle");
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLowerEnginePower(long power, User user) {
        Connection connection = createConnection();

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("DELETE FROM vehicle WHERE engine_power < ? and user_login = ?");
            statement.setLong(1, power);
            statement.setString(2, user.getLogin());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() {
        Connection connection = createConnection();
        Statement statement = null;
        List<User> users = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_info");
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");

                User user = new User(id, login, password);
                users.add(user);
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public void addUser(User user) {
        Connection connection = createConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO user_info VALUES (default, ?, ?) RETURNING ID");
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            long id = resultSet.getLong(1);
            user.setId(id);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
