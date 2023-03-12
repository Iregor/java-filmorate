package ru.yandex.practicum.filmorate.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistValidator implements ConstraintValidator<Exist, Object> {

    @Autowired
    UserStorage userStorage;
    @Autowired
    FilmStorage filmStorage;
    String entityType;

    @Override
    public void initialize(Exist annotation){
        this.entityType = annotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        switch (entityType) {
            case "user":
                if (value instanceof Integer) {
                    changeContextTemplate((Integer)value, context);
                    return userStorage.findUserById((Integer)value) != null;
                } else if (value instanceof User) {
                    changeContextTemplate(((User) value).getId(), context);
                    return userStorage.findUserById(((User) value).getId()) != null;
                } else {
                    return false;
                }
            case "film":
                if (value instanceof Integer) {
                    changeContextTemplate((Integer)value, context);
                    return filmStorage.findFilmById((Integer)value) != null;
                } else if (value instanceof Film) {
                    changeContextTemplate(((Film)value).getId(), context);
                    return filmStorage.findFilmById(((Film) value).getId()) != null;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    private void changeContextTemplate(Integer id, ConstraintValidatorContext context) {
        context
                .buildConstraintViolationWithTemplate("Provided entity with id: " + id + " doesn't exist.")
                .addConstraintViolation();
    }
}

