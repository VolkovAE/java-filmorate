package ru.yandex.practicum.filmorate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest   //если оставить так, то встроенный веб-сервер не запустится и не смогу проверить http-запросы.
//Запускаю встроенный веб-сервер и слушаю порт указанный в настройках см. webEnvironment
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {
    @Test
    void contextLoads() {
    }

    @Test
    void addFilm(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Интерстеллар");
        jsonFilm.addProperty("description", "Когда засуха, пыльные бури и вымирание растений приводят " +
                "человечество к продовольственному кризису, коллектив исследователей и учёных отправляется " +
                "сквозь червоточину, чтобы себя спасти.");
        jsonFilm.addProperty("releaseDate", "2014-11-06");
        jsonFilm.addProperty("duration", 10140);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    void updateFilm(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Зеленая миля");
        jsonFilm.addProperty("description", "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», " +
                "каждый из узников которого однажды проходит «зеленую милю» по пути к месту казни.");
        jsonFilm.addProperty("releaseDate", "1999-04-18");
        jsonFilm.addProperty("duration", 11340);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // получаем id добавленного фильма
        long id = jsonObject.get("id").getAsLong();

        //Обновляем фильм, только дату выпуска фильма (исправляем).
        String correctReleaseDate = "2000-04-18";

        JsonObject jsonFilmUpdate = new JsonObject();
        jsonFilmUpdate.addProperty("id", id);
        jsonFilmUpdate.addProperty("releaseDate", correctReleaseDate);    //корректная дата выхода фильма

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilmUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о фильме
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //Проверим по результату запроса, что данные обновились.
        //Проверяем формат ответа от сервера.
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        jsonObject = jsonElement.getAsJsonObject();

        // получаем id добавленного фильма
        assertEquals(correctReleaseDate, jsonObject.get("releaseDate").getAsString());
    }

    @Test
    void shouldBeStatus400CreateFilmWithNameNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        //jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "сражается с хулиганом Верзилой, весьма оригинальным способом готовится к экзамену и " +
                "предотвращает «ограбление века».");
        jsonFilm.addProperty("releaseDate", "1965-07-23");
        jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно проблема из-за пустого наименования.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Название фильма не может быть пустым.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по наименованию фильма.");
    }

    @Test
    void shouldBeStatus400CreateFilmWithDescriptionLength201(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        //jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "сражается с хулиганом Верзилой, весьма оригинальным способом готовится к экзамену и " +
                "предотвращает «ограбление века», на которое идёт троица бандитов — Балбес, Трус и Бывалый.");
        jsonFilm.addProperty("releaseDate", "1965-07-23");
        jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно проблема из-за длины описания фильма.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Максимальная длина описания — 200 символов.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по описанию фильма.");

    }

    @Test
    void shouldBeStatus200updateFilmWithDescriptionLength200(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "сражается с хулиганом Верзилой, весьма оригинальным способом готовится к экзамену и " +
                "предотвращает «ограбление века».");
        jsonFilm.addProperty("releaseDate", "1965-07-23");
        jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // получаем id добавленного фильма
        long id = jsonObject.get("id").getAsLong();

        //Обновляем фильм, только описание фильма (200 символов, граничный вариант).
        String newDescription = "Студент Шурик попадает в самые невероятные ситуации: сражается с хулиганом Верзилой, " +
                "весьма оригинальным способом готовится к экзамену и предотвращает «ограбление века», на которое " +
                "идёт троица бандито";

        JsonObject jsonFilmUpdate = new JsonObject();
        jsonFilmUpdate.addProperty("id", id);
        jsonFilmUpdate.addProperty("description", newDescription);

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilmUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о фильме
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //Проверяем формат ответа от сервера.
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        jsonObject = jsonElement.getAsJsonObject();

        // проверяем, что описание изменилось
        assertEquals(newDescription, jsonObject.get("description").getAsString());
    }

    @Test
    void shouldBeStatus400CreateFilmWithReleaseDate27121895(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "сражается с хулиганом Верзилой, весьма оригинальным способом готовится к экзамену и " +
                "предотвращает «ограбление века».");
        jsonFilm.addProperty("releaseDate", "1895-12-27");
        jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за даты выпуска.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Даты выпуска фильма не может быть ранее 28.12.1895", jsonObject.get("message").getAsString(),
                "Ошибка валидация по дате выпуска фильма.");
    }

    @Test
    void shouldBeStatus200CreateFilmWithReleaseDate28121895(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "сражается с хулиганом Верзилой, весьма оригинальным способом готовится к экзамену и " +
                "предотвращает «ограбление века»!");
        jsonFilm.addProperty("releaseDate", "1895-12-28");
        jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldBeStatus400CreateFilmWithDurationNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "длительность равна null");
        jsonFilm.addProperty("releaseDate", "1895-12-28");
        //jsonFilm.addProperty("duration", 5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за длительности.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Нужно указать длительность фильма в сек.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по длительности фильма is null.");
    }

    @Test
    void shouldBeStatus400CreateFilmWithDuration0(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "длительность равна 0");
        jsonFilm.addProperty("releaseDate", "1895-12-28");
        jsonFilm.addProperty("duration", 0);//5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за длительности.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Продолжительность фильма должна быть положительным числом.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по длительности фильма равной 0.");
    }

    @Test
    void shouldBeStatus400CreateFilmWithDurationNegative(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
        jsonFilm.addProperty("name", "Операция «Ы» и другие приключения Шурика");
        jsonFilm.addProperty("description", "Студент Шурик попадает в самые невероятные ситуации: " +
                "длительность равна -1");
        jsonFilm.addProperty("releaseDate", "1895-12-28");
        jsonFilm.addProperty("duration", -1);//5700);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за длительности.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Продолжительность фильма должна быть положительным числом.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по длительность фильма меньше нуля.");
    }

    @Test
    void shouldBeStatus400UpdateFilmWithIDNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Обновляем фильм, только дату выпуска фильма (исправляем).
        String correctReleaseDate = "2000-04-18";

        JsonObject jsonFilmUpdate = new JsonObject();
        //jsonFilmUpdate.addProperty("id", id);
        jsonFilmUpdate.addProperty("releaseDate", correctReleaseDate);    //корректная дата выхода фильма

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilmUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о фильме
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за id = null.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("При обновлении данных о фильме должен быть указан его id.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по id фильма is null.");
    }

    @Test
    void shouldBeStatus404UpdateFilmWithIDNotExist(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Обновляем фильм, только дату выпуска фильма (исправляем).
        String correctReleaseDate = "2000-04-18";
        long id = 1_000_000_000L;

        JsonObject jsonFilmUpdate = new JsonObject();
        jsonFilmUpdate.addProperty("id", id);
        jsonFilmUpdate.addProperty("releaseDate", correctReleaseDate);    //корректная дата выхода фильма

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilmUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о фильме
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        //Проверим по сообщению, что действительно из-за указания несуществующего id.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Фильм с id = " + id + " не найден.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по не существующему ID фильма.");
    }

    @Test
    void shouldBeStatus400CreateFilmWithBodyEmpty(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
//        jsonFilm.addProperty("name", "Интерстеллар");
//        jsonFilm.addProperty("description", "Когда засуха, пыльные бури и вымирание растений приводят " +
//                "человечество к продовольственному кризису, коллектив исследователей и учёных отправляется " +
//                "сквозь червоточину, чтобы себя спасти.");
//        jsonFilm.addProperty("releaseDate", "06.11.2014");
//        jsonFilm.addProperty("duration", 10140);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());   //сработали все валидации (violations)
    }

    @Test
    void shouldBeStatus400UpdateFilmWithBodyEmpty(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        //Добавляем фильм.
        JsonObject jsonFilm = new JsonObject();
//        jsonFilm.addProperty("name", "Интерстеллар");
//        jsonFilm.addProperty("description", "Когда засуха, пыльные бури и вымирание растений приводят " +
//                "человечество к продовольственному кризису, коллектив исследователей и учёных отправляется " +
//                "сквозь червоточину, чтобы себя спасти.");
//        jsonFilm.addProperty("releaseDate", "06.11.2014");
//        jsonFilm.addProperty("duration", 10140);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilm.toString()))
                .build();

        // вызываем рест, отвечающий за создание фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());   //сработала валидация по полю id
    }

    @Test
    void addUser(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Chadrick79@hotmail.com");
        jsonUsers.addProperty("login", "Kacie.Koch");
        jsonUsers.addProperty("name", "KK_");
        jsonUsers.addProperty("birthday", "1954-11-06");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    void updateUser(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        jsonUsers.addProperty("login", "Ella_Kshlerin");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        //Обновим пользователя, наименование.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // получаем id добавленного пользователя
        long id = jsonObject.get("id").getAsLong();

        //Обновляем пользователя, только наименование.
        String newName = "Valdemar";

        JsonObject jsonUserUpdate = new JsonObject();
        jsonUserUpdate.addProperty("id", id);
        jsonUserUpdate.addProperty("name", newName);

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUserUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о пользователе
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //Проверяем формат ответа от сервера.
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        jsonObject = jsonElement.getAsJsonObject();

        // проверяем, что описание изменилось
        assertEquals(newName, jsonObject.get("name").getAsString());
    }

    @Test
    void shouldBeStatus400UpdateUserWithIDNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Обновляем пользователя, только наименование.
        String newName = "Valdemar";

        JsonObject jsonUserUpdate = new JsonObject();
//        jsonUserUpdate.addProperty("id", id);
        jsonUserUpdate.addProperty("name", newName);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUserUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о пользователе
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за id = null.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("При обновлении данных о пользователе должен быть указан его id.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по id пользователя id null.");
    }

    @Test
    void shouldBeStatus400UpdateUserWithIDNotExist(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Обновляем пользователя, только наименование.
        long id = 1_000_000_000L;
        String newName = "Valdemar";

        JsonObject jsonUserUpdate = new JsonObject();
        jsonUserUpdate.addProperty("id", id);
        jsonUserUpdate.addProperty("name", newName);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUserUpdate.toString()))
                .build();

        // вызываем рест, отвечающий за обновление данных о пользователе
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        //Проверим по сообщению, что действительно из-за не существующего id.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Пользователь с id = " + id + " не найден.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по несуществующему id пользователя.");
    }

    @Test
    void shouldBeStatus400CreateUserWithEmailNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        //jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        jsonUsers.addProperty("login", "Ella_Kshlerin");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не указанного email.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Электронная почта не может быть пустой.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по email is null.");
    }

    @Test
    void shouldBeStatus400CreateUserWithEmailErrorFormat(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90-hotmail.com");
        jsonUsers.addProperty("login", "Ella_Kshlerin");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не указанного email.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Email is not valid", jsonObject.get("message").getAsString(),
                "Ошибка валидация по формату email.");
    }

    @Test
    void shouldBeStatus400CreateUserWithLoginNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        //jsonUsers.addProperty("login", "Ella_Kshlerin");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не указанного логина.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Логин не может быть пустым.", jsonObject.get("message").getAsString(),
                "Ошибка валидация логина is null.");
    }

    @Test
    void shouldBeStatus400CreateUserWithLoginLength2(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        jsonUsers.addProperty("login", "El");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за логина длиной менее 2 символов.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Длина логина не менее 3 и не более 116 символов. Логин не может содержать пробелы.",
                jsonObject.get("message").getAsString(), "Ошибка валидация по длине логина пользователя.");
    }

    @Test
    void shouldBeStatus400CreateUserWithLoginLength117(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        jsonUsers.addProperty("login", "Ella_Kshlerin12340123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за логина длиной более 16 символов.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Длина логина не менее 3 и не более 116 символов. Логин не может содержать пробелы.",
                jsonObject.get("message").getAsString(), "Ошибка валидация по длине логина пользователя.");
    }

    @Test
    void shouldBeStatus400CreateUserWithLoginContainSpace(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Joelle90@hotmail.com");
        jsonUsers.addProperty("login", "Ella Kshlerin");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за логина с пробелом.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Длина логина не менее 3 и не более 116 символов. Логин не может содержать пробелы.",
                jsonObject.get("message").getAsString(), "Ошибка валидация по длине логина пользователя.");
    }

    @Test
    void shouldBeStatus400CreateUserWithBirthdayIsNull(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Earnest.Jacobs@gmail.com");
        jsonUsers.addProperty("login", "Car_Macejkovic");
        //jsonUsers.addProperty("name", "");
        //jsonUsers.addProperty("birthday", "13.12.1974");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не указания даты рождения.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Нужно указать дату рождения.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по дате рождения.");
    }

    @Test
    void shouldBeStatus400CreateUserWithBirthdayIsFuture(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Earnest.Jacobs@gmail.com");
        jsonUsers.addProperty("login", "Car_Macejkovic");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "2974-12-13");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не указания даты рождения.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Дата рождения не может быть в будущем.", jsonObject.get("message").getAsString(),
                "Ошибка валидация по дате рождения.");
    }

    @Test
    void shouldBeStatus400CreateUserWithEmailNotUnique(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Angel.Glover@hotmail.com");
        jsonUsers.addProperty("login", "Pat_Stanton");
        jsonUsers.addProperty("name", "KK_1");
        jsonUsers.addProperty("birthday", "1987-12-31");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        //Добавляем пользователя.
        jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Angel.Glover@hotmail.com");
        jsonUsers.addProperty("login", "Car_Macejkovic");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не уникального значения почты.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Нельзя создать пользователя по причине: этот имейл уже используется.",
                jsonObject.get("message").getAsString(), "Ошибка валидация по дублю почты.");
    }

    @Test
    void shouldBeStatus400CreateUserWithLoginNotUnique(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Angel.Glover@hot.com");
        jsonUsers.addProperty("login", "Pat_Stanton15");
        jsonUsers.addProperty("name", "KK_1");
        jsonUsers.addProperty("birthday", "1987-12-31");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        //Добавляем пользователя.
        jsonUsers = new JsonObject();
        jsonUsers.addProperty("email", "Glover@hotmail.com");
        jsonUsers.addProperty("login", "Pat_Stanton15");
        //jsonUsers.addProperty("name", "");
        jsonUsers.addProperty("birthday", "1974-12-13");

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());

        //Проверим по сообщению, что действительно из-за не уникального значения почты.
        //Проверяем формат ответа от сервера.
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        // преобразуем результат разбора текста в JSON-объект
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        jsonElement = jsonObject.get("violations");
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(1, jsonArray.size(), "Ответ от сервера не соответствует ожидаемому.");

        jsonElement = jsonArray.get(0);
        assertTrue(jsonElement.isJsonObject(), "Ответ от сервера не соответствует ожидаемому.");

        jsonObject = jsonElement.getAsJsonObject();

        assertEquals("Нельзя создать пользователя по причине: этот логин уже используется.",
                jsonObject.get("message").getAsString(), "Ошибка валидация по дублю логина.");
    }

    @Test
    void shouldBeStatus400CreateUserWithBodyEmpty(@Value("${server.port}") String port)
            throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        //Добавляем пользователя.
        JsonObject jsonUsers = new JsonObject();
//        jsonUsers.addProperty("email", "Angel.Glover@hot.com");
//        jsonUsers.addProperty("login", "Pat_Stanton15");
//        jsonUsers.addProperty("name", "KK_1");
//        jsonUsers.addProperty("birthday", "31.12.1987");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUsers.toString()))
                .build();

        // вызываем рест, отвечающий за создание пользователя
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(400, response.statusCode());   //сработали валидации по всем полям user (violations)
    }

    @Test
    void shouldBeStatus200GetUsers(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlUsers = URI.create(String.format("http://localhost:%s/users", port));

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlUsers)
                .GET()
                .build();

        // вызываем рест, отвечающий за получение списка пользователей
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldBeStatus200GetFilms(@Value("${server.port}") String port) throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI urlFilms = URI.create(String.format("http://localhost:%s/films", port));

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json;charset=utf-8")
                .uri(urlFilms)
                .GET()
                .build();

        // вызываем рест, отвечающий за получение списка фильма
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());
    }
}
