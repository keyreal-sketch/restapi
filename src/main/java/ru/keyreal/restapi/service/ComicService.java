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
public class ComicService {
    @Autowired
    ComicRepository comicRepository;
    @Autowired
    CharacterRepository characterRepository;

    public Comic getById(String comicId) {
        return comicRepository.getById(comicId);
    }

    public Page<Comic> getAllComic(Pageable pageable) {
        return comicRepository.findAll(pageable);
    }

    public Page<Character> getAllCharactersByComicId(String comicId, Pageable pageable) {
        return this.characterRepository.findAllByComicidsContains(comicId, pageable);
    }
    public Page<Character> getAllByComicidsContainsAndNameContaining(String comicId, String characterNameContainig, Pageable pageable) {
        return this.characterRepository.findAllByComicidsContainsAndNameContaining(comicId, characterNameContainig, pageable);
    }

//    public List<Character> getAllCharactersByComicId(String comicId) {
//
//        List<String> characterIdList = comicRepository.getById(comicId).getCharacterids();
//
//        if (characterIdList == null) {
//            return Collections.emptyList();
//        }
//
//        List<Character> charactersList = new ArrayList<>();
//        for (String idList : characterIdList) {
//            charactersList.add(characterRepository.getById(idList));
//        }
//        return charactersList;
//    }

    public void deleteComicById(String comicId) {

        List<Character> listCharacter = characterRepository.findAllByComicidsContains(comicId);
        Iterator<Character> iteratorList = listCharacter.iterator();
        while(iteratorList.hasNext()) {
            Character updateCharacter = iteratorList.next();
            Iterator<String> iteratorComicIds = updateCharacter.getComicids().iterator();
            while (iteratorComicIds.hasNext()) {
                String deleteString = iteratorComicIds.next();
                if (deleteString.equals(comicId)) {
                    iteratorComicIds.remove();
                }
                characterRepository.save(updateCharacter);
            }
        }

        comicRepository.deleteById(comicId);
    }

    public Comic getByTitle(String title) {
        return comicRepository.findByTitle(title);
    }

    public Comic saveNewComic(Comic comic) {
        Comic newComic = this.comicRepository.save(comic);

        // добавление id объекта comic в comic_ids объекта character
        // поиск объектов character делаю на основе поля comic.character_ids ----------------------------
        if(comic.getCharacterids() != null) {
            Iterator<String> iterator = newComic.getCharacterids().iterator();
            Character findCharacter;
            while (iterator.hasNext()) {
                List<String> tempComic_ids;
                String tempStr = iterator.next();
                findCharacter = characterRepository.getById(tempStr);
                //
                if (findCharacter.getComicids() == null) {
                    tempComic_ids = new ArrayList<>();
                } else {
                    tempComic_ids = findCharacter.getComicids();
                }
                //
                tempComic_ids.add(newComic.getId());
                findCharacter.setComicids(tempComic_ids);
                // здесь нужно сказать БД, чтобы она записала (обновила) поле comic_ids для findCharacter
                characterRepository.save(findCharacter); // mongo db должна сделать update
            }
        }

        return comicRepository.findByTitle(newComic.getTitle());
    }

    public Comic uploadImage(String comicId, MultipartFile file) {

        byte[] bytes;
        try {
            bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("uploadedFile")));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            return null;
        }

        Comic comic = comicRepository.getById(comicId);
        String bytesToString = Base64.getEncoder().encodeToString(bytes);
        List<String> tempList = new ArrayList<>();

        if (comic.getListimage() == null) {
            tempList.add(bytesToString);
        } else {
            tempList = comic.getListimage();
            tempList.add(bytesToString);
        }

        comic.setListimage(tempList);
        comicRepository.save(comic);

        return comicRepository.getById(comicId);
    }

    public Comic updateComic(String comicId, Comic comic) {

        Comic currentComic = comicRepository.getById(comicId);

        if (!comic.getTitle().isEmpty()) {
            if (comicRepository.findByTitle(comic.getTitle()) != null)
                return new Comic();
            currentComic.setTitle(comic.getTitle());
        }

        if (!comic.getWriter().isEmpty()) {
            currentComic.setWriter(comic.getWriter());
        }

        if (!comic.getYear().isEmpty()) {
            currentComic.setYear(comic.getYear());
        }

        if (comic.getCharacterids() != null) {
            // удаляем связи (если есть) с персонажами - очищаем у character ссылки на этот comic
            if (currentComic.getCharacterids() != null) {
                List<Character> listCharacter = characterRepository.findAllByComicidsContains(comicId);
                Iterator<Character> iteratorList = listCharacter.iterator();
                while(iteratorList.hasNext()) {
                    Character updateCharacter = iteratorList.next();
                    Iterator<String> iteratorComicIds = updateCharacter.getComicids().iterator();
                    while (iteratorComicIds.hasNext()) {
                        String deleteString = iteratorComicIds.next();
                        if (deleteString.equals(comicId)) {
                            iteratorComicIds.remove();
                        }
                        characterRepository.save(updateCharacter);
                    }
                }
            }
            // устанавливаем новые значения для Characterids
            currentComic.setCharacterids(comic.getCharacterids());

            // прописываем себя в character.comicids в соответствии с comic.getCharacterids
            Iterator<String> iterator = currentComic.getCharacterids().iterator();
            while (iterator.hasNext()) {
                String nextId = iterator.next();
                Character updCharacter = this.characterRepository.getById(nextId);
                List<String> tempList = updCharacter.getComicids();
                if (tempList != null) {
                    tempList.add(currentComic.getId());
                } else {
                    tempList = new ArrayList<>();
                    tempList.add(currentComic.getId());
                }
                updCharacter.setComicids(tempList);
                characterRepository.save(updCharacter);
            }
        }
        comicRepository.save(currentComic);

        return this.comicRepository.getById(comicId);
    }

    // проверяем, всем ли новым значениям в characterids есть соответствие character
    public boolean isGoodCharacterids(Comic comic) {
        List<String> newlistCharacterids = comic.getCharacterids();
        Iterator<String> listIterator = newlistCharacterids.iterator();
        while (listIterator.hasNext()) {
            String isString = listIterator.next();
            if (this.characterRepository.getById(isString) == null) {
                return false;
            }
        }
        return true;
    }

    // фильтры ----
    public Page<Comic> getAllByTitleContaining(String filterByTitleContainig, Pageable pageable) {
        return comicRepository.findAllByTitleContaining(filterByTitleContainig, pageable);
    }

}
