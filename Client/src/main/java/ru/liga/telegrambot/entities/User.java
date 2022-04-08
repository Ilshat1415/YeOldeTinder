package ru.liga.telegrambot.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * Пользователь.
 */
@Getter
@Setter
public class User {
    /**
     * Id пользователя.
     */
    private Long id;
    /**
     * Половая принадлежность пользователя.
     */
    private String gender;
    /**
     * Имя пользователя.
     */
    private String name;
    /**
     * Описание пользователя.
     */
    private String description;
    /**
     * Пол, который ищет пользователь.
     */
    private String genderSearch;

    /**
     * Создание пользователя, с присвоением id.
     *
     * @param id id пользователя
     */
    public User(Long id) {
        this.id = id;
    }

    public User() {}
}
