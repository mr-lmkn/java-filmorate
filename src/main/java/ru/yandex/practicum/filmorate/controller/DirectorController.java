package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> findAll() {
        log.debug("Получен запрос на список всех режиссеров");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    @Validated
    public Director findDirector(@PathVariable @NotNull Integer id) throws NoDataFoundException {
        return directorService.findDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
//        if (director != null) {
            return directorService.create(director);
//        } else {
//            log.debug("Ошибка валидации пользователя: пришел null");
//            throw new ValidationException();
//        }
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) throws NoDataFoundException {
//        if (director != null) {
            directorService.update(director);
            return director;
//        } else {
//            log.debug("Ошибка валидации режиссера: пришел null");
//            throw new ValidationException();
//        }
    }

    @DeleteMapping("/{id}")
    @Validated
    public void deleteDirector(@PathVariable @NotNull Integer id) throws NoDataFoundException {
//        if (id != null) {
            directorService.deleteDirectorById(id);
//        } else {
//            log.debug("Ошибка валидации режиссера: пришел null");
//            throw new ValidationException();
//        }
    }
}
