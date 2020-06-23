package ru.keyreal.restapi.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import ru.keyreal.restapi.service.ComicService;

import java.util.List;

@RestController
@RequestMapping("/v1/public/comics")
@Api(value="onlinestore", description="Операции с комиксами")
public class ComicRestController {
    @Autowired
    private ComicService comicService;
    Pageable pageable;

    @ApiOperation(value = "Просмотреть лист комиксов (по-умолчанию page=0, size=10, sortBy=id")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Comic>> getAllComic(@RequestParam(defaultValue = "0") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @RequestParam(defaultValue = "") String filterByTitleContainig) {
        pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortBy));

        if (!filterByTitleContainig.equals("")) {
            Page<Comic> comics = this.comicService.getAllByTitleContaining(filterByTitleContainig, pageable);
            if (comics == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(comics,HttpStatus.OK);
        }

        Page<Comic> comics = this.comicService.getAllComic(pageable);
        if (comics == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comics, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить комикс по id")
    @RequestMapping(value = "/{comicId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comic> getById(@PathVariable("comicId") String comicId) {
        Comic comic = this.comicService.getById(comicId);
        if (comic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comic, HttpStatus.OK);
    }

    @ApiOperation(value = "Получить всех персонажей по id комикса")
    @RequestMapping(value = "/{comicId}/characters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Character>> getAllCharactersByComicId(@PathVariable("comicId") String comicId,
                                                                     @RequestParam(defaultValue = "0") Integer pageNum,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                     @RequestParam(defaultValue = "") String characterNameContainig) {
        pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortBy));

        if (this.comicService.getById(comicId) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!characterNameContainig.equals("")) {
            Page<Character> charactersList = this.comicService.getAllByComicidsContainsAndNameContaining(comicId, characterNameContainig, pageable);
            if (charactersList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(charactersList, HttpStatus.OK);
        }

        Page<Character> charactersList = this.comicService.getAllCharactersByComicId(comicId, pageable);

        if (charactersList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(charactersList, HttpStatus.OK);
    }

    @ApiOperation(value = "Удалить комикс по id")
    @RequestMapping(value = "/{comicId}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> deleteComicById(@PathVariable("comicId") String comicId) {

        if (this.comicService.getById(comicId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.comicService.deleteComicById(comicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Сохранить новый комикс в БД")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comic> saveNewComic(@RequestBody Comic comic) {

        if(this.comicService.getByTitle(comic.getTitle()) != null) {
            return new ResponseEntity<>(this.comicService.getByTitle(comic.getTitle()), HttpStatus.ALREADY_REPORTED);
        }

        if (comic.getTitle().isEmpty() || comic.getWriter().isEmpty() || comic.getYear().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (comic.getCharacterids() != null) {
            if (!this.comicService.isGoodCharacterids(comic))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Comic newComic = this.comicService.saveNewComic(comic);
        return new ResponseEntity<>(this.comicService.getByTitle(newComic.getTitle()), HttpStatus.CREATED);
    }

    // обновление полей для существующего comicId
    @ApiOperation(value = "Обновить поле(-я) комикса по его id")
    @RequestMapping(value = "/{comicId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comic> updateComicById(@PathVariable("comicId") String comicId, @RequestBody Comic comic) {

        if (this.comicService.getById(comicId) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (comic.getCharacterids() != null) {
            if (!this.comicService.isGoodCharacterids(comic))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Comic comic1 = this.comicService.updateComic(comicId, comic);

        if (comic1.getId() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(comic1,HttpStatus.OK);
    }

    // добавление image в конкретный comic
    @ApiOperation(value = "Добавить изображение для комикса по его ID")
    @RequestMapping(value = "/{comicId}/uploadImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comic> uploadImage(@PathVariable("comicId") String comicId,
                                                 @RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // файл пустой

        if (this.comicService.getById(comicId)== null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // comic с этим ID отсутствует в БД
        }

        Comic comic = this.comicService.uploadImage(comicId, file);

        if (comic == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(comic, HttpStatus.OK);
    }

}
