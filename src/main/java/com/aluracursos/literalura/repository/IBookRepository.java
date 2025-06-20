package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Book;
import com.aluracursos.literalura.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IBookRepository extends JpaRepository<Book,Long> {

    List<Book> findByTitleContainsIgnoreCase(String userBook);

    // A partir de aquí los comandos pertenecen a la 2da versión.

    // Muchos libros suelen tener diferentes ediciones, por lo que sus títulos se asemejan bastante;
    // para guardar la variedad pero sin repeticiones, se agrega el "Equals".
    Optional<Book> findByTitleEqualsIgnoreCase(String userBook);

    @Query("SELECT b FROM Book b " +
            "WHERE b.language = :userLanguageEnum")
    List<Book> registeredBookByLanguage(Language userLanguageEnum);
}
