package ru.liga.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User {

    @Id
    private Long id;

    @Column(name = "gender")
    private String gender;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "gender_search")
    private String genderSearch;

    @ManyToMany
    @JsonIgnore
    private List<User> favorites;
}
