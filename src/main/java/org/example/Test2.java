package org.example;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.RequestOptions;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

/**
 * @author luyou.ly
 * @version 1.0
 * @description: TODO
 * @date 2021/11/7 1:00 上午
 */
public class Test2 {
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




        GraphTraversalSource g = EmptyGraph.instance()
                .traversal()
                .withRemote(DriverRemoteConnection.using(cluster));

       /* traversal.addV("person-test").property("id", "00003"); //顶点id为00003的 ‘person-test’ 对象

        //给顶点 00004 到 顶点 00002增加一条‘出边’名称为 created 的边，且定义边id = 00004-00002
        traversal.V("00004").addE("created").to(traversal.V("00002")).property("id", "00004-00002");

        GraphTraversal gt = traversal.V().hasLabel("person-test");
        System.out.println(gt.value());*/
        /*try {
            List<Object> verticesWithNamePumba = traversal.with("ARGS_EVAL_TIMEOUT", 500L).V().has("Person", "pumba").out("friendOf").id().toList();
            System.out.println(verticesWithNamePumba);
        } finally {
            cluster.close();
        }

        GraphTraversal xx= traversal.V().has("customerAge", P.gt(5));


        System.out.println(xx.value() + "****");*/

        /*List<Object> ss = traversal.V().hasLabel("Team").id().toList();
        System.err.println(ss);*/

        /*g.addV("User").property("name","Bill").property("birthdate", "1988-03-22").
                addV("User").property("name","Sarah").property("birthdate", "1992-05-03").
                addV("User").property("name","Ben").property("birthdate", "1989-10-21").
                addV("User").property("name","Lucy").property("birthdate", "1998-01-17").
                addV("User").property("name","Colin").property("birthdate", "2001-08-14").
                addV("User").property("name","Emily").property("birthdate", "1998-03-05").
                addV("User").property("name","Gordon").property("birthdate", "2002-12-04").
                addV("User").property("name","Kate").property("birthdate", "1995-02-12").
                addV("User").property("name","Peter").property("birthdate", "2001-02-27").
                addV("User").property("name","Terry").property("birthdate", "1989-10-02").
                addV("User").property("name","Alistair").property("birthdate", "1992-06-30").
                addV("User").property("name","Eve").property("birthdate", "2000-05-13").
                addV("User").property("name","Gary").property("birthdate", "1998-09-20").
                addV("User").property("name","Mary").property("birthdate", "1997-01-27").
                addV("User").property("name","Charlie").property("birthdate", "1989-11-02").
                addV("User").property("name","Sue").property("birthdate", "1994-03-08").
                addV("User").property("name","Arnold").property("birthdate", "2002-07-23").
                addV("User").property("name","Chloe").property("birthdate", "1988-11-04").
                addV("User").property("name","Henry").property("birthdate", "1996-03-15").
                addV("User").property("name","Josie").property("birthdate", "2003-08-21").
                V().hasLabel("User").has("name","Sarah").as("a").V().hasLabel("User").has("name","Bill").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Colin").as("a").V().hasLabel("User").has("name","Bill").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Terry").as("a").V().hasLabel("User").has("name","Bill").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Peter").as("a").V().hasLabel("User").has("name","Colin").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Kate").as("a").V().hasLabel("User").has("name","Ben").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Kate").as("a").V().hasLabel("User").has("name","Lucy").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Eve").as("a").V().hasLabel("User").has("name","Lucy").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Alistair").as("a").V().hasLabel("User").has("name","Kate").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Gary").as("a").V().hasLabel("User").has("name","Colin").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Gordon").as("a").V().hasLabel("User").has("name","Emily").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Alistair").as("a").V().hasLabel("User").has("name","Emily").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Terry").as("a").V().hasLabel("User").has("name","Gordon").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Alistair").as("a").V().hasLabel("User").has("name","Terry").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Gary").as("a").V().hasLabel("User").has("name","Terry").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Mary").as("a").V().hasLabel("User").has("name","Terry").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Henry").as("a").V().hasLabel("User").has("name","Alistair").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Sue").as("a").V().hasLabel("User").has("name","Eve").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Sue").as("a").V().hasLabel("User").has("name","Charlie").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Josie").as("a").V().hasLabel("User").has("name","Charlie").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Henry").as("a").V().hasLabel("User").has("name","Charlie").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Henry").as("a").V().hasLabel("User").has("name","Mary").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Mary").as("a").V().hasLabel("User").has("name","Gary").addE("FRIEND").to("a").property("strength",1).
                V().hasLabel("User").has("name","Henry").as("a").V().hasLabel("User").has("name","Gary").addE("FRIEND").to("a").property("strength",2).
                V().hasLabel("User").has("name","Chloe").as("a").V().hasLabel("User").has("name","Gary").addE("FRIEND").to("a").property("strength",3).
                V().hasLabel("User").has("name","Henry").as("a").V().hasLabel("User").has("name","Arnold").addE("FRIEND").to("a").property("strength",1).
                next();*/


        System.err.println(g.V().hasLabel("User").has("name", "Terry").as("user").
                both("FRIEND").aggregate("friends").
                both("FRIEND").
                where(P.neq("user")).where(P.without("friends")).
                groupCount().by("name").
                order(Scope.local).by("name", Order.decr).out("name").valueMap());

        System.out.println(g.V().has("name", "marko").out("knows").valueMap());

      /*  g.addV("item").property("id","1").property("price", "1122").property("title","the item is 1");
        g.addV("item").property("id","2").property("price", "33").property("title","the item is 2");
        g.addV("item").property("id","3").property("price", "33").property("title","the item is 3");
        g.addV("item").property("id","4").property("price", "4").property("title","the item is 4");
        g.addV("item").property("id","5").property("price", "4").property("title","the item is 5");

        g.V().hasLabel("item").has("id","1").as("a").
                V().hasLabel("item").has("id","2").
                addE("i2i").to("a").property("strength",1);

        g.V().hasLabel("item").has("id","2").as("a").
                V().hasLabel("item").has("id","3").
                addE("i2i").to("a").property("strength",1);
       GraphTraversal<Vertex, Object> s = g.V().hasLabel("item").has("id","1").out("i2i").values("title").order().by().limit(4);
       System.err.println(g.V().hasLabel("item").out());
       *//* Map<Object, Long> vertex = g.V().hasLabel("item").has("id","1").as("i")
                .both("i2i")
                .aggregate("i2is")
                .both("i2i")
                .where(P.neq("i")).where(P.without("i2is"))
                .groupCount().by("id")
                .order(Scope.local).by("price",Order.desc).next();*//*
        Map<Object, Long> vertex = g.V().hasLabel("item").has("id","1").as("i")
                .both("i2i")
                .aggregate("i2is")
                .both("i2i")
                .where(P.neq("i")).where(P.without("i2is"))
                .groupCount().by("id")
                .order(Scope.local).by("price",Order.desc).next();
        System.err.println(vertex);*/



        cluster.close();

    }
}
