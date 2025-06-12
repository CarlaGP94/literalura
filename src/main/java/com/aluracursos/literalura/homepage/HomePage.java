package com.aluracursos.literalura.homepage;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAuthorRepository;
import com.aluracursos.literalura.repository.IBookRepository;
import com.aluracursos.literalura.service.APIConsumption;
import com.aluracursos.literalura.service.ConvertsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class HomePage {
    private Scanner keyboard = new Scanner(System.in);
    private APIConsumption consumingAPI = new APIConsumption();
    private ConvertsData converter = new ConvertsData();
    private final String URL_BASE= "https://gutendex.com/books/?";
    private final String URL_SEARCH = "search=";
    private final String URL_START_YEAR = "author_year_start=";
    private final String URL_END_YEAR = "&author_year_end=";
    private final String URL_LANGUAGES = "languages=";
    @Autowired
    private IBookRepository bookRepository;
    @Autowired
    private IAuthorRepository authorRepository;

    public void showMenu(){
        var exit = -1;
        while(exit != 0) {
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

            switch (exit){
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

    public GeneralData ConsumingAPI(String urlSearch, String userInput){
        var json = consumingAPI.getData(URL_BASE + urlSearch + userInput.replace(" ","%20"));
        var data = converter.getData(json, GeneralData.class);

        return data;
    }

    public GeneralData ConsumingAPI(String urlStartYear, Integer userInput1, String urlEndYear, Integer userInput2){
        var json = consumingAPI.getData(URL_BASE + urlStartYear + userInput1 + urlEndYear + userInput2);
        var data = converter.getData(json, GeneralData.class);

        return data;
    }

    public GeneralData ConsumingAPI(String urlBase, String urlLanguages, String userInput){
        var json = consumingAPI.getData(urlBase + urlLanguages + userInput.toLowerCase());
        var data = converter.getData(json, GeneralData.class);

        return data;
    }

    private void findBookByTitle() {
        System.out.println("Ingrese 5 dígitos del nombre del libro: (ej: cinde = cinderella)");
        var userBook = keyboard.nextLine();

        GeneralData data = ConsumingAPI(URL_SEARCH,userBook);

        // Verifica que las listas no estén en "null" o vacías
        if(data.booksList() != null && !data.booksList().isEmpty()){
            BookData firstBook = data.booksList().get(0); // Solo se queda el 1er libro que coincida con la búsqueda.

            if (firstBook.authorList() != null && !firstBook.authorList().isEmpty()){
                AuthorData firstAuthor = firstBook.authorList().get(0);

                // Revisa que el autor no esté repetido así guardarlo en el repositorio.
                Author authorReferent = null;
                Optional<Author> existingAuthor = authorRepository.findByCompleteName(firstAuthor.completeName());

                if (existingAuthor.isPresent()) {
                    authorReferent = existingAuthor.get();
                } else {
                    authorReferent = new Author(firstAuthor);
                    authorRepository.save(authorReferent); // Guarda el nuevo autor.
                }

                // Ahora verifica con el libro.
                Book bookReferent = null;
                Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(userBook);

                if(existingBook.isPresent()){
                    bookReferent = existingBook.get();
                    System.out.println("¡Encontramos tu libro!\n" + bookReferent);
                } else {
                    Book theBook = new Book(firstBook);
                    theBook.setAuthor(authorReferent);
                    bookRepository.save(theBook); // Guarda el nuevo libro.
                    System.out.println("¡Encontramos tu libro!\n" + theBook);
                }
            }
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
                "Ej.: si quieres ver autores del siglo XX, podrás inicio 1900 - fin 1999.\n\n" +
                "Ingrese el año de inicio:");
        var startYear = keyboard.nextInt();
        keyboard.nextLine();

        System.out.println("Ingrese el año de fin:");
        var endYear = keyboard.nextInt();
        keyboard.nextLine();

        GeneralData data = ConsumingAPI(URL_START_YEAR, startYear, URL_END_YEAR, endYear);

        // Tomará los primeros 5 resultados.
        List<BookData> theData = data.booksList().stream()
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("Encontramos los siguientes autores:\n");

        for (BookData bookAPI : theData){
            if(bookAPI.authorList() != null && !bookAPI.authorList().isEmpty()){
                AuthorData authorAPI = bookAPI.authorList().get(0);

                // Verifica si ya existe en la base de datos o hay que agregarlo.
                Author authorReferent = null;
                Optional<Author> existingAuthor = authorRepository.findByCompleteName(authorAPI.completeName());

                if (existingAuthor.isPresent()) {
                    authorReferent = existingAuthor.get();
                    System.out.println(authorReferent);
                } else {
                    authorReferent = new Author(authorAPI);
                    authorRepository.save(authorReferent);
                    System.out.println(authorReferent);
                }
            }
        }
    }

    private void registeredBookByLenguage() {
        System.out.println("Ingrese:\n" +
                "\"en\" para libros en inglés." +
                "\n\"es\" para libros en español." +
                "\n\"fr\" para libros en francés.");

        var userLanguage = keyboard.nextLine();

        GeneralData data = ConsumingAPI(URL_BASE,URL_LANGUAGES,userLanguage);

        // Tomará los primeros 5 resultados.
        List<BookData> bookByLanguage = data.booksList().stream()
                .limit(5)
                .collect(Collectors.toList());

        for(BookData bookAPI : bookByLanguage) {
            if (bookByLanguage != null && !bookByLanguage.isEmpty()) {
                AuthorData authorAPI = bookAPI.authorList().get(0);

                // Verifica si el autor ya existe en la base de datos o hay que agregarlo.
                Author authorReferent = null;
                Optional<Author> existingAuthor = authorRepository.findByCompleteName(authorAPI.completeName());

                if (existingAuthor.isPresent()) {
                    authorReferent = existingAuthor.get();
                } else {
                    authorReferent = new Author(authorAPI);
                    authorRepository.save(authorReferent);
                }

                // Verifica si el libro existe o hay que agregarlo a la base de datos.
                Book bookReferent = null;
                Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(bookAPI.title());

                if(existingBook.isPresent()){
                    bookReferent = existingBook.get();
                    System.out.println(bookReferent);
                } else {
                    Book otherBook = new Book(bookAPI);
                    otherBook.setAuthor(authorReferent);
                    bookRepository.save(otherBook); // Guarda el nuevo libro.
                    System.out.println(otherBook);
                }
            }
        }
    }
}
