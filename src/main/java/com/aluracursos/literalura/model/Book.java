package com.aluracursos.literalura.model;

import jakarta.persistence.*;

@Entity // Relación con la base de dato: creación de tabla
@Table(name = "books")
public class Book {
    @Id // cada libro tendrá su ID único y será el parámetro para la estrategia de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, columnDefinition = "TEXT") // Evita nombres repetidos + Acepta longitudes largas de caracteres -> SQL: ALTER TABLE books ALTER COLUMN title TYPE TEXT;
    private String title;
    @ManyToOne // muchos libros = un autor
    private Author author;
    @Enumerated(EnumType.STRING) // atributo del tipo Enum
    private Language language;
    private Double downloadCount;

    public Book (){} //constructor genérico

    public Book(BookData bookData) {
        this.title = bookData.title();
        this.language = Language.fromString(bookData.languagesList().get(0).trim());
        this.downloadCount = bookData.downloadCount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Double downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Título: " + title +
                "\nAutor: " + author.getCompleteName() +
                "\nIdioma: " + language.getOwnLanguage() +
                "\nTotal de descargas: " + downloadCount +
                "\n";
    }
}
