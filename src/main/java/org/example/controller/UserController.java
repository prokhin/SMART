package org.example.controller;

import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserController extends AllDirectives {
    private static final Map<String, User> users = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Инициализация objectMapper

    public Route createRoutes() {
        return concat(
                pathPrefix("api_v1", () ->
                        route(
                                pathPrefix("registrate", () ->
                                        post(() ->
                                                entity(Unmarshaller.entityToString(), requestData -> {
                                                    try {
                                                        User newUser = objectMapper.readValue(requestData, User.class);
                                                        String email = newUser.getEmail();
                                                        if (users.containsKey(email)) {
                                                            return complete(StatusCodes.UNPROCESSABLE_ENTITY, "{\"error\": \"session.errors.emailAlreadyRegistered\"}");
                                                        } else {
                                                            users.put(email, newUser);
                                                            return complete(StatusCodes.OK);
                                                        }
                                                    } catch (JsonProcessingException e) {
                                                        e.printStackTrace();
                                                        return complete(StatusCodes.BAD_REQUEST);
                                                    }
                                                })
                                        )
                                ),
                                pathPrefix("login", () ->
                                        post(() ->
                                                entity(Unmarshaller.entityToString(), requestData -> {
                                                    try {
                                                        User loginUser = objectMapper.readValue(requestData, User.class);
                                                        String email = loginUser.getEmail();
                                                        String password = loginUser.getPassword();

                                                        User user = users.get(email);
                                                        if (user != null && user.getPassword().equals(password)) {
                                                            return complete(StatusCodes.OK);
                                                        } else {
                                                            return complete(StatusCodes.UNPROCESSABLE_ENTITY, "{\"error\": \"Invalid credentials\"}");
                                                        }
                                                    } catch (JsonProcessingException e) {
                                                        e.printStackTrace();
                                                        return complete(StatusCodes.BAD_REQUEST);
                                                    }
                                                })
                                        )
                                ),
                                authenticateBasic("realm", credentials -> {
                                    try {
                                        if (credentials.isPresent()) {
                                            String email = credentials.get().identifier();
                                            String password = users.get(email).getPassword(); // Получаем пароль пользователя
                                            if (users.containsKey(email) && credentials.get().verify(password)) {
                                                return Optional.of(users.get(email));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return Optional.empty(); // Возвращаем пустой Optional, если пользователь не аутентифицирован или произошла ошибка
                                }, userOpt -> {
                                    if (userOpt.isPresent()) {
                                        User user = userOpt.get();
                                        return pathPrefix("me", () ->
                                                get(() ->
                                                        complete(HttpEntities.create(ContentTypes.APPLICATION_JSON, getUserJson(user)))
                                                )
                                        );
                                    } else {
                                        return complete(StatusCodes.UNAUTHORIZED);
                                    }
                                })
                                ,
                                pathPrefix("logout", () ->
                                        put(() -> complete(StatusCodes.OK))
                                )
                        )
                )
        );
    }

    private String getUserJson(User user) throws JsonProcessingException {
        return objectMapper.writeValueAsString(user);
    }
}
