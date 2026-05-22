APP_NAME=my-blog-app
CONTAINER_NAME=my-blog-container

build:
	mvn clean package -DskipTests

test:
	mvn test

run:
	java -jar target/*.jar

docker-build:
	docker build -t $(APP_NAME) .

docker-run:
	docker run -p 8080:8080 --name $(CONTAINER_NAME) $(APP_NAME)

docker-stop:
	docker stop $(CONTAINER_NAME)

docker-remove:
	docker rm $(CONTAINER_NAME)

docker-logs:
	docker logs -f $(CONTAINER_NAME)

clean:
	mvn clean