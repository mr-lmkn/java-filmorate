package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        log.info("Got all directors request");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    @Validated
    public Director findDirector(@PathVariable @NotNull Integer id) throws NoDataFoundException {
        log.info("Got find Director request");
        return directorService.findDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Got create Director request");
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) throws NoDataFoundException {
        log.info("Got update Director request");
        directorService.update(director);
        return director;
    }

    @DeleteMapping("/{id}")
    @Validated
    public void deleteDirector(@PathVariable @NotNull Integer id) throws NoDataFoundException {
        log.info("Got delete Director request");
        directorService.deleteDirectorById(id);
    }

}
