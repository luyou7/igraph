package org.example;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luyou.ly
 * @version 1.0
 * @description: TODO
 * @date 2021/11/7 1:00 上午
 */
public class Test3 {
    public static void main(String[] args) {

        /*final Cluster cluster = Cluster.build("localhost")
                .port(8182)
                .maxInProcessPerConnection(32)
                .maxSimultaneousUsagePerConnection(32)
                .serializer(Serializers.GRAPHBINARY_V1D0)
                .create();

        try {
            final Client client = cluster.connect();
            List<Result> results = client.submit("g.V().has('name','pumba').out('friendOf').id()").all().get();
            System.out.println(verticesWithNamePumba);
        } finally {
            cluster.close();
        }*/

       /* final Cluster cluster = Cluster.build("localhost")
                .port(8182)
                .maxInProcessPerConnection(32)
                .maxSimultaneousUsagePerConnection(32)
                .serializer(Serializers.GRAPHBINARY_V1D0)
                .create();

        try {
            final GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using(cluster));
            List<Object> verticesWithNamePumba = g.V().has("name", "pumba").out("friendOf").id().toList();
            System.out.println(verticesWithNamePumba);
        } finally {
            cluster.close();
        }*/

        // Create a GraphTraversalSource for the remote connection
       /* final GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using(cluster));
// Add 5 vertices in a single query
        g.addV("Person").property(T.id, "P1")
                .addV("Person").property(T.id, "P2")
                .addV("Person").property(T.id, "P3")
                .addV("Person").property(T.id, "P4")
                .addV("Person").property(T.id, "P5").iterate();*/

        Cluster.Builder builder = Cluster.build()
                .addContactPoint("test-buyer-neptune-cluster.cluster-cx3z9ane9h8d.us-west-2.neptune.amazonaws.com")
                .port(8182)
                .enableSsl(true)
                .minConnectionPoolSize(1)
                .maxConnectionPoolSize(1)
                .serializer(Serializers.GRAPHBINARY_V1D0)
                .reconnectInterval(2000);


        Cluster cluster = builder.create();


       try {

           GraphTraversalSource g = EmptyGraph.instance()
                   .traversal()
                   .withRemote(DriverRemoteConnection.using(cluster));
           //Users/luyou/tool_work/test-data/ml-1m/movies.dat 电影id、片名、属性
           FileReader input = new  FileReader("/Users/luyou/tool_work/test-data/ml-1m/movies.dat");
           BufferedReader br = new BufferedReader(input);
           List<Movie> movies = new ArrayList<>();
           br.lines().forEach(line -> {
              // System.out.println(line)xx;
               String[] values =  line.split("::");
               Movie movie = new Movie(values[0], values[1], (values[2]).split("\\|"));
               movies.add(movie);
           });
           int count = 0;
           //电影实体  电影类型实体 建立电影和电影类型的关系
           for (Movie movie : movies) {
               count++;
               g.addV("Movie")
                       .property("movieId",movie.movieId)
                       .property("movieName", movie.movieName).next();
               if (null != movie.attrs) {
                   for (String attr : movie.attrs) {
                       g.addV("Genera").property("genera", attr).next();
                       g.V().hasLabel("Movie").has("movieId", movie.movieId).as("m")
                               .V().hasLabel("Genera").has("genera", attr).addE("hasGenera").to("m").next();
                   }
               }
               if (count >= 5) {
                   break;
               }
           }
           count = 0;



           //文件中包含观众记录，每行有5列，分别是：观众id、性别、年龄、职业、邮编
           input = new  FileReader("/Users/luyou/tool_work/test-data/ml-1m/users.dat");
           br = new BufferedReader(input);
           List<User> users = new ArrayList<>();
           br.lines().forEach(line -> {
               String[] values =  line.split("::");
               User user = new User(values[0],values[1],Integer.valueOf(values[2]),values[3]);
               users.add(user);
           });

           //观众实体 职业实体 建立观众与职业的关系
           for (User user : users) {
               count++;
               g.addV("User")
                       .property("userId", user.userId)
                       .property("gender", user.gender)
                       .property("age", user.age).next();
               g.addV("Occupation").property("occupation", user.occupation).next();
               g.V().hasLabel("User").has("userId", user.userId).as("u")
                       .V().hasLabel("Occupation").has("occupation", user.occupation)
                       .addE("hasOccupation").to("u").next();
               if (count >= 5) {
                   break;
               }
           }

           count = 0;


           //ratings.dat 电影的评分记录，每行有4列，分别是：观众id, 电影id, 电影评星 (1-5 星), 时间戳(这里不需要)
           input = new  FileReader("/Users/luyou/tool_work/test-data/ml-1m/ratings.dat");
           br = new BufferedReader(input);
           List<Rating> ratings = new ArrayList<>();
           br.lines().forEach(line -> {
               String[] values =  line.split("::");
               Rating rating = new Rating(values[0],values[1],Integer.valueOf(values[2]));
               ratings.add(rating);
           });

           //设置用户与电影的评价关系 关系属性有评分
           for (Rating rating : ratings) {
               count++;
               g.V().hasLabel("User").has("userId", rating.userId).as("u")
                     .V().hasLabel("Movie").has("movieId", rating.movieId)
                     .addE("rated").property("stars", rating.stars).next();
               if (count >= 5) {
                   break;
               }
           }



           System.err.println( g.V().hasLabel("User").has("userId", "1").out().next());
           System.err.println(g.E().count().next().intValue());
           System.err.println(g.V("Movie").count().next().intValue());
           System.err.println(g.V("Genera").count().next().intValue());
           System.err.println(g.V("User").count().next().intValue());
           System.err.println(g.V("Occupation").count().next().intValue());

           //观众职业分布情况
           GraphTraversal<Vertex, Vertex> s = g.V("User").out("hasOccupation").groupCount("occupation");
           System.err.println(g.V("User").out("hasOccupation").groupCount("occupation").iterate());
          // System.err.println(s.order().by().next().property("occupation"));
          // System.err.println(g.V("User").valueMap("age").mean().next().intValue());
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           cluster.close();
       }
    }

   public static class Movie {
        String movieId;
        String movieName;
        List<String> attrs = new ArrayList<>();

        public Movie(String movieId, String movieName, String[] attr) {
            this.movieId = movieId;
            this.movieName = movieName;
            attrs.addAll(attrs);
        }

        public String getMovieId() {
            return movieId;
        }

        public void setMovieId(String movieId) {
            this.movieId = movieId;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public List<String> getAttrs() {
            return attrs;
        }

        public void setAttrs(List<String> attrs) {
            this.attrs = attrs;
        }
    }

    public static class User {
        public String userId;
        public String gender;
        public Integer age;
        public String occupation;

        public User(String userId, String gender, Integer age, String occupation) {
            this.userId = userId;
            this.gender = gender;
            this.age = age;
            this.occupation = occupation;
        }
    }

    public static class Rating {
        public String userId;
        public String movieId;
        public Integer stars;

        public Rating(String userId, String movieId, Integer stars) {
            this.userId = userId;
            this.movieId = movieId;
            this.stars = stars;
        }
    }
}
