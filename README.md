[![verify](https://github.com/bootique-examples/bootique-kafka-consumer/actions/workflows/verify.yml/badge.svg)](https://github.com/bootique-examples/bootique-kafka-consumer/actions/workflows/verify.yml)

# bootique-kafka-consumer

An example of the consumer to read streams of data from topics in the [Kafka](https://kafka.apache.org) cluster integrated for Bootique.

*For additional help/questions about this example send a message to
[Bootique forum](https://groups.google.com/forum/#!forum/bootique-user).*

You can find different versions of framework in use at
* [1.x](https://github.com/bootique-examples/bootique-kafka-consumer/tree/1.x)
* [2.x](https://github.com/bootique-examples/bootique-kafka-consumer/tree/2.x)

## Prerequisites

* Java 1.8 or newer.
* Apache Maven.

## Build the Demo

Here is how to build it:

	git clone git@github.com:bootique-examples/bootique-kafka-consumer.git
	cd bootique-kafka-consumer
	mvn package

## Run the Demo

Now you can check the options available in your app:

    NAME
          bootique-kafka-consumer-0.0.1-SNAPSHOT.jar
    
    OPTIONS
          -b host:port, --bootstrap=host:port
               Single Kafka bootstrap server.
    
          -c yaml_location, --config=yaml_location
               Specifies YAML config location, which can be a file path or a URL.
    
          -h, --help
               Prints this message.
    
          -H, --help-config
               Prints information about application modules and their configuration options.
    
          -t topic_name, --topic=topic_name
               Kafka topic to consume. Can be specified multiple times.
    
          -v, --verbose
               If enabled, Kafka client will print extra debugging information to STDOUT.
        
    
To test this example, you will need a Kafka broker running release 0.10.0.0 and a topic with some string data to consume. 
Run Zookeeper and Kafka broker both on localhost from Kafka root directory:

    bin/zookeeper-server-start.sh config/zookeeper.properties
    
    bin/kafka-server-start.sh config/server.properties

Run the consumer:

    java -jar target/bootique-kafka-consumer-0.0.1-SNAPSHOT.jar --bootstrap=localhost:9092 --topic=topic --verbose

Run kafka-verifiable-producer.sh script to write a bunch of string data to a topic from Kafka root directory:
    
    bin/kafka-verifiable-producer.sh --topic topic --max-messages 200000 --broker-list localhost:9092