package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event implements Serializable {
    Long timestamp;
    Integer eventId;
    @NotEmpty(message = "Идентификатор пользователя не может быть пустым.")
    Long userId;
    @NotEmpty(message = "Идентификатор сущности не может быть пустым.")
    Long entityId;
    @NotEmpty(message = "Тип события не может быть пустым.")
    EventType eventType;
    @NotEmpty(message = "Тип операции не может быть пустым.")
    Operation operation;
}
