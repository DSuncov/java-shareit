package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "User № 1", "user_1@yandex.ru");
        entityManager.persist(user1);
        entityManager.flush();

        user2 = new User(null, "User № 2", "user_2@yandex.ru");
        entityManager.persist(user2);
        entityManager.flush();
    }

    @AfterEach
    void setAfter() {
        testEntityManager.getEntityManager()
                .createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY")
                .executeUpdate();
    }

    @Test
    @DisplayName("Возвращает пользователя по email")
    void findByEmail_ShouldReturnUser() {
        Optional<User> result = userRepository.findByIdEmail("user_1@yandex.ru");

        assertTrue(result.isPresent());
        assertEquals("User № 1", result.get().getName());
        assertEquals("user_1@yandex.ru", result.get().getEmail());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Возвращает пустой Optional")
    void findByEmail_ShouldReturnEmptyUserWithWrongEmail() {
        Optional<User> result = userRepository.findByIdEmail("user_wrong_email@yandex.ru");

        assertTrue(result.isEmpty());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Возвращает пользователя по id")
    void findById_ShouldReturnUser() {
        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("User № 1", result.get().getName());
        assertEquals("user_1@yandex.ru", result.get().getEmail());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Возвращает пустой Optional")
    void findById_ShouldReturnEmptyUserWithWrongId() {
        Optional<User> result = userRepository.findById(3L);

        assertTrue(result.isEmpty());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Добавляет 3-го user")
    void save_ShouldSaveNewUser() {
        assertEquals(2, userRepository.findAll().size());
        userRepository.save(new User(null, "User № 3", "user_3yandex.ru"));
        assertEquals(3, userRepository.findAll().size());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Удаляет существующего пользователя")
    void delete_ShouldDeleteExistUser() {
        userRepository.deleteById(1L);
        assertTrue(userRepository.findById(1L).isEmpty());
        assertEquals(1, userRepository.findAll().size());
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Возвращает имя пользователя по id")
    void findNameByUser_ShouldReturnName() {
        String name = userRepository.findNameByUser(1L);
        assertEquals("User № 1", name);
        userRepository.deleteAll();
    }
}
