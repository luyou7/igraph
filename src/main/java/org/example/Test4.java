package org.example;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luyou.ly
 * @version 1.0
 * @description: TODO
 * @date 2021/11/7 1:00 上午
 */
public class Test4 {
    public static void main(String[] args) {

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
            // https://aws.amazon.com/cn/blogs/database/load-balance-graph-queries-using-the-amazon-neptune-gremlin-client/

            try {

                GraphTraversalSource g = EmptyGraph.instance()
                        .traversal()
                        .withRemote(DriverRemoteConnection.using(cluster));


                g.V().hasLabel("User").has("id", "33").drop().iterate();

                g.V().hasLabel("Cate").has("id", "1").drop().iterate();

                g.V().hasLabel("Item").has("id", "11").drop().iterate();
                g.V().hasLabel("Item").has("id", "22").drop().iterate();

                //U2C
                g.addV("Cate").property("id", "1").as("c")
                        .addV("User")
                        .property("id", "33")
                        .property("name", "zhangsan")
                        .addE("u2c").to("c").next();

                //C2I
                g.addV("Item")
                        .property("id", "11")
                        .property("title", "item01")
                        .property("price", 22).as("i")
                        .V().hasLabel("Cate").has("id", "1").addE("c2i").to("i").next();

                g.addV("Item")
                        .property("id", "22")
                        .property("title", "item02")
                        .property("price", 33).as("i")
                        .V().hasLabel("Cate").has("id", "1").addE("c2i").to("i").next();

                //user -> cate -> items
                System.err.println(g.V().hasLabel("User").has("id",
                        "33").out("u2c").has("id", "1").values("id").toList());

                //user -> cate -> items
                System.err.println(g.V().hasLabel("User").has("id",
                                "33").out("u2c").has("id", "1").out("c2i")
                        .values("id", "title", "price").toList());

                //item -> cate-> user
                System.err.println(g.V().hasLabel("Item").has("id", "11").in("c2i").has("id", "1").in("u2c")
                        .values("id", "name").toList());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cluster.close();
            }
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
