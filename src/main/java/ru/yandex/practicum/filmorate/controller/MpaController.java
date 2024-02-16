package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@AllArgsConstructor
public class MpaController {

    private MpaService mpaService;

    @GetMapping()
    public List<Mpa> getAll() {
        log.info("Got all MPA request");
        return mpaService.getAllMpa();
    }

    @GetMapping(value = "/{id}")
    public Mpa getMpa(@Valid @PathVariable Integer id) throws NoDataFoundException {
        log.info("Got MPA request");
        return mpaService.getMpaById(id);
    }

}
