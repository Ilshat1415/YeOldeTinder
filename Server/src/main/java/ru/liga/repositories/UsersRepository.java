package ru.liga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.liga.entities.User;

/**
 * Интерфейс репозитория пользователей.
 */
public interface UsersRepository extends JpaRepository<User, Long> {
}
