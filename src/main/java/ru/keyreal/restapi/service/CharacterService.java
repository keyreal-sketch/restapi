package ru.keyreal.restapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.keyreal.restapi.model.Character;
import ru.keyreal.restapi.model.Comic;
import ru.keyreal.restapi.repository.CharacterRepository;
import ru.keyreal.restapi.repository.ComicRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class CharacterService {
    @Autowired
    CharacterRepository characterRepository;
    @Autowired
    ComicRepository comicRepository;

    public Character getById(String id) {
        return characterRepository.getById(id);
    }

    public Page<Character> getAllCharacter(Pageable pageable) {
        return characterRepository.findAll(pageable);
    }

    public Page<Comic> getAllComicsByCharacterId(String characterId, Pageable pageable) {
        return this.comicRepository.findAllByCharacteridsContains(characterId, pageable);
    }

    public Page<Comic> getAllComicsByCharacterIdWhereTitleContaining(String characterId, String comicTitleContaining, Pageable pageable) {
        return this.comicRepository.findAllByCharacteridsContainsAndTitleContaining(characterId, comicTitleContaining, pageable);
    }

    public void deleteCharacterById(String characterId) {
        List<Comic> listComic = comicRepository.findAllByCharacteridsContains(characterId);
        Iterator<Comic> iteratorList = listComic.iterator();
        while(iteratorList.hasNext()) {
            Comic updateComic = iteratorList.next();
            Iterator<String> iteratorCharacterIds = updateComic.getCharacterids().iterator();
            while (iteratorCharacterIds.hasNext()) {
                String deleteString = iteratorCharacterIds.next();
                if (deleteString.equals(characterId)) {
                    iteratorCharacterIds.remove();
                }
                comicRepository.save(updateComic);
            }
        }

        characterRepository.deleteById(characterId);
    }

    public Character getByName(String name) {
        return characterRepository.findByName(name);
    }

    public Character saveNewCharacter(Character character) {

        Character newCharacter = this.characterRepository.save(character);

        // добавление id объекта character в character_ids объекта comic
        // поиск объектов comic делаю на основе поля character.comic_ids ----------------------------
        if (character.getComicids() != null) {
            Iterator<String> iterator = newCharacter.getComicids().iterator();
            Comic findComic;
            while (iterator.hasNext()) {
                List<String> tempCharacter_ids;
                String tempStr = iterator.next();
                findComic = comicRepository.getById(tempStr);

                if (findComic.getCharacterids() == null) {
                    tempCharacter_ids = new ArrayList<>();
                } else {
                    tempCharacter_ids = findComic.getCharacterids();
                }
                //
                tempCharacter_ids.add(newCharacter.getId());
                findComic.setCharacterids(tempCharacter_ids);
                // здесь нужно сказать БД, чтобы она записала (обновила) поле character_ids для findComic
                comicRepository.save(findComic); // mongo db должна сделать update
            }
        }

        return characterRepository.findByName(newCharacter.getName());
    }

    public Character uploadImage(String characterId, MultipartFile file) {

        byte[] bytes;
        try {
            bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("uploadedFile")));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            return null;
        }

        Character character = characterRepository.getById(characterId);
        String bytesToString = Base64.getEncoder().encodeToString(bytes);
        List<String> tempList = new ArrayList<>();

        if (character.getListimage() == null) {
            tempList.add(bytesToString);
        } else {
            tempList = character.getListimage();
            tempList.add(bytesToString);
        }

        character.setListimage(tempList);
        characterRepository.save(character);

        return characterRepository.getById(characterId);
    }

    public Character updateCharacter(String characterId, Character character) {

        Character currentCharacter = characterRepository.getById(characterId);

        if (!character.getName().isEmpty()) {
            if (characterRepository.findByName(character.getName()) != null)
                return new Character();
            currentCharacter.setName(character.getName());
        }

        if (!character.getWeight().isEmpty()) {
            currentCharacter.setWeight(character.getWeight());
        }

        if (!character.getHeight().isEmpty()) {
            currentCharacter.setHeight(character.getHeight());
        }

        if (character.getComicids() != null) {
            // удаляем связи (если есть) с комиксами - очищаем у comic ссылки на этот character
            if (currentCharacter.getComicids() != null) {
                List<Comic> listComic = comicRepository.findAllByCharacteridsContains(characterId);
                Iterator<Comic> iteratorList = listComic.iterator();
                while(iteratorList.hasNext()) {
                    Comic updateComic = iteratorList.next();
                    Iterator<String> iteratorCharacterIds = updateComic.getCharacterids().iterator();
                    while (iteratorCharacterIds.hasNext()) {
                        String deleteString = iteratorCharacterIds.next();
                        if (deleteString.equals(characterId)) {
                            iteratorCharacterIds.remove();
                        }
                        comicRepository.save(updateComic);
                    }
                }
            }
            // устанавливаем новые значения для Comicids
            currentCharacter.setComicids(character.getComicids());

            // прописываем себя в comic.characterids в соответствии с character.getComicids
            Iterator<String> iterator = currentCharacter.getComicids().iterator();
            while (iterator.hasNext()) {
                String nextId = iterator.next();
                Comic updComic = this.comicRepository.getById(nextId);
                List<String> tempList = updComic.getCharacterids();
                if (tempList != null) {
                    tempList.add(currentCharacter.getId());
                } else {
                    tempList = new ArrayList<>();
                    tempList.add(currentCharacter.getId());
                }
                updComic.setCharacterids(tempList);
                comicRepository.save(updComic);
            }
        }
        characterRepository.save(currentCharacter);

        return this.characterRepository.getById(characterId);
    }

    // проверяем есть ли объекты в comicRepository c id = элементу из character.getComicids()
    public boolean isGoodComicids(Character character) {
        List<String> newlistComicids = character.getComicids();
        for (String isString : newlistComicids) {
            if (this.comicRepository.getById(isString) == null) {
                return false;
            }
        }
        return true;
    }

    // фильтры ------------------

    public Page<Character> getAllByNameContaining(String filterNameContaining, Pageable pageable) {
        return characterRepository.findAllByNameContaining(filterNameContaining, pageable);
    }

}
