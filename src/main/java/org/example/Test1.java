package org.example;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.SigV4WebSocketChannelizer;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.apache.tinkerpop.gremlin.process.remote.RemoteConnectionException;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.io.StringWriter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

/**
 * @author luyou.ly
 * @version 1.0
 * @description: TODO
 * @date 2021/11/6 9:15 下午
 */
public class Test1 {
    public static void main(String[] args) {
        boolean useIam = false;

        // Create Gremlin cluster and traversal source
        Cluster.Builder builder = Cluster.build()
                .addContactPoint("test-buyer-neptune-cluster.cluster-cx3z9ane9h8d.us-west-2.neptune.amazonaws.com")
                .port(8182)
                .enableSsl(true)
                .minConnectionPoolSize(1)
                .maxConnectionPoolSize(1)
                .serializer(Serializers.GRAPHBINARY_V1D0)
                .reconnectInterval(2000);

        if (useIam) {
            builder = builder.channelizer(SigV4WebSocketChannelizer.class);
        }

        Cluster cluster = builder.create();

        GraphTraversalSource g = AnonymousTraversalSource
                .traversal()
                .withRemote(DriverRemoteConnection.using(cluster));

        // Configure retries
        RetryConfig retryConfig = new RetryConfigBuilder()
                .retryOnCustomExceptionLogic(getRetryLogic())
                .withDelayBetweenTries(1000, ChronoUnit.MILLIS)
                .withMaxNumberOfTries(5)
                .withFixedBackoff()
                .build();

//        @SuppressWarnings("unchecked")
        CallExecutor<Object> retryExecutor = new CallExecutorBuilder<Object>()
                .config(retryConfig)
                .build();

        g.addV("Person").property("001", "v1");

        g.V("v1").property("name", "John");

        g.addE("knows").from(V("v1")).to(V("v2")).property("001", "e1");

        g.V("v1").property("set", "phone", "956-424-2563").property("set", "phone", "956-354-3692 (tel:9563543692)");


        // Do lots of queries
        for (int i = 0; i < 0; i++){
            String id = String.valueOf(i);

            @SuppressWarnings("unchecked")
            Callable<Object> query = () -> g.V(id)
                    .fold()
                    .coalesce(
                            unfold(),
                            addV("Person").property("name", id))
                    .id().next();

            g.addV("Person").property(id, "v1");

            g.V("v1").property("name", "John");

            g.addE("knows").from(V("v1")).to(V("v2")).property(id, "e1");


            // Retry query
            // If there are connection failures, the Java Gremlin client will automatically
            // attempt to reconnect in the background, so all we have to do is wait and retry.
            Status<Object> status = retryExecutor.execute(query);

           System.out.println(status.getResult().toString());
        }

        cluster.close();
    }

    private static Function<Exception, Boolean> getRetryLogic() {

        return e -> {

            Class<? extends Exception> exceptionClass = e.getClass();

            StringWriter stringWriter = new StringWriter();
            String message = stringWriter.toString();


            if (RemoteConnectionException.class.isAssignableFrom(exceptionClass)){
                System.out.println("Retrying because RemoteConnectionException");
                return true;
            }

            // Check for connection issues
            if (message.contains("Timed out while waiting for an available host") ||
                    message.contains("Timed-out waiting for connection on Host") ||
                    message.contains("Connection to server is no longer active") ||
                    message.contains("Connection reset by peer") ||
                    message.contains("SSLEngine closed already") ||
                    message.contains("Pool is shutdown") ||
                    message.contains("ExtendedClosedChannelException") ||
                    message.contains("Broken pipe"))
            {
                System.out.println("Retrying because connection issue");
                return true;
            };

            // Concurrent writes can sometimes trigger a ConcurrentModificationException.
            // In these circumstances you may want to backoff and retry.
            if (message.contains("ConcurrentModificationException")) {
                System.out.println("Retrying because ConcurrentModificationException");
                return true;
            }

            // If the primary fails over to a new instance, existing connections to the old primary will
            // throw a ReadOnlyViolationException. You may want to back and retry.
            if (message.contains("ReadOnlyViolationException")) {
                System.out.println("Retrying because ReadOnlyViolationException");
                return true;
            }

            System.out.println("Not a retriable error");
            return false;
        };
    }
}
