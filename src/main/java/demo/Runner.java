package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import demo.spring.SpringExtension;
import demo.messages.FileAnalysisMessage;
import demo.messages.FileProcessedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
class Runner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private SpringExtension springExtension;

    @Override
    public void run(String[] args) throws Exception {
        // Create first actor based on the specified class using spring
        ActorRef coordinator = actorSystem.actorOf(springExtension.props("fileAnalysisActor"), "fileAnalysisActor");

        // Create a message including the file path
        FileAnalysisMessage msg = new FileAnalysisMessage("data/log.txt");

        // Send a message to start processing the file. This is a synchronous call using 'ask' with a timeout.
        Future<Object> future = Patterns.ask(coordinator, msg, new Timeout(5, TimeUnit.SECONDS));

        // Process the results
        final ExecutionContext ec = actorSystem.dispatcher();
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object message) throws Throwable {
                if (message instanceof FileProcessedMessage) {
                    printResults((FileProcessedMessage) message);

                    // Stop the actor system
                    logger.info("Akka actor system shutting down...");
                    actorSystem.shutdown();
                }
            }

            private void printResults(FileProcessedMessage message) {
                System.out.println("================================");
                System.out.println("||\tCount\t||\t\tIP");
                System.out.println("================================");

                Map<String, Long> result = new LinkedHashMap<>();

                // Sort by value and put it into the "result" map
                message.getData().entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

                // Print only if count > 50
                result.entrySet().stream().filter(entry -> entry.getValue() > 50).forEach(entry ->
                        System.out.println("||\t" + entry.getValue() + "   \t||\t" + entry.getKey())
                );
            }
        }, ec);
    }
}
