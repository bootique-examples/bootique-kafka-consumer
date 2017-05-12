[![Build Status](https://travis-ci.org/bootique-examples/bootique-kafka-consumer.svg)](https://travis-ci.org/bootique-examples/bootique-kafka-consumer)

# bootique-kafka-consumer

An example of the consumer to read streams of data from topics in the [Kafka](https://kafka.apache.org) cluster integrated for Bootique.

*For additional help/questions about this example send a message to
[Bootique forum](https://groups.google.com/forum/#!forum/bootique-user).*

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

    java -jar target/bootique-kafka-consumer-0.0.1-SNAPSHOT.jar

	Option                                              Description
    ------                                              -----------
    -b host:port, --bootstrap-server=host:port          Single Kafka bootstrap server. Can be specified multiple times.
                                                        Optional. If omitted, will be read from YAML or environment variable
                                                        BQ_KAFKACLIENT_BOOTSTRAPSERVERS_DEFAULT.

    --config=yaml_location                              Specifies YAML config location, which can be a file path or a URL.

    --consumer

    -h, --help                                          Prints this message.

    -H, --help-config                                   Prints information about application modules and their configuration
                                                            options.

    -r, --rewind                                        Whether to rewind offsets for each consumed partition to the
                                                            beginning of the queue.

    -t topic_name, --topic=topic_name                   Kafka topic to consume. Can be specified multiple times.

    -v, --verbose                                       If enabled, Kafka client will print extra debugging information to
                                                            STDOUT.
    
    
To test this example, you will need a Kafka broker running release 0.10.0.0 and a topic with some string data to consume. 
Run Zookeeper and Kafka broker both on localhost from Kafka root directory:

    bin/zookeeper-server-start.sh config/zookeeper.properties
    
    bin/kafka-server-start.sh config/server.properties

Run the consumer:

    java -jar target/bootique-kafka-consumer-0.0.1-SNAPSHOT.jar --bootstrap-server=localhost:9092 --consumer --topic=topic --verbose

Run kafka-verifiable-producer.sh script to write a bunch of string data to a topic from Kafka root directory:
    
    bin/kafka-verifiable-producer.sh --topic topic --max-messages 200000 --broker-list localhost:9092