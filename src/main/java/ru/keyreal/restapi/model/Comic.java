package ru.keyreal.restapi.model;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "comic")
public class Comic {
    @Id
    @ApiModelProperty(notes = "Генерируется автоматически", position = 0, hidden = true)
    private String id;
    @ApiModelProperty(notes = "Заголовок комикса", position = 1, required = true)
    private String title;
    @ApiModelProperty(notes = "Автор комикса", position = 2, required = true)
    private String writer;
    @ApiModelProperty(notes = "Год издания комикса", position = 3, required = true)
    private String year;
    @ApiModelProperty(notes = "Список id персонажей, которые фигурируют в комиксе", position = 4)
    private List<String> characterids;
    @ApiModelProperty(notes = "Список изображений комикса. Изображения хранятся в формате списка строк (Base64)", position = 5, hidden = true)
    private List<String> listimage;

    public Comic() {

    }

    public Comic(String title, String writer, String year) {
        this.title = title;
        this.writer = writer;
        this.year = year;
    }

    public Comic(String title, String writer, String year, List<String> characterids, List<String> listimage) {
        this.title = title;
        this.writer = writer;
        this.year = year;
        this.characterids = characterids;
        this.listimage = listimage;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getCharacterids() {
        return characterids;
    }

    public void setCharacterids(List<String> characterids) {
        this.characterids = characterids;
    }

    public List<String> getListimage() {
        return listimage;
    }

    public void setListimage(List<String> listimage) {
        this.listimage = listimage;
    }

//    @Override
//    public String toString() {
//        return "Comic{" +
//                "id='" + id + '\'' +
//                ", title='" + title + '\'' +
//                ", writer='" + writer + '\'' +
//                ", year='" + year + '\'' +
//                ", characterids=" + characterids +
//                ", listimage=" + listimage +
//                '}';
//    }
}
