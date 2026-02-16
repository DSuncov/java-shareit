package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User requestor;
    private User author;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    private Item item1;
    private Item item2;

    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User(null, "User Owner", "user_owner@yandex.ru");
        entityManager.persist(owner);
        entityManager.flush();

        requestor = new User(null, "User Requestor", "user_requestor@yandex.ru");
        entityManager.persist(requestor);
        entityManager.flush();

        author = new User(null, "User Author", "user_author@yandex.ru");
        entityManager.persist(author);
        entityManager.flush();

        itemRequest1 = new ItemRequest(null, "itemRequest_№1_description", requestor, null);
        entityManager.persist(itemRequest1);
        entityManager.flush();

        itemRequest2 = new ItemRequest(null, "itemRequest_№2_description", requestor, null);
        entityManager.persist(itemRequest2);
        entityManager.flush();

        item1 = new Item(null, "Item № 1 name", "Item № 1 description", true, owner, itemRequest1);
        entityManager.persist(item1);
        entityManager.flush();

        item2 = new Item(null, "Item № 2 name", "Item № 2 description", false, owner, itemRequest2);
        entityManager.persist(item2);
        entityManager.flush();

        comment = new Comment();
        comment.setText("text");
        comment.setAuthor(author);
        comment.setItem(item1);
        entityManager.persist(comment);
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
                .createNativeQuery("TRUNCATE TABLE items RESTART IDENTITY")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE requests RESTART IDENTITY")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE comments RESTART IDENTITY")
                .executeUpdate();
    }

    @Test
    @DisplayName("Получаем все вещи пользователя-владельца")
    void getAllItemsByOwner_ShouldReturnListOfItems() {
        List<Item> items = itemRepository.findAllItemsByOwner(1L);

        assertEquals(2, items.size());
        assertEquals("Item № 1 name", items.get(0).getName());
        assertEquals("Item № 2 name", items.get(1).getName());
    }

    @Test
    @DisplayName("Получаем вещь по id")
    void getItemById_ShouldReturnItem() {
        Optional<Item> item = itemRepository.findById(1L);

        assertTrue(item.isPresent());
        assertEquals("Item № 1 name", item.get().getName());
        assertEquals(1L, item.get().getOwner().getId());
    }

    @Test
    @DisplayName("Создаем новую вещь")
    void createItem_ShouldReturnNewItem() {
        Item item = itemRepository.save(new Item(null,"Item № 3 name", "Item № 3 description", true, owner, null));

        assertEquals(3, itemRepository.findAll().size());
        assertEquals("Item № 3 name", item.getName());
        assertEquals(1L, item.getOwner().getId());
    }

    @Test
    void search_ShouldReturnItem() {
        List<Item> items = itemRepository.search("nAmE", 1L);

        assertEquals(1, items.size());
        assertEquals("Item № 1 name", items.getFirst().getName());
        assertEquals(true, items.getFirst().getAvailable());
    }

    @Test
    @DisplayName("Получаем вещи по id запроса")
    void findItemsForRequest_ShouldReturnItems() {
        List<Item> items = itemRepository.findItemsForRequest(2L);

        assertEquals(1, items.size());
        assertEquals("Item № 2 name", items.getFirst().getName());
        assertEquals(false, items.getFirst().getAvailable());
    }

    @Test
    @DisplayName("Получаем отзыв по id")
    void findById_ShouldReturnComment() {
        Optional<Comment> comment = commentRepository.findById(1L);

        assertTrue(comment.isPresent());
        assertEquals("text", comment.get().getText());
        assertEquals(3L, comment.get().getAuthor().getId());
        assertEquals(1L, comment.get().getItem().getId());
    }

    @Test
    @DisplayName("Создаем новый отзыв")
    void save_ShouldReturnNewComment() {
        Comment comment = new Comment();
        comment.setText("text new");
        comment.setAuthor(author);
        comment.setItem(item2);

        Comment created = commentRepository.save(comment);

        assertEquals(2, commentRepository.findAll().size());
        assertEquals("text new", created.getText());
        assertEquals(3L, created.getAuthor().getId());
        assertEquals(2L, created.getItem().getId());
    }
}
