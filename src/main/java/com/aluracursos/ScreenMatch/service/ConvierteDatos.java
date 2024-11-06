package com.aluracursos.ScreenMatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos{
         private ObjectMapper objectMapper = new ObjectMapper(); // objectmapper nos permite convertir de un formato json a una clase de java, es un traductor

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json,clase); //este metodo readvalue nos puede devolver una excepcion entoces java nos obliga a implementar una manera de manejarlo, ejemplo un try catch.
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
