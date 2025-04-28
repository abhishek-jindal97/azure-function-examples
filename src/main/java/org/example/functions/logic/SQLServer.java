package org.example.functions.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.functions.model.Person;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLServer.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<Person> query() {

        List<Person> personList = new ArrayList<>();
        try (Connection connection = DataSource.getConnection()) {
            String query = "SELECT * from persons";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Connected to database.");

            while (resultSet.next()) {
                personList.add(new Person(resultSet.getInt("ID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName")));
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        try {
            LOGGER.info("Query: {}", new JSONArray(OBJECT_MAPPER.writeValueAsString(personList)));
        } catch (JsonProcessingException e) {
            e.getMessage();
        }
        return personList;
    }

    public ArrayList<Person> insert(ArrayList<Person> person) {

        ArrayList<Person> resultPerson = new ArrayList<>();

        try (Connection connection = DataSource.getConnection()) {
            System.out.println("Connected to database.");

            String insertQuery = "INSERT INTO persons (FirstName, LastName) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            System.out.println(preparedStatement);
            LOGGER.info("Insert into persons...");
            for (Person p : person) {
                preparedStatement.setString(1, p.firstName());
                preparedStatement.setString(2, p.lastName());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                resultPerson.add(new Person(resultSet.getInt(1),p.firstName(),p.lastName()));
                LOGGER.info("Inserted Person: {} {}", p.firstName(), p.lastName());
            }

            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultPerson;
    }
}
