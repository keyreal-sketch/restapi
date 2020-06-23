package ru.keyreal.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "character")
public class Character {
    @Id
    @ApiModelProperty(notes = "Генерируется автоматически", position = 0, hidden = true)
    private String id;
    @ApiModelProperty(notes = "Имя персонажа", position = 1, required = true)
    private String name;
    @ApiModelProperty(notes = "Вес персонажа", position = 2, required = true)
    private String weight;
    @ApiModelProperty(notes = "Рост персонажа", position = 3, required = true)
    private String height;
    @ApiModelProperty(notes = "Список id комиксов, в которых задействован персонаж", position = 4)
    private List<String> comicids;
    @ApiModelProperty(notes = "Список изображений персонажа. Изображения хранятся в формате списка строк (Base64)", position = 5, hidden = true)
    private List<String> listimage;

    public Character() {}

    public Character(String name, String weight, String height) {
        this.name = name;
        this.weight = weight;
        this.height = height;
    }

    public Character(String name, String weight, String height, List<String> comicids, List<String> listimage) {
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.comicids = comicids;
        this.listimage = listimage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public List<String> getComicids() {
        return comicids;
    }

    public void setComicids(List<String> comicids) {
        this.comicids = comicids;
    }

    public List<String> getListimage() {
        return listimage;
    }

    public void setListimage(List<String> listimage) {
        this.listimage = listimage;
    }

//    @Override
//    public String toString() {
//        return "Character{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", weight='" + weight + '\'' +
//                ", height='" + height + '\'' +
//                ", comicids=" + comicids +
//                ", listimage=" + listimage +
//                '}';
//    }
}
