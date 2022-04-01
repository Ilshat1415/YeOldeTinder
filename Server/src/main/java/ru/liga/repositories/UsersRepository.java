package ru.liga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.liga.entities.User;

public interface UsersRepository extends JpaRepository<User, Long> {
}
