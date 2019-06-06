
$$$ git clone --depth=50 --branch=master https://github.com/hobbit-project/java-sdk-example.git hobbit-project/java-sdk-example
Cloning into 'hobbit-project/java-sdk-example'...

$$$ make install-deps
mvn validate

$$$ mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
Apache Maven 3.5.2 (138edd61fd100ec658bfa2d307c43b76940a5d7d; 2017-10-18T07:58:13Z)
Maven home: /usr/local/maven-3.5.2
Java version: 1.8.0_151, vendor: Oracle Corporation

$$$ make test-benchmark
mvn -Dtest=BenchmarkTest#checkHealth test


## BM 




1) Benchmark Controller (BC) receives parameters from benchmark.ttl, and initializes the components accordingly.
 
 BC creates the pipeline to be benchmarked. 