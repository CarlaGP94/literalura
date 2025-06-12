package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorData(@JsonAlias("name") String completeName,
                         @JsonAlias("birth_year") Integer birthYear,
                         @JsonAlias("death_year") Integer deathYear) {
}
