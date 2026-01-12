# sm-lefort
Social Media - Light version by Trofel (Lefort)


# kafka
## lance docker-compose dans la racine du projet avec
docker compose up -d


## creation topic
docker exec -it kafka kafka-topics --create --topic user.created --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics --create --topic chat.created --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics --create --topic grammar.correction.response --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics --create --topic grammar.correction.request --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1


### creation auto des topic 
spring.kafka.admin.auto-create=true


### liste des topics
docker exec -it kafka /bin/bash
kafka-topics --bootstrap-server localhost:9092 --list


#### histo des user creer 
kafka-console-consumer --bootstrap-server localhost:9092 --topic user.created --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic chat.created --from-beginning


# KEYCLOCK 
docker run -d --name keycloak -p 192.168.1.72:8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0 start-dev


docker rm -f keycloak


docker run -d --name keycloak -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0 start-dev


# Conteneurisation frontend
docker build -t frontend-app .

## lancement frontend dans le conteneur 
docker run --name frontend -p 5173:5173 frontend-app 
 
