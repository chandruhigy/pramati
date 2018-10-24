java -Xmx3g -DbaseUrl="http://127.0.0.1:8983/solr/localhost" -DoutputDir="../output" -DinputFile="../input/hotels.json" -DcacheSize="100" -jar IngestJson.jar
pause