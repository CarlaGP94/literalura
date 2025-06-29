package com.aluracursos.literalura.homepage;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAuthorRepository;
import com.aluracursos.literalura.repository.IBookRepository;
import com.aluracursos.literalura.service.APIConsumption;
import com.aluracursos.literalura.service.ConvertsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// En esta segunda versión, el consumo de la API se hace una única vez y luego las búsquedas se basan en lo contenido en la base de datos.
@Service
public class NewHomePage {
    private Scanner keyboard = new Scanner(System.in);
    private APIConsumption consumingAPI = new APIConsumption();
    private ConvertsData converter = new ConvertsData();
    private final String URL_BASE = "https://gutendex.com/books/?page=";
    private final Integer numPage = 10;
    @Autowired
    private IBookRepository bookRepository;
    @Autowired
    private IAuthorRepository authorRepository;

    // Métodos para el consumo de la API.
    public List<GeneralData> ConsumingAPI(String urlBase, Integer numPage) {
        List<GeneralData> data = new ArrayList<>();
        
        for (int i = 1; i <= numPage ; i++) {
            var json = consumingAPI.getData(urlBase + i);
            var dataAPI = converter.getData(json, GeneralData.class);
            data.add(dataAPI);
        }

        return data;
    }

    public List<BookData> resultOfConsumingAPI (List<GeneralData> data){
        List<BookData> result = new ArrayList<>();

        for(GeneralData dataOk : data){

            // Siempre verifica primero que las listas no estén vacías o null.
            if (dataOk.booksList() != null && !dataOk.booksList().isEmpty()) {
                result.addAll(dataOk.booksList());
            }
        }
        return result;
    }

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
    
    public List<Book> saveBook(List<BookData> bookDataList, List<Author> authorList){
        var bookList = new ArrayList<Book>();

        if (bookDataList != null && !bookDataList.isEmpty() &&
                authorList != null && !authorList.isEmpty()) {

            for (BookData bookAPI : bookDataList) {

                Optional<Book> existingBook = bookRepository.findByTitleEqualsIgnoreCase(bookAPI.title());

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

    // La base de datos contendrá los libros de las primeras 10 páginas de la API.
    public void createDataBase(){
        var data = ConsumingAPI(URL_BASE,numPage);
        var resultAPI = resultOfConsumingAPI(data);
        var authors = saveAuthor(resultAPI);
        var books = saveBook(resultAPI,authors);
    }

    // Métodos propios de la app.
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
            try {
                exit = keyboard.nextInt();
                keyboard.nextLine();

                switch (exit) {
                    case 1 -> findBookByTitle();
                    case 2 -> registeredBook();
                    case 3 -> registeredAuthors();
                    case 4 -> authorsLiveIn();
                    case 5 -> registeredBookByLanguage();
                    case 0 -> System.out.println("¡Gracias por usar nuestra aplicación!\n¡Hasta luego!\n");
                    default -> System.out.println("Opción inválida."); // en caso de que ingrese un número fuera del rango.
                }
            } catch (InputMismatchException e){ // en caso de que ingrese uno o una cadena de caracteres.
                System.out.println("Por favor, ingrese [números] únicamente.");
                keyboard.nextLine();
            }
        }
    }

    private void findBookByTitle() {

        System.out.println("Ingrese el nombre del libro:");
        var userBook = keyboard.nextLine();

        List<Book> foundBook = bookRepository.findByTitleContainsIgnoreCase(userBook);

        if (foundBook != null && !foundBook.isEmpty()) {
            System.out.println("Resultado de la búsqueda:\n");
            foundBook.forEach(System.out::println);
        } else {
            System.out.println("No encontramos el libro.");
        }
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
                "Ej.: si quieres ver autores del siglo XX, ingresarás -> año de inicio 1900 - año de fin 1999.\n\n" +
                "Ingrese el año de inicio:");

        var startYear = keyboard.nextInt();
        keyboard.nextLine();

        System.out.println("Ingrese el año de fin:");
        var endYear = keyboard.nextInt();
        keyboard.nextLine();

        List<Author> foundAuthors = authorRepository.authorsLiveIn(startYear,endYear);

        if(foundAuthors != null && !foundAuthors.isEmpty()){
            System.out.println("Resultado de la búsqueda:\n");
            foundAuthors.forEach(System.out::println);
        }
    }

    private void registeredBookByLanguage() {
        System.out.println("Ingrese el idioma para la búsqueda:\n" +
                "\"en\" para libros en inglés." +
                "\n\"es\" para libros en español." +
                "\n\"fr\" para libros en francés.");

        var userLanguage = keyboard.nextLine();

        var userLanguageInput = Language.fromString(userLanguage);

        List<Book> foundBookLanguage = bookRepository.registeredBookByLanguage(userLanguageInput);

        System.out.println("Filtrando libros por idioma...\n");
        foundBookLanguage.forEach(System.out::println);
    }
}
