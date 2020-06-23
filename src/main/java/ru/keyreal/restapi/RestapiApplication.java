package ru.keyreal.restapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.keyreal.restapi.model.Character;
import ru.keyreal.restapi.model.Comic;
import ru.keyreal.restapi.repository.CharacterRepository;
import ru.keyreal.restapi.repository.ComicRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@SpringBootApplication
public class RestapiApplication {

//	@Bean
//	CommandLineRunner init(CharacterRepository characterRepository, ComicRepository comicRepository) {
//
//		characterRepository.deleteAll();
//		comicRepository.deleteAll();
//
//		final int COUNT = 10; // количество элементов в каждой из таблиц (коллекций) в MongoDB
//
//		// создание двух коллекций с количеством элементов COUNT
//		for (int i = 0; i < COUNT; i++) {
//			characterRepository.save(new Character("Hero_"+i,"10"+i,"20"+i));
//			comicRepository.save(new Comic("Comic_"+i,"Writer_"+i,"20"+i));
//		}
//
//		// получаю IdList's
//		ArrayList<String> characterIDsList = new ArrayList<>();
//		ArrayList<String> comicIDsList = new ArrayList<>();
//		List<Character> characters = characterRepository.findAll();
//		List<Comic> comics = comicRepository.findAll();
//
//		for (Character character : characters) {
//			characterIDsList.add(character.getId());
//		}
//		for (Comic comic : comics) {
//			comicIDsList.add(comic.getId());
//		}
//
//		// вывод в консоль всех IDs
//		Iterator iteratorCharacter = characterIDsList.iterator();
//		while(iteratorCharacter.hasNext()) {
//			System.out.print("character id list:");
//			System.out.println(" "+ iteratorCharacter.next());
//		}
//		Iterator iteratorComics = comicIDsList.iterator();
//		while(iteratorComics.hasNext()) {
//			System.out.print("comic id list:");
//			System.out.println(" "+ iteratorComics.next());
//		}
//
//		// копирование ссылок (IDs) друг в друга для Character(3 comicIds) и Comic (3 characterIds)
//		ArrayList<String> tempListForCharacter = new ArrayList<>();
//		iteratorCharacter = characters.iterator();
//
//		ArrayList<String> tempListForComic = new ArrayList<>();
//		iteratorComics = comics.iterator();
//
//
//		// заполняем character коллекцию mongodb
//		for (int i = 0; i < COUNT; i++) {
//			if(i < (comicIDsList.size()- 2)) {
//				tempListForCharacter.add(comicIDsList.get(i));
//				tempListForCharacter.add(comicIDsList.get(i + 1));
//				tempListForCharacter.add(comicIDsList.get(i + 2));
//
//				if (iteratorCharacter.hasNext()) {
//					Character character = (Character) iteratorCharacter.next();
//					character.setComicids(tempListForCharacter);
//					characterRepository.save(character);
//				}
//				tempListForCharacter.clear();
//			} else if (i == (comicIDsList.size() - 2)) {
//				tempListForCharacter.add(comicIDsList.get(i));
//				tempListForCharacter.add(comicIDsList.get(i + 1));
//				tempListForCharacter.add(comicIDsList.get(i - i));
//				if(iteratorCharacter.hasNext()) {
//					Character character = (Character) iteratorCharacter.next();
//					character.setComicids(tempListForCharacter);
//					characterRepository.save(character);
//				}
//				tempListForCharacter.clear();
//			} else if (i > (comicIDsList.size() - 2)) {
//				tempListForCharacter.add(comicIDsList.get(i));
//				tempListForCharacter.add(comicIDsList.get(i - i));
//				tempListForCharacter.add(comicIDsList.get(i - i + 1));
//				if(iteratorCharacter.hasNext()) {
//					Character character = (Character) iteratorCharacter.next();
//					character.setComicids(tempListForCharacter);
//					characterRepository.save(character);
//				}
//				tempListForCharacter.clear();
//			}
//		}
//
//		// заполняем comic коллекцию mongodb
//		for (int i = 0; i < COUNT; i++) {
//			if(i < (characterIDsList.size() - (COUNT-1))) {
//				tempListForComic.add(characterIDsList.get(i + (COUNT-2)));
//				tempListForComic.add(characterIDsList.get(i + (COUNT-1)));
//				tempListForComic.add(characterIDsList.get(i));
//				if (iteratorComics.hasNext()) {
//					Comic comic = (Comic) iteratorComics.next();
//					comic.setCharacterids(tempListForComic);
//					comicRepository.save(comic);
//				}
//				tempListForComic.clear();
//			} else if (i == (characterIDsList.size() - (COUNT-1))) {
//				tempListForComic.add(characterIDsList.get(i + (COUNT-2)));
//				tempListForComic.add(characterIDsList.get(i - 1));
//				tempListForComic.add(characterIDsList.get(i));
//				if (iteratorComics.hasNext()) {
//					Comic comic = (Comic) iteratorComics.next();
//					comic.setCharacterids(tempListForComic);
//					comicRepository.save(comic);
//				}
//				tempListForComic.clear();
//			} else if (i > (comicIDsList.size() - (COUNT-1))) {
//				tempListForComic.add(characterIDsList.get(i - 2));
//				tempListForComic.add(characterIDsList.get(i - 1));
//				tempListForComic.add(characterIDsList.get(i));
//				if (iteratorComics.hasNext()) {
//					Comic comic = (Comic) iteratorComics.next();
//					comic.setCharacterids(tempListForComic);
//					comicRepository.save(comic);
//				}
//				tempListForComic.clear();
//			}
//		}
//
//		// выводим всех в консоль
//		characters = characterRepository.findAll();
//		comics = comicRepository.findAll();
//		for (Character character : characters) {
//			System.out.println(character);
//		}
//		for (Comic comic : comics) {
//			System.out.println(comic);
//		}
//
//		return null;
//	}



	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

}
