package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event implements Serializable {
    Timestamp timestamp;
    int eventId;
    @NotEmpty(message = "Идентификатор пользователя не может быть пустым.")
    int userId;
    @NotEmpty(message = "Идентификатор сущности не может быть пустым.")
    int entityId;
    @NotEmpty(message = "Тип события не может быть пустым.")
    EventType eventType;
    @NotEmpty(message = "Тип операции не может быть пустым.")
    Operation operation;
}
