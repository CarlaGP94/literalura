package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAuthorRepository extends JpaRepository<Author,Long> {

    Optional<Author> findByCompleteName(String authorName);

    // A partir de aquí los comandos pertenecen a la 2da versión.
}
