package ru.practicum.shareit.request;

/**
 * TODO Sprint add-item-requests.
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Описание вещи должно быть указано.")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Пользователь, создавший запрос, должен быть указан.")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "created_date", nullable = false)
    private Instant created = Instant.now();
}
