package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String completeName;
    private Integer birthYear;
    private Integer deathYear;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Book> booksList = new ArrayList<>();

    public List<Book> getBooks() {
        return booksList;
    }

    public void setBooks(List<Book> books) {
        books.forEach(b -> b.setAuthor(this)); // relaciona el autor al libro
        this.booksList = books;
    }

    public Author(){}

    public Author(AuthorData authorData) {
        this.completeName = authorData.completeName();
        this.birthYear = authorData.birthYear();
        this.deathYear = authorData.deathYear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    @Override
    public String toString() {
        return "Autor: " + (completeName == null ? "Desconocido" : completeName) +
                "\nNacido en: " + (birthYear  == null ? "-" : birthYear) +
                "\nDifunto en: " + (deathYear == null ? "-" : deathYear) +
                "\n";
    }
}
