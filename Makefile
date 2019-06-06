install-deps:
	mvn validate

package:
	mvn -DskipTests -DincludeDeps=true package

tdb:
	mvn -Dtest=BenchmarkTest#checkHealthDockerized -DfailIfNoTests=false test
