## Blog Backend API

### Бэкенд для приложения-блога на Java 21 с использованием Spring Framework 6, 
### REST API, PostgreSQL/H2 и покрытием тестами на JUnit 5.

# 📌 Описание проекта

### Проект представляет собой backend-часть блог-платформы, взаимодействующую с React frontend через REST API.

### Функциональность:

* CRUD для постов
* CRUD для комментариев
* Лайки постов
* Загрузка и получение изображений
* Поиск постов
* Пагинация
* Фильтрация по тегам
* Unit + integration tests

### Frontend работает через Nginx и обращается к backend по адресу:
http://localhost:8080

# 🛠 Технологии
* Java 21
* Spring Framework 6
* Spring MVC
* PostgreSQL
* Maven
* JUnit 5
* Docker
* Tomcat
# 📂 Структура проекта
```
src
├── main
│   ├── java
│   │   └── com.example.blog
│   │       ├── config
│   │       ├── controller   
│   │       ├── exception
│   │       ├── dto
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       └── util
│   └── resources
│        └── application.properties
│          └── schema.sql
│          └── data.sql
│
└── test
├── java
│   └── com.example.blog
│       ├── controller
│       ├── service
│       └── repository
```

## Настройки в application.properties:

* jdbc.url=jdbc:postgresql://localhost:5432/blog_db
* jdbc.username=
* jdbc.password=
* jdbc.driver=org.postgresql.Driver
