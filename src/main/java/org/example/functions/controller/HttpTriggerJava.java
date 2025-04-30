package org.example.functions.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.example.functions.logic.RestAPI;
import org.example.functions.logic.SQLServer;
import org.example.functions.model.Error;
import org.example.functions.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerJava {
    /**
     * This function listens at endpoint "/api/HttpTriggerJava". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTriggerJava
     * 2. curl {your host}/api/HttpTriggerJava?name=HTTP%20Query
     */
    @FunctionName("HttpTrigger")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (!Objects.equals(request.getHttpMethod().toString(), "GET") && !Objects.equals(request.getHttpMethod().toString(), "POST")) {
            Error methodNotAllowed = new Error(405,
                    "Function accepts only GET or POST",
                    "HttpTrigger");
            return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).header("Content-Type", "application/json")
                    .body(methodNotAllowed).build();
        } else if (name == null) {
            Error badRequest = new Error(400,
                    "Please pass a name on the query string or in the request body",
                    "HttpTrigger");
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "application/json")
                    .body(badRequest).build();
        } else {
            RestAPI restAPI = new RestAPI();
            if (Objects.equals(request.getHttpMethod().toString(), "POST")) {
                return request.createResponseBuilder(HttpStatus.CREATED).header("Content-Type", "application/json").header("Sample", "function").body(restAPI.createNewUser()).build();
            } else {
                return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").header("Sample", "function").body(restAPI.listUsers()).build();
            }
        }
    }

    @FunctionName("userById")
    public HttpResponseMessage getUserById(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION, route = "userById/{id}") HttpRequestMessage<Optional<String>> request, @BindingName("id") Integer userId,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        RestAPI restAPI = new RestAPI();
        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").header("Sample", "function").body(restAPI.getUserById(userId)).build();
    }

    @FunctionName("querySQL")
    public HttpResponseMessage querySQL(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION, route = "sql") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        SQLServer sqlServer = new SQLServer();
        if (Objects.equals(request.getHttpMethod().toString(), "POST")) {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Person> personL;
            try {
                personL = mapper.readValue(request.getBody().orElse(""),
                        new TypeReference<>() {
                        });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return request.createResponseBuilder(HttpStatus.CREATED).header("Content-Type", "application/json").body(sqlServer.insert(personL)).build();
        } else {
            List<Person> personList = sqlServer.query();
            return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(personList).build();
        }
    }
}