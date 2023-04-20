package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event implements Serializable {
    Long timestamp;
    Long eventId;
    @NotEmpty(message = "Идентификатор пользователя не может быть пустым.")
    Long userId;
    @NotEmpty(message = "Идентификатор сущности не может быть пустым.")
    Long entityId;
    @NotEmpty(message = "Тип события не может быть пустым.")
    EventType eventType;
    @NotEmpty(message = "Тип операции не может быть пустым.")
    Operation operation;
}
