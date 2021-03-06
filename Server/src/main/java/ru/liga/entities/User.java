package ru.liga.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Пользователь.
 */
@Getter
@Setter
@Entity
@Table(name = "accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User {
    /**
     * Id пользователя.
     */
    @Id
    @Column(name = "id")
    private Long id;
    /**
     * Половая принадлежность пользователя.
     */
    @Column(name = "gender")
    private String gender;
    /**
     * Имя пользователя.
     */
    @Column(name = "name")
    private String name;
    /**
     * Описание пользователя.
     */
    @Column(name = "description", length = 1024)
    private String description;
    /**
     * Пол, который ищет пользователь.
     */
    @Column(name = "gender_search")
    private String genderSearch;
    /**
     * Список любимцев.
     */
    @ManyToMany
    @JsonIgnore
    private List<User> favorites;
}
