package com.aluracursos.ScreenMatch.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true) //ignora todos los demas parametros que llegan desde la API, si no usamos jasonignore o las demas propiedades el programa dara error
public record DatosSerie(
        @JsonAlias("Title") String titulo,    // jsonAlias busca el parametro "title" y lo guarda como "titulo"
        @JsonAlias("totalSeasons") Integer totalTemporadas,
        @JsonAlias("imdbRating") String evaluacion
        //@jsonAlias sirve solo para leer y @JsonProperty sirve tanto para leer y para cambiar parametros
) {
}
