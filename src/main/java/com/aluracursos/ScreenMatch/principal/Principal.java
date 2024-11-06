package com.aluracursos.ScreenMatch.principal;

import com.aluracursos.ScreenMatch.models.DatosEpisodio;
import com.aluracursos.ScreenMatch.models.DatosSerie;
import com.aluracursos.ScreenMatch.models.DatosTemporada;
import com.aluracursos.ScreenMatch.models.Episodio;
import com.aluracursos.ScreenMatch.service.ConsumoAPI;
import com.aluracursos.ScreenMatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=4fc7c187";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    public void muestraElMenu(){
        System.out.println("Escribe el nombre de la série que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL + nombreSerie.replace(" ", "+") + APIKEY);
        //https://www.omdbapi.com/?t=game+of+thrones&apikey=4fc7c187
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        List<DatosTemporada> temporadas = new ArrayList<>();// es una buena practica la programacion orientada a interfaces

        for (int i = 1; i <= datos.totalTemporadas(); i++) {
            json = consumoAPI.obtenerDatos(URL + nombreSerie.replace(" ", "+") + "&Season=" + i + APIKEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }
        temporadas.forEach(System.out::println);

        for (int i = 0; i < datos.totalTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporadas.size(); j++) {
                System.out.println(episodiosTemporadas.get(j).titulo());
            }
        }
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<String> nombres = Arrays.asList("Genesys","Eric","Maria","Brenda");

        nombres.stream()
                .sorted()
                .limit(2)
                .filter(n -> n.startsWith("E"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);

        List<DatosEpisodio> datosEpisodios = temporadas.stream()//Crea un flujo (stream) a partir de la lista temporadas
                .flatMap(t -> t.episodios().stream()) //Aplica la operación flatMap al flujo. Esta operación toma cada elemento del flujo (una temporada) y lo transforma en un nuevo flujo de episodios. Luego, combina todos los flujos de episodios en un solo flujo.
                .collect(Collectors.toList()); //Recopila todos los elementos del flujo en una nueva lista. La lista es mitable, si usamos solo toList() la lista seria inmutable
        System.out.println("\n Top 5 episodios");

        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())//Esta línea de código ordena la lista de DatosEpisodio en orden descendente según la evaluación de cada episodio.
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("a partir de que año deseas ver los episodios?");
        var fecha = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episodio: " + e.getTitulo() +
                                " Fecha de Lanzamiento: " + e.getFechaDeLanzamiento().format(formatter)
                ));

        //Busqueda de episodios por un pedazo de titulo
        System.out.println("Porfavor escriba el titulo de episodio que desea ver");
        var pedazoDeTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
               .filter(e -> e.getTitulo().toUpperCase().contains(pedazoDeTitulo.toUpperCase())) //Contains busca una parte de string en otra string
               .findFirst();
        if(episodioBuscado.isPresent()){
            System.out.println(" Episodio Encontrado");
            System.out.println(" Los datos son:"+episodioBuscado.get());
        } else {
            System.out.println(" Episodio No encontrado");
        }

        // Muestra evaluaciones de todas las temporadas
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);

        //Calcular estadísticas de las evaluaciones de los episodios
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media " + est.getAverage());
        System.out.println("Mejor episódio: " + est.getMax());
        System.out.println("Peor episódio: " + est.getMin());
        System.out.println("Cantidad " + est.getCount());
    }
}
