# kafka-streams final project
[![Build Status](https://travis-ci.com/AChepurnoi/scala-kafka-streams.svg?token=dFANEVvUn3HF3pZ9jc1Z&branch=master)](https://travis-ci.com/AChepurnoi/scala-kafka-streams)


This is final project of `Functional and streaming programming` at UCU. 
The goal of the project is develop three services: two producers and steaming application that will join two streams in one enriched data stream.

## Structure
* **Solar panel** - produces solar panel data information to the topic. 
Each solar panel has it's own location and registered in Zookeeper cluster
* **Weather service** - produces weather data from OpenWeatherAPI for each location registered in Zookeeper cluster
* **Enrich service** - joins the data from two streams into one stream of solar data + weather data
## Technologies
* Zookeeper
* Apache Curator (Zookeeper library)
* Apache Avro
* Akka
* Kafka streams

## Modules

* **data-avro** - avro schema and generated AvroSpecificRecords. Shared data classes between other modules
* **solar-plant** - solar plant implementation with Akka
* **weather-service** - weather service implementation with Akka
* **enrich-service** - streams join service implementation with Kafka Streams

## Notes

### Location registration
**Solar plant** register itself at Zookeeper as ephemeral node because we want to keep only list of active plants and their locations (plant has ID and lat/lon)

**Weather service** uses Apache Curator(wrapper library over ZK client) to keep the list of registered nodes up to date and in sync.  

### Rate limiting

Open weather API have limiting for each API key (1 req/sec). Now each node of Weather Service should know the number of weather services instances to adjust interval to stay under limit.


### Drawbacks

There are multiple things that could be implemented better:
* Separate thread pools properly (Akka, HTTP and async thread pools)
* Distributed rate limiting
* Yes, my API token in the code. Feel free to use it 🌝

## How to run

Currently kafka deployment supports creating ad-hoc topics. To disable set `KAFKA_AUTO_CREATE_TOPICS_ENABLE` to `false`. In this case you have create topics with required properties before running services
### Kafka 
* `export KAFKA_HOST=127.0.0.1` - export kafka host ip. should be replaced with instance ip where kafka is deployed (assuming kafka and zookeeper have the same ip)

* `docker-compose -f kafka-compose.yml up`

### Solar/Weather/Streaming services

* `export KAFKA_HOST=127.0.0.1`

* `docker-compose -f solar(weather/enrich)-compose.yml up`

## Building images

The latest images are already built and public.
* `sashachepurnoi/solar-plant:1`
* `sashachepurnoi/weather-service:1`
* `sashachepurnoi/streaming-app:1`

If you want to modify and rebuild, before building docker images each module should be assembled with sbt:
* `sbt project $PROJECT_NAME` (from sbt projects)
* `sbt assebmly`

After building jars, to build docker containers call:

* solar-plant - `cd solar-plant && docker build -t $IMAGE_REPO/solar-plant:1 .`

* weather-service - `cd weather-service && docker build -t $IMAGE_REPO/weather-service:1 .`

* streaming-app - `cd enrich-service && docker build -t $IMAGE_REPO/streaming-app:1 .`

