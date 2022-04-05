package ru.liga.telegrambot.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String gender;
    private String name;
    private String description;
    private String genderSearch;
}
