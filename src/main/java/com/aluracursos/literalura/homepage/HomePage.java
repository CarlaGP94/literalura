package com.aluracursos.literalura.homepage;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAuthorRepository;
import com.aluracursos.literalura.repository.IBookRepository;
import com.aluracursos.literalura.service.APIConsumption;
import com.aluracursos.literalura.service.ConvertsData;
import org.antlr.v4.runtime.InputMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class HomePage {
    private Scanner keyboard = new Scanner(System.in);
    private APIConsumption consumingAPI = new APIConsumption();
    private ConvertsData converter = new ConvertsData();
    private final String URL_BASE = "https://gutendex.com/books/?";
    private final String URL_SEARCH = "search=";
    private final String URL_START_YEAR = "author_year_start=";
    private final String URL_END_YEAR = "&author_year_end=";
    private final String URL_LANGUAGES = "languages=";
    @Autowired
    private IBookRepository bookRepository;
    @Autowired
    private IAuthorRepository authorRepository;
    private GeneralData data;
    private List<BookData> bookDataList;
    private List<Author> authorList;
    private List<Book> bookList;

    public void showMenu() {
        var exit = -1;
        while (exit != 0) {
            System.out.println("""
                    \n¡Bienvenido a tu biblioteca virtual!\nIngresa una opción:\n
                    [1] Buscar libro por título.
                    [2] Listar libros registrados.
                    [3] Listar autores registrados.
                    [4] Explora autores que estaban activos en...
                    [5] Listar libros por idioma.\n
                    [0] Salir
                    """);

            exit = keyboard.nextInt();
            keyboard.nextLine();

            switch (exit) {
                case 1 -> findBookByTitle();
                case 2 -> registeredBook();
                case 3 -> registeredAuthors();
                case 4 -> authorsLiveIn();
                case 5 -> registeredBookByLenguage();
                case 0 -> System.out.println("¡Gracias por usar nuestra aplicación!\n¡Hasta luego!\n");
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    // Consumo de API -> sobrecarga de métodos para adaptarlos a los diferentes filtros.
    public GeneralData ConsumingAPI(String urlSearch, String userInput) {
        var json = consumingAPI.getData(URL_BASE + urlSearch + userInput.replace(" ", "%20"));
        var data = converter.getData(json, GeneralData.class);

        return data;
    }
    public GeneralData ConsumingAPI(String urlStartYear, Integer userInput1, String urlEndYear, Integer userInput2) {
        var json = consumingAPI.getData(URL_BASE + urlStartYear + userInput1 + urlEndYear + userInput2);
        var data = converter.getData(json, GeneralData.class);

        return data;
    }
    public GeneralData ConsumingAPI(String urlBase, String urlLanguages, String userInput) {
        var json = consumingAPI.getData(urlBase + urlLanguages + userInput.toLowerCase());
        var data = converter.getData(json, GeneralData.class);

        return data;
    }

    // Límite para el resultado del consumo de la API - n primeras coincidencias.
    public List<BookData> resultOfCosumingAPI (GeneralData data,Integer limitList){
        List<BookData> result = new ArrayList<>();

        // Siempre verifica primero que las listas no estén vacías o null.
        if (data.booksList() != null && !data.booksList().isEmpty()) {
            List<BookData> bookDataList = data.booksList().stream()
                    .limit(limitList)
                    .collect(Collectors.toList());

            result = bookDataList;
            return result;
        }
        return result;
    }

    // Verificación de la existencia o no, del autor en el repositorio.
    public List<Author> saveAuthor(List<BookData> bookDataList) {
        var authorFound = new ArrayList<Author>();

        for (BookData bookAPI : bookDataList) {
            if (bookAPI.authorList() != null && !bookAPI.authorList().isEmpty()) {
                AuthorData authorAPI = bookAPI.authorList().get(0);

                // Verifica si ya existe en la base de datos o hay que agregarlo.
                Optional<Author> existingAuthor = authorRepository.findByCompleteName(authorAPI.completeName());

                if (existingAuthor.isPresent()) {
                    authorFound.add(existingAuthor.get());
                } else {
                    Author otherAuthor = new Author(authorAPI);
                    authorRepository.save(otherAuthor); // Guarda al author
                    authorFound.add(otherAuthor);
                }
            }
        }
        return authorFound;
    }

    // Verificación de la existencia o no, del libro en el repositorio.
    public List<Book> saveBook(List<BookData> bookDataList, List<Author> authorList){
        var bookList = new ArrayList<Book>();

        if (bookDataList != null && !bookDataList.isEmpty() &&
                authorList != null && !authorList.isEmpty()) {

            for (BookData bookAPI : bookDataList) {

                Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(bookAPI.title());

                if (existingBook.isPresent()) {
                    bookList.add(existingBook.get()); // Si existe, toma los datos.
                } else {
                    Book otherBook = new Book(bookAPI);
                    otherBook.setAuthor(authorList.get(0));
                    bookRepository.save(otherBook); // Sino, guarda el nuevo libro.
                    bookList.add(otherBook);
                }
            }
        }
        
        return bookList;
    }

    // Métodos propios de la app.
    private void findBookByTitle() {
        System.out.println("Ingrese el nombre del libro:");
        var userBook = keyboard.nextLine();

        data = ConsumingAPI(URL_SEARCH, userBook.substring(0, Math.min(userBook.length(), 5)));
        bookDataList = resultOfCosumingAPI(data, 1);
        authorList = saveAuthor(bookDataList);
        bookList = saveBook(bookDataList, authorList);

        System.out.println("Resultados de tu búsqueda:\n");
        bookList.forEach(System.out::println);

    }

    private void registeredBook() {
        List<Book> savedBooks = bookRepository.findAll();
        savedBooks.forEach(System.out::println);
    }

    private void registeredAuthors() {
        List<Author> savedAuthors = authorRepository.findAll();
        savedAuthors.forEach(System.out::println);
    }

    private void authorsLiveIn() {
        System.out.println("Esta opción te permitirá buscar los autores activos dentro de un rango de años.\n" +
                "Ej.: si quieres ver autores del siglo XX, podrás inicio 1900 - fin 1999.\n\n" +
                "Ingrese el año de inicio:");
        var startYear = keyboard.nextInt();
        keyboard.nextLine();

        System.out.println("Ingrese el año de fin:");
        var endYear = keyboard.nextInt();
        keyboard.nextLine();

        data = ConsumingAPI(URL_START_YEAR, startYear, URL_END_YEAR, endYear);
        bookDataList = resultOfCosumingAPI(data,5);
        authorList = saveAuthor(bookDataList);

        System.out.println("Los autores seleccionados son:\n");
        authorList.forEach(System.out::println);
    }

    private void registeredBookByLenguage() {
        System.out.println("Ingrese:\n" +
                "\"en\" para libros en inglés." +
                "\n\"es\" para libros en español." +
                "\n\"fr\" para libros en francés.");

        var userLanguage = keyboard.nextLine();

        data = ConsumingAPI(URL_BASE,URL_LANGUAGES,userLanguage);
        bookDataList = resultOfCosumingAPI(data,5);
        authorList = saveAuthor(bookDataList);
        bookList = saveBook(bookDataList, authorList);

        System.out.println("Filtrando libros por idioma...\n");
        bookList.forEach(System.out::println);
    }
}
