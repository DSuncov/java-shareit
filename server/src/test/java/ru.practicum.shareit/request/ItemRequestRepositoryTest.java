package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestor;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        requestor = new User(null, "User № 1", "user_1@yandex.ru");
        entityManager.persist(requestor);
        entityManager.flush();

        itemRequest1 = new ItemRequest(null, "itemRequest_№1_description", requestor, null);
        entityManager.persist(itemRequest1);
        entityManager.flush();

        itemRequest2 = new ItemRequest(null, "itemRequest_№2_description", requestor, null);
        entityManager.persist(itemRequest2);
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

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE requests RESTART IDENTITY")
                .executeUpdate();
    }

    @Test
    @DisplayName("Вовзращает список запросов автора")
    void getAllRequestsByAuthor_ShouldReturnListOfItemRequests() {
        List<ItemRequest> requests = itemRequestRepository.findItemRequestsByAuthor(1L);

        assertEquals(2, requests.size());
        assertEquals("itemRequest_№1_description", requests.get(0).getDescription());
        assertEquals(1L, requests.get(0).getRequestor().getId());
    }

    @Test
    @DisplayName("Возвращает запрос по id")
    void getRequestsById_ShouldReturnExistRequest() {
        Optional<ItemRequest> request = itemRequestRepository.findById(1L);

        assertTrue(request.isPresent());
        assertEquals("itemRequest_№1_description", request.get().getDescription());
        assertEquals(1L, request.get().getRequestor().getId());
    }

    @Test
    @DisplayName("Создет новый запрос")
    void save_ShouldReturnNewRequest() {
        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(null, "itemRequest_№3_description", requestor, null));

        assertEquals(3, itemRequestRepository.findAll().size());
        assertEquals("itemRequest_№3_description", request.getDescription());
        assertEquals(1L, request.getRequestor().getId());
    }

    @Test
    @DisplayName("Обновляет запрос")
    void save_ShouldReturnUpdateRequest() {
        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(1L, "itemRequest_№1_update_description", requestor, null));

        assertEquals(2, itemRequestRepository.findAll().size());
        assertEquals("itemRequest_№1_update_description", request.getDescription());
    }
}
