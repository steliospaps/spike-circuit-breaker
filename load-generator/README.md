# run
```
../mvnw clean package
java -jar target/demo-circuit-breaker-load-generator-0.0.1-SNAPSHOT.jar \
  --load-generator.iterrations=1000 \
  --load-generator.delayMs=100 \
  --load-generator.connectionCount=100\
  --load-generator.rampUpDelayMs=10
```
