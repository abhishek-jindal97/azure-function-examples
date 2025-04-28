package org.example.functions.logic;

import org.example.functions.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.List;

public class RestAPI {

    public List<User> listUsers() {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(ExampleUtils.USER_API)).GET().build();

        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return ExampleUtils.toList(httpResponse.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserById(int id) {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(ExampleUtils.USER_API + "/" + id)).GET().build();

        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return ExampleUtils.toObject(httpResponse.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User createNewUser() {
        System.out.println("\nCreating a new user using Java 11 HttpClient:");

        User user = ExampleUtils.buildUser();
        HttpRequest.BodyPublisher userPublisher = HttpRequest.BodyPublishers.ofString(ExampleUtils.toJson(user));

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(ExampleUtils.USER_API))
                .POST(userPublisher)
                .setHeader("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            int statusCode = response.statusCode();
            System.out.println("HTTP status: " + statusCode);

            User createdUser = ExampleUtils.toObject(response.body());
            System.out.println("Created new user: " + createdUser);

            System.out.println("Headers:");
            response.headers().map().forEach((header, value) -> System.out.println(header + " = " + String.join(", ", value)));
            return createdUser;
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}