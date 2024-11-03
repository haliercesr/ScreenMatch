package com.aluracursos.ScreenMatch.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosSerie(
        @JsonAlias("Title") String titulo,
        @JsonAlias("totalSeasons") String totalDeTemporadas,
        @JsonAlias("imdbRating") String evaluacion
        @Json
) {
}
