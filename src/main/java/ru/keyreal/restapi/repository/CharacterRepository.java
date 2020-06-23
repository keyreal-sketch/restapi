package ru.keyreal.restapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.keyreal.restapi.model.Character;

import java.util.List;

public interface CharacterRepository extends MongoRepository<Character, String> {

    Character getById(String id);

    Character findByName(String name);

    Page<Character> findAll(Pageable pageable);

    Page<Character> findAllByComicidsContains(String comicId, Pageable pageable);

    List<Character> findAllByComicidsContains(String comicId);

    void deleteById(String id);

    // для фильтров
    Page<Character> findAllByNameContaining(String filterNameContaining, Pageable pageable);

    Page<Character> findAllByComicidsContainsAndNameContaining(String comicId, String characterNameContainig, Pageable pageable);
}
