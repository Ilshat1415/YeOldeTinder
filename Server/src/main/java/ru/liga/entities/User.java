package ru.liga.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User {

    @Id
    private Long id;
    private String gender;
    private String name;
    private String description;
    private String genderSearch;

    @ManyToMany
    @JsonIgnore
    private List<User> favorites;
}
