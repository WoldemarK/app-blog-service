# My Blog Backend

Бэкенд приложения-блога на Java 21 и Spring Framework.

## 📦 Технологии

- Java 21
- Spring Framework
- Maven
- PostgreSQL / H2
- Docker
- JUnit 5
- Tomcat / Jetty

---

## 🚀 Запуск проекта локально

### 1. Склонировать репозиторий

```bash
git clone https://github.com/your-name/my-blog-back-app.git
cd my-blog-back-app
```

---

### 2. Собрать проект

```bash
mvn clean package
```

После сборки появится файл:

```bash
target/*.jar
```

---

### 3. Запустить приложение

```bash
java -jar target/*.jar
```

Приложение будет доступно:

```text
http://localhost:8080
```

---

## 🐳 Docker

### Сборка Docker-образа

```bash
docker build -t my-blog-app .
```

---

### Запуск контейнера

```bash
docker run -p 8080:8080 my-blog-app
```

---

## 📂 Структура проекта

```text
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

---

## 🧪 Тесты

Запуск всех тестов:

```bash
mvn test
```

---

## 📡 Основные REST API

### Получить список постов

```http
GET /api/posts
```

---

### Получить пост

```http
GET /api/posts/{id}
```

---

### Создать пост

```http
POST /api/posts
```

---

### Удалить пост

```http
DELETE /api/posts/{id}
```

---

## 📸 Работа с изображениями

### Загрузка изображения

```http
PUT /api/posts/{id}/image
```

### Получение изображения

```http
GET /api/posts/{id}/image
```

---

## 📝 Комментарии

### Получить комментарии

```http
GET /api/posts/{id}/comments
```

### Добавить комментарий

```http
POST /api/posts/{id}/comments
```

---

## ⚙️ Переменные окружения

Пример:

```bash
DB_URL=jdbc:postgresql://localhost:5432/blog
DB_USER=postgres
DB_PASSWORD=postgres
```

---

## 🐳 Dockerfile

Проект использует multi-stage build:

1. Maven собирает jar
2. Финальный образ содержит только Java Runtime

---

## 👨‍💻 Автор

[Kovtunov Vladimir](https://github.com/WoldemarK)
[Software Engineering Telegram](https://t.me/K_Waldemar)