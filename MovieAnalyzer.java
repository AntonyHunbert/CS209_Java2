import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.Collectors;






public class MovieAnalyzer {
    public static class Movie{
        String Series_Title;
        int Released_Year;
        String Certificate;
        int Runtime;
        Set<String> Genre;
        float IMDB_Rating;
        String OverView;
        String Meta_Score;
        String Director;
        String[] Stars;
        //    String Star2;
//    String Star3;
//    String Star4;
        String Noofvotes;
        Long Gross;
        public Movie(String[] movie_data){
            if(movie_data[1].startsWith("\"")){
                int len = movie_data[1].length();
                this.Series_Title = movie_data[1].substring(1,len - 1);
            }
            else{
                this.Series_Title = movie_data[1];
            }
            this.Released_Year = Integer.parseInt(movie_data[2]);
            this.Certificate = movie_data[3];
            String runtime = movie_data[4].replace(" min", "");
            this.Runtime = Integer.parseInt(runtime);
            String[] genre1 = movie_data[5].replace("\"", "").split(", ");
            this.Genre = new HashSet<>();
            this.Genre.addAll(Arrays.asList(genre1));

            this.IMDB_Rating = Float.parseFloat(movie_data[6]);
            if(movie_data[7].startsWith("\"")){
                int len = movie_data[7].length();
                this.OverView = movie_data[7].substring(1,len - 1);
            }
            else{
                this.OverView = movie_data[7];
            }

            this.Meta_Score = movie_data[8];
            this.Director = movie_data[9];
            String[] stars1 = Arrays.copyOfRange(movie_data,10,14);
            Arrays.sort(stars1);
            this.Stars = stars1;
            this.Noofvotes = movie_data[14];
            if(movie_data.length == 16){
                String gross1 = movie_data[15];
                gross1 = gross1.replace("\"", "");
                gross1 = gross1.replace(",", "");
                this.Gross = Long.parseLong(gross1);

            }
            else{
                this.Gross = 0L;
            }

        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public int getReleased_Year() {
            return Released_Year;
        }

        public String getCertificate() {
            return Certificate;
        }

        public Set<String> getGenre() {
            return Genre;
        }

        public float getIMDB_Rating() {
            return IMDB_Rating;
        }

        public String getDirector() {
            return Director;
        }

        public Long getGross() {
            return Gross;
        }

        public String getMeta_Score() {
            return Meta_Score;
        }

        public String getOverView() {
            return OverView;
        }

        public int getRuntime() {
            return Runtime;
        }

        public String[] getStars() {
            return Stars;
        }

//    public String getStar2() {
//        return Star2;
//    }

        public String getNoofvotes() {
            return Noofvotes;
        }

//    public String getStar3() {
//        return Star3;
//    }
//
//    public String getStar4() {
//        return Star4;
//    }

    }
    List<Movie> movies;


    public MovieAnalyzer (String dataset_path) throws IOException {
        movies =Files.lines(Paths.get(dataset_path), StandardCharsets.UTF_8).skip(1).map(l -> l.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")).map(Movie::new).collect(Collectors.toList());

    }
//1
    public Map<Integer, Integer> getMovieCountByYear(){
        Map<Integer, Long> res = movies.stream().collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.counting()));
        Map<Integer, Integer> res1 = new HashMap<>();
        for(Map.Entry<Integer,Long> entry: res.entrySet()){
            res1.put(entry.getKey(), entry.getValue().intValue());
        }
        Map<Integer, Integer> res2 = new LinkedHashMap<>();
        res1.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).forEachOrdered(x -> res2.put(x.getKey(), x.getValue()));
        return res2;


    }

//2
    public Map<String, Integer> getMovieCountByGenre(){
        Map<String, Integer> res = new HashMap<>();
        movies.forEach(m1 -> {
            for(String g: m1.Genre){
                if(res.containsKey(g)){
                    res.put(g,res.get(g) + 1);
                }else{
                    res.put(g,1);
                }
            }
        });
        Map<String, Integer> res1 = new LinkedHashMap<>();
        res.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> res1.put(x.getKey(), x.getValue()));
        Map<String, Integer> res2 = new LinkedHashMap<>();
        res1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> res2.put(x.getKey(), x.getValue()));
        return res2;
    }

//3
    public Map<List<String>, Integer> getCoStarCount(){
        Map<List<String>, Integer> res = new HashMap<>();
        movies.forEach(m1 -> {
            for(int i = 0;i < 4;i++){
                for(int j = i + 1; j < 4;j++){
                    List<String> l1 = new ArrayList<>();
                    l1.add(m1.getStars()[i]);
                    l1.add(m1.getStars()[j]);
                    if(res.containsKey(l1)){
                        res.put(l1,res.get(l1) + 1);
                    }else{
                        res.put(l1,1);
                    }
                }
            }
        });
        Map<List<String>, Integer> res1 = new LinkedHashMap<>();
        res.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> res1.put(x.getKey(), x.getValue()));
        return res1;
    }

//4
   public List<String> getTopMovies(int top_k, String by){
        List<String> res = new ArrayList<>();
        if(by.equals("runtime")){
            Map<String, Integer> runTimeMap = new HashMap<>();
            movies.forEach(m1 -> runTimeMap.put(m1.Series_Title + "$" + m1.Released_Year, m1.Runtime));
            Map<String, Integer> runTimeMap1 = new LinkedHashMap<>();
            runTimeMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> runTimeMap1.put(x.getKey(), x.getValue()));
            Map<String, Integer> runTimeMap2 = new LinkedHashMap<>();
            runTimeMap1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> runTimeMap2.put(x.getKey(), x.getValue()));
            List<String> res1 = new ArrayList<>(runTimeMap2.keySet());
            for(String title: res1){
                String title1 = title.substring(0, title.indexOf("$"));
                res.add(title1);
            }
            res = res.subList(0,top_k);
        }
        else{
            Map<String, Integer> overViewMap = new HashMap<>();
            movies.forEach(m1 -> overViewMap.put(m1.getSeries_Title() + "$" + m1.Released_Year, m1.getOverView().length()));
            Map<String, Integer> overViewMap1 = new LinkedHashMap<>();
            overViewMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> overViewMap1.put(x.getKey(), x.getValue()));
            Map<String, Integer> overViewMap2 = new LinkedHashMap<>();
            overViewMap1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> overViewMap2.put(x.getKey(), x.getValue()));
            List<String> res1 = new ArrayList<>(overViewMap2.keySet());
            for(String title: res1){
                String title1 = title.substring(0, title.indexOf("$"));
                res.add(title1);
            }
            res = res.subList(0,top_k);
        }
       return res;
   }

//5
   public List<String> getTopStars(int top_k, String by){
        List<String> res;
        if(by.equals("rating")){
            Map<String, List<Double>> totalStarRate = new HashMap<>();
            movies.forEach(m1 -> {
                for(String star: m1.Stars){
                    if(totalStarRate.containsKey(star)){
                        totalStarRate.get(star).add((double) m1.IMDB_Rating);
                    }else{
                        List<Double> rate = new ArrayList<>();
                        rate.add((double) m1.IMDB_Rating);
                        totalStarRate.put(star, rate);
                    }
                }
            });
            Map<String, Double> avgStarRate = new HashMap<>();
            for(Map.Entry<String,List<Double>> entry: totalStarRate.entrySet()){
                Double avgRate = entry.getValue().stream().collect(Collectors.averagingDouble(Double::doubleValue));
                avgStarRate.put(entry.getKey(), avgRate);
            }
            Map<String, Double> avgStarRate1 = new LinkedHashMap<>();
            avgStarRate.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> avgStarRate1.put(x.getKey(), x.getValue()));
            Map<String, Double> avgStarRate2 = new LinkedHashMap<>();
            avgStarRate1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> avgStarRate2.put(x.getKey(), x.getValue()));
            List<String> res1 = new ArrayList<>(avgStarRate2.keySet());
            res = res1.subList(0, top_k);
        }else{
            Map<String, List<Long>> totalStarGross = new HashMap<>();
            movies.forEach(m1 -> {

                for(String star: m1.Stars){
                    if(m1.getGross() == 0L){
                        continue;
                    }
                    if(totalStarGross.containsKey(star)){
                        totalStarGross.get(star).add(m1.getGross());
                    }else{
                        List<Long> gross = new ArrayList<>();
                        gross.add(m1.getGross());
                        totalStarGross.put(star, gross);
                    }
                }
            });
            Map<String, Double> avgStarGross = new HashMap<>();
            for(Map.Entry<String,List<Long>> entry: totalStarGross.entrySet()){
                double avgGross = entry.getValue().stream().collect(Collectors.averagingLong(Long::longValue));
                avgStarGross.put(entry.getKey(), avgGross);
            }
            Map<String, Double> avgStarGross1 = new LinkedHashMap<>();
            avgStarGross.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> avgStarGross1.put(x.getKey(), x.getValue()));
            Map<String, Double> avgStarGross2 = new LinkedHashMap<>();
            avgStarGross1.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> avgStarGross2.put(x.getKey(), x.getValue()));
//            for(Map.Entry<String, Double> entry: avgStarGross2.entrySet()){
//                System.out.println(entry.getKey() + " ------ " + entry.getValue());
//
//            }
//            System.out.println("------" + avgStarGross2.get("Toni Collette"));
            List<String> res1 = new ArrayList<>(avgStarGross2.keySet());
            res = res1.subList(0, top_k);
        }
        return res;
   }

//6
    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        List<String> res = new ArrayList<>();
        movies.forEach(m1 -> {
            if(m1.getGenre().contains(genre) && m1.getIMDB_Rating() >= min_rating && m1.getRuntime() <= max_runtime){
                res.add(m1.getSeries_Title());
            }
        });


        return res.stream().sorted().toList();


    }



//    public static void main(String[] args) throws IOException {
//        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("Assignment1/resources/imdb_top_500.csv");
////        System.out.println(getMovieCountByYear()); //1
////        movies.forEach(m1 -> {
////            System.out.println(m1.getOverView());
////
////        });
////        System.out.println(getMovieCountByGenre()); //2
////        System.out.println(getCoStarCount());
////        System.out.println(getCoStarCount().size()); //3
////        System.out.println(getTopMovies(20, "overview")); //4
////        System.out.println(getTopStars(40, "gross"));//5
////        System.out.println(searchMovies("Drama", 9.0f, 150));
//    }


}
