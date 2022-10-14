import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class MovieAnalyzer {

  public static class Movie {

    String seriesTitle;
    int releasedYear;
    String certificate;
    int runtime;
    Set<String> genre;
    float IMDB_Rating;
    String overview;
    String MetaScore;
    String director;
    String[] stars;
    String noofvotes;
    Long gross;

    public Movie(String[] movie_data) {
      if (movie_data[1].startsWith("\"")) {
        int len = movie_data[1].length();
        this.seriesTitle = movie_data[1].substring(1, len - 1);
      } else {
        this.seriesTitle = movie_data[1];
      }
      this.releasedYear = Integer.parseInt(movie_data[2]);
      this.certificate = movie_data[3];
      String runtime = movie_data[4].replace(" min", "");
      this.runtime = Integer.parseInt(runtime);
      String[] genre1 = movie_data[5].replace("\"", "").split(", ");
      this.genre = new HashSet<>();
      this.genre.addAll(Arrays.asList(genre1));

      this.IMDB_Rating = Float.parseFloat(movie_data[6]);
      if (movie_data[7].startsWith("\"")) {
        int len = movie_data[7].length();
        this.overview = movie_data[7].substring(1, len - 1);
      } else {
        this.overview = movie_data[7];
      }

      this.MetaScore = movie_data[8];
      this.director = movie_data[9];
      String[] stars1 = Arrays.copyOfRange(movie_data, 10, 14);
      Arrays.sort(stars1);
      this.stars = stars1;
      this.noofvotes = movie_data[14];
      if (movie_data.length == 16) {
        String gross1 = movie_data[15];
        gross1 = gross1.replace("\"", "");
        gross1 = gross1.replace(",", "");
        this.gross = Long.parseLong(gross1);

      } else {
        this.gross = 0L;
      }

    }

    public String getSeriesTitle() {
      return seriesTitle;
    }

    public int getReleased_Year() {
      return releasedYear;
    }

    public String getCertificate() {
      return certificate;
    }

    public Set<String> getGenre() {
      return genre;
    }

    public float getIMDB_Rating() {
      return IMDB_Rating;
    }

    public String getDirector() {
      return director;
    }

    public Long getGross() {
      return gross;
    }

    public String getMeta_Score() {
      return MetaScore;
    }

    public String getOverView() {
      return overview;
    }

    public int getRuntime() {
      return runtime;
    }

    public String[] getStars() {
      return stars;
    }


    public String getNoofvotes() {
      return noofvotes;
    }


  }

  List<Movie> movies;


  public MovieAnalyzer(String dataset_path) throws IOException {
    movies = Files.lines(Paths.get(dataset_path), StandardCharsets.UTF_8).skip(1)
        .map(l -> l.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")).map(Movie::new)
        .collect(Collectors.toList());

  }

  //1
  public Map<Integer, Integer> getMovieCountByYear() {
    Map<Integer, Long> res = movies.stream()
        .collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.counting()));
    Map<Integer, Integer> res1 = new HashMap<>();
    for (Map.Entry<Integer, Long> entry : res.entrySet()) {
      res1.put(entry.getKey(), entry.getValue().intValue());
    }
    Map<Integer, Integer> res2 = new LinkedHashMap<>();
    res1.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
        .forEachOrdered(x -> res2.put(x.getKey(), x.getValue()));
    return res2;


  }

  //2
  public Map<String, Integer> getMovieCountByGenre() {
    Map<String, Integer> res = new HashMap<>();
    movies.forEach(m1 -> {
      for (String g : m1.genre) {
        if (res.containsKey(g)) {
          res.put(g, res.get(g) + 1);
        } else {
          res.put(g, 1);
        }
      }
    });
    Map<String, Integer> res1 = new LinkedHashMap<>();
    res.entrySet().stream().sorted(Map.Entry.comparingByKey())
        .forEachOrdered(x -> res1.put(x.getKey(), x.getValue()));
    Map<String, Integer> res2 = new LinkedHashMap<>();
    res1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEachOrdered(x -> res2.put(x.getKey(), x.getValue()));
    return res2;
  }

  //3
  public Map<List<String>, Integer> getCoStarCount() {
    Map<List<String>, Integer> res = new HashMap<>();
    movies.forEach(m1 -> {
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          List<String> l1 = new ArrayList<>();
          l1.add(m1.getStars()[i]);
          l1.add(m1.getStars()[j]);
          if (res.containsKey(l1)) {
            res.put(l1, res.get(l1) + 1);
          } else {
            res.put(l1, 1);
          }
        }
      }
    });
    Map<List<String>, Integer> res1 = new LinkedHashMap<>();
    res.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEachOrdered(x -> res1.put(x.getKey(), x.getValue()));
    return res1;
  }

  //4
  public List<String> getTopMovies(int top_k, String by) {
    List<String> res = new ArrayList<>();
    if (by.equals("runtime")) {
      Map<String, Integer> runTimeMap = new HashMap<>();
      movies.forEach(m1 -> runTimeMap.put(m1.seriesTitle + "$" + m1.releasedYear, m1.runtime));
      Map<String, Integer> runTimeMap1 = new LinkedHashMap<>();
      runTimeMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
          .forEachOrdered(x -> runTimeMap1.put(x.getKey(), x.getValue()));
      Map<String, Integer> runTimeMap2 = new LinkedHashMap<>();
      runTimeMap1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(x -> runTimeMap2.put(x.getKey(), x.getValue()));
      List<String> res1 = new ArrayList<>(runTimeMap2.keySet());
      for (String title : res1) {
        String title1 = title.substring(0, title.indexOf("$"));
        res.add(title1);
      }
      res = res.subList(0, top_k);
    } else {
      Map<String, Integer> overViewMap = new HashMap<>();
      movies.forEach(m1 -> overViewMap.put(m1.getSeriesTitle() + "$" + m1.releasedYear,
          m1.getOverView().length()));
      Map<String, Integer> overViewMap1 = new LinkedHashMap<>();
      overViewMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
          .forEachOrdered(x -> overViewMap1.put(x.getKey(), x.getValue()));
      Map<String, Integer> overViewMap2 = new LinkedHashMap<>();
      overViewMap1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(x -> overViewMap2.put(x.getKey(), x.getValue()));
      List<String> res1 = new ArrayList<>(overViewMap2.keySet());
      for (String title : res1) {
        String title1 = title.substring(0, title.indexOf("$"));
        res.add(title1);
      }
      res = res.subList(0, top_k);
    }
    return res;
  }

  //5
  public List<String> getTopStars(int top_k, String by) {
    List<String> res;
    if (by.equals("rating")) {
      Map<String, List<Double>> totalStarRate = new HashMap<>();
      movies.forEach(m1 -> {
        for (String star : m1.stars) {
          if (totalStarRate.containsKey(star)) {
            totalStarRate.get(star).add((double) m1.IMDB_Rating);
          } else {
            List<Double> rate = new ArrayList<>();
            rate.add((double) m1.IMDB_Rating);
            totalStarRate.put(star, rate);
          }
        }
      });
      Map<String, Double> avgStarRate = new HashMap<>();
      for (Map.Entry<String, List<Double>> entry : totalStarRate.entrySet()) {
        Double avgRate = entry.getValue().stream()
            .collect(Collectors.averagingDouble(Double::doubleValue));
        avgStarRate.put(entry.getKey(), avgRate);
      }
      Map<String, Double> avgStarRate1 = new LinkedHashMap<>();
      avgStarRate.entrySet().stream().sorted(Map.Entry.comparingByKey())
          .forEachOrdered(x -> avgStarRate1.put(x.getKey(), x.getValue()));
      Map<String, Double> avgStarRate2 = new LinkedHashMap<>();
      avgStarRate1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(x -> avgStarRate2.put(x.getKey(), x.getValue()));
      List<String> res1 = new ArrayList<>(avgStarRate2.keySet());
      res = res1.subList(0, top_k);
    } else {
      Map<String, List<Long>> totalStarGross = new HashMap<>();
      movies.forEach(m1 -> {

        for (String star : m1.stars) {
          if (m1.getGross() == 0L) {
            continue;
          }
          if (totalStarGross.containsKey(star)) {
            totalStarGross.get(star).add(m1.getGross());
          } else {
            List<Long> gross = new ArrayList<>();
            gross.add(m1.getGross());
            totalStarGross.put(star, gross);
          }
        }
      });
      Map<String, Double> avgStarGross = new HashMap<>();
      for (Map.Entry<String, List<Long>> entry : totalStarGross.entrySet()) {
        double avgGross = entry.getValue().stream()
            .collect(Collectors.averagingLong(Long::longValue));
        avgStarGross.put(entry.getKey(), avgGross);
      }
      Map<String, Double> avgStarGross1 = new LinkedHashMap<>();
      avgStarGross.entrySet().stream().sorted(Map.Entry.comparingByKey())
          .forEachOrdered(x -> avgStarGross1.put(x.getKey(), x.getValue()));
      Map<String, Double> avgStarGross2 = new LinkedHashMap<>();
      avgStarGross1.entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(x -> avgStarGross2.put(x.getKey(), x.getValue()));
      List<String> res1 = new ArrayList<>(avgStarGross2.keySet());
      res = res1.subList(0, top_k);
    }
    return res;
  }

  //6
  public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
    List<String> res = new ArrayList<>();
    movies.forEach(m1 -> {
      if (m1.getGenre().contains(genre) && m1.getIMDB_Rating() >= min_rating
          && m1.getRuntime() <= max_runtime) {
        res.add(m1.getSeriesTitle());
      }
    });

    return res.stream().sorted().toList();


  }


}
