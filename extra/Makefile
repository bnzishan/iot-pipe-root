
install-deps:
	mvn validate

test-benchmark-nt:
	mvn -Dtest=BenchmarkTest#checkHealth test -DfailIfNoTests=false

test-benchmark:
	mvn -Dtest=BenchmarkTest#checkHealth test

tb:
	mvn -Dtest=BenchmarkTest#checkHealth test

package:
	mvn -DskipTests -DincludeDeps=true package


build-images:
	mvn -Dtest=BenchmarkTest#buildImages surefire:test

test-dockerized-benchmark:
	mvn -Dtest=BenchmarkTest#checkHealthDockerized test

tdb:
	mvn -Dtest=BenchmarkTest#checkHealthDockerized test

push-images:
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/benchmark-controller
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/datagen
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/taskgen
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/eval-storage
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/system-adapter
	sudo docker push git.project-hobbit.eu:bushran/sdk-example-benchmark-sensemark2/eval-module


d-zk:
	docker build -f ./docker/sensemark-zookeeper.docker -t sensemark/zookeeper .
d-k:
	docker build -f ./docker/sensemark-kafka.docker -t sensemark/kafka .
dockerize-sa:
	docker build -f ./docker/sensemark-systemadapter.docker -t sensemark/system-adapter .

d-nim:
	docker build -f ./docker/sensemark-storm-nimbus.docker -t sensemark/storm-nimbus .
d-sup:
	docker build -f ./docker/sensemark-storm-nimbus.docker -t sensemark/storm-supervisor .
d-tg:
	docker build -f ./docker/sensemark-taskgenerator.docker -t sensemark/taskgen .


dockerize-base:
	docker build -f ./docker/base/alpine-bash-jdk/Dockerfile -t bnz/base-alpine:v0.0 ./docker/base/alpine-bash-jdk

dockerize-zk-test:
	docker build -f ./system/zookeeper/cur/Dockerfile -t bnz-zookeeper1 ./system/zookeeper/cur
	docker build -f ./system/zookeeper/cur/Dockerfile -t bnz-zookeeper2 ./system/zookeeper/cur
	docker build -f ./system/zookeeper/cur/Dockerfile -t bnz-zookeeper3 ./system/zookeeper/cur

dockerize-zk-dynamic-reconf:
	docker build -f ./system/zookeeper/dynamic-reconf/Dockerfile -t bnz/zk1 ./system/zookeeper/dynamic-reconf
	docker build -f ./system/zookeeper/dynamic-reconf/Dockerfile -t bnz/zk2 ./system/zookeeper/dynamic-reconf
	docker build -f ./system/zookeeper/dynamic-reconf/Dockerfile -t bnz/zk3 ./system/zookeeper/dynamic-reconf
	docker build -f ./system/zookeeper/dynamic-reconf/Dockerfile -t bnz/zk4 ./system/zookeeper/dynamic-reconf
	docker build -f ./system/zookeeper/dynamic-reconf/Dockerfile -t bnz/zk5 ./system/zookeeper/dynamic-reconf

dockerize-zk-swarm:
	docker build -f ./systems/current/zookeeper/swarm/Dockerfile -t bnz/zk-swarm1 ./systems/zookeeper/swarm


dockerize-hadoop:
	docker build -f ./hadoop/base/Dockerfile-hadoop-base-bde -t bnz/hadoop/base ./hadoop/base
	docker build -f ./hadoop/namenode/Dockerfile-hadoop-namenode-bde -t bnz/bde2020/hadoop/namenode ./hadoop
	docker build -f ./hadoop/datanode/Dockerfile-hadoop-datanode-bde -t bnz/hadoop/datanode ./hadoop
	docker build -f ./hadoop/resourcemanager/Dockerfile-hadoop-resourcemanager-bde -t bnz/hadoop/resourcemanager ./hadoop
	docker build -f ./hadoop/nodemanager/Dockerfile-hadoop-nodemanager-bde -t bnz/bde2020/hadoop/nodemanager ./hadoop
	docker build -f ./hadoop/historyserver/Dockerfile-hadoop-historyserver-bde -t bnz/hadoop/historyserver ./hadoop

dockerize-hbase:
	docker build -f ./habse/base/Dockerfile-hbase-base-bde -t bnz1ss-hbase-base-bde/bde2020/hbase-base:1.0.0-hbase1.2.6 ./hbase
	docker build -f ./hbase/hmaster/Dockerfile-hbase-hmaster-bde -t bnz1ss-hbase-hmaster-bde/bde2020/hbase-master:1.0.0-hbase1.2.6 ./hbase
	docker build -f ./hbase/hregionserver/Dockerfile-hbase-hregionserver-bde -t bnz1ss-hbase-hregionserver-bde/bde2020/hbase-regionserver:1.0.0-hbase1.2.6 ./hbase

dockerize-storm:
	docker build -f ./storm/storm/Dockerfile-storm-base -t bnz1ss-storm-base/storm-base ./storm/storm
	docker build -f ./storm/storm/Dockerfile-storm-base-ext -t bnz1ss-storm-base-ext/storm-base ./storm/base
	docker build -f ./storm/storm-nimbus/Dockerfile-storm-nimbus -t bnz1ss-storm-nimbus/storm-nimbus ./storm/nimbus
	docker build -f ./storm/storm-supervisor/Dockerfile-storm-supervisor -t bnz1ss-storm-supervisor/storm-supervisor ./storm/supervisor
	docker build -f ./storm/storm-ui/Dockerfile-storm-ui -t bnz1ss-storm-ui/storm-ui ./storm/ui

test-d-zk: dockerize-zk
	docker run -it --rm bnz/zookeeper --restart=always


