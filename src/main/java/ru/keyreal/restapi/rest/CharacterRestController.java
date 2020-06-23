package ru.keyreal.restapi.rest;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.keyreal.restapi.model.Character;
import ru.keyreal.restapi.model.Comic;
import ru.keyreal.restapi.service.CharacterService;
import springfox.documentation.builders.PathSelectors;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/public/characters")
@Api(value="onlinestore", description="Операции с персонажами")
public class CharacterRestController {
    @Autowired
    private CharacterService characterService;
    Pageable pageable;

    @ApiOperation(value = "Просмотреть лист персонажей (по-умолчанию page=0, size=10, sortBy=id")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Character>> getAllCharacter(@RequestParam(defaultValue = "0") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(defaultValue = "id") String sortBy,
                                                           @RequestParam(defaultValue = "") String filterByNameContaining) {
        pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortBy));

        if(!filterByNameContaining.equals("")) {
            Page<Character> characters = this.characterService.getAllByNameContaining(filterByNameContaining, pageable);
            if (characters == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(characters, HttpStatus.OK);
        }

        Page<Character> characters = this.characterService.getAllCharacter(pageable);
        if (characters == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(characters, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить персонажа по id")
    @RequestMapping(value = "/{characterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Character> getById(@PathVariable("characterId") String characterId) {
        Character character = this.characterService.getById(characterId);

        if (character == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(character, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить все комиксы по id персонажа")
    @RequestMapping(value = "/{characterId}/comics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Comic>> getAllComicsByChacterId(@PathVariable("characterId") String characterId,
                                                               @RequestParam(defaultValue = "0") Integer pageNum,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(defaultValue = "id") String sortBy,
                                                               @RequestParam(defaultValue = "") String comicTitleContaining) {
        pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortBy));

        if (this.characterService.getById(characterId) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!comicTitleContaining.equals("")) {
            Page<Comic> comicsList = this.characterService.getAllComicsByCharacterIdWhereTitleContaining(characterId, comicTitleContaining, pageable);
            if (comicsList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(comicsList, HttpStatus.OK);
        }

        Page<Comic> comicsList = this.characterService.getAllComicsByCharacterId(characterId, pageable);

        if (comicsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comicsList, HttpStatus.OK);
    }

    @ApiOperation(value = "Удалить персонажа по id")
    @RequestMapping(value = "/{characterId}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> deleteCharacterById(@PathVariable("characterId") String characterId) {

        if (this.characterService.getById(characterId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.characterService.deleteCharacterById(characterId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // обновление полей для существующего characterId
    @ApiOperation(value = "Обновить поле(-я) персонажа по его id")
    @RequestMapping(value = "/{characterId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Character> updateCharacterById(@PathVariable("characterId") String characterId, @RequestBody Character character) {

        if (this.characterService.getById(characterId) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (character.getComicids() != null) {
            if (!this.characterService.isGoodComicids(character))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Character character1 = this.characterService.updateCharacter(characterId, character);

        if (character1.getId() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(character1,HttpStatus.OK);
    }

    // сохранение нового character
    @ApiOperation(value = "Сохранить нового персонажа в БД")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Character> saveNewCharacter(@RequestBody Character character) {

        if(this.characterService.getByName(character.getName()) != null) {
            return new ResponseEntity<>(this.characterService.getByName(character.getName()), HttpStatus.ALREADY_REPORTED);
        }

        if (character.getName().isEmpty() || character.getHeight().isEmpty() || character.getWeight().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (character.getComicids() != null) {
            if (!this.characterService.isGoodComicids(character))
                return new ResponseEntity<>((HttpStatus.BAD_REQUEST));
        }

        Character newCharacter = this.characterService.saveNewCharacter(character);
        return new ResponseEntity<>(this.characterService.getByName(newCharacter.getName()), HttpStatus.CREATED);
    }

    // добавление image в конкретный character
    @ApiOperation(value = "Добавить изображение для персонажа по его id")
    @RequestMapping(value = "/{characterId}/uploadImage", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Character> uploadImage(@PathVariable("characterId") String characterId,
                                                 @RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // файл пустой

        if (this.characterService.getById(characterId)== null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // character с этим ID отсутствует в БД
        }

        Character character = this.characterService.uploadImage(characterId, file);

        if (character == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(character, HttpStatus.OK);
    }

}
