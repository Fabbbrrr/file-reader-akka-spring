# file-reader-akka-spring

This is a very small akka application to demonstrate how to create an akka system, actors, cross actor communication
and message processing. It also includes one synchronous call (ask) and a few asynchronous calls (tell).

The objective of the application is to analyse a log file from a webserver and extract how many times an IP address
has been logged. Everything is done through concurrent actors.

This example also includes a spring integration:

Akka ActorSystem can be integrated with Spring ApplicationContext following three steps.

- Use a SpringActorProducer to create actors by getting them by name (as Spring beans) from the
ApplicationContext.

- A new Akka Extension is used to add additional functionality to the ActorSystem.
 The SpringExtension uses Akka Props to create actors with the SpringActorProducer.

- Spring @Configuration is used to provide the ActorSystem as a Spring bean.
 The AppConfiguration creates the ActorSystem from the Akka configuration and registers the SpringExtension with it.

The SampleService is a simple service that is injected in the FileAnalysisActor by Spring.
