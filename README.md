# kafka-streams final project
This is final project of `Functional and streaming programmin` at UCU. 
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
* TBD
