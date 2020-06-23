package ru.keyreal.restapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.keyreal.restapi.model.Comic;

import java.util.List;

public interface ComicRepository extends MongoRepository<Comic, String> {

    Comic getById(String id);

    Comic findByTitle(String title);

    Page<Comic> findAll(Pageable pageable);

    Page<Comic> findAllByCharacteridsContains(String characterId, Pageable pageable);

    List<Comic> findAllByCharacteridsContains(String characterId);

    void deleteById(String id);

    // для фильтров
    Page<Comic> findAllByTitleContaining(String filterByTitleContainig, Pageable pageable);

    Page<Comic> findAllByCharacteridsContainsAndTitleContaining(String characterId, String comicTitleContaining, Pageable pageable);

}
