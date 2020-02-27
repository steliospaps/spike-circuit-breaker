# spike-circuit-breaker
# run

```
./mvnw clean package
java -jar target *.jar
```
access endpoint:

```
curl http://localhost:8080/hello
```


# docker-compose

see https://github.com/gysel/spring-boot-metrics-influxdb/

```
cd docker-compose
docker-compose up

```

grafana at localhost:3000 login/pass is admin/admin
