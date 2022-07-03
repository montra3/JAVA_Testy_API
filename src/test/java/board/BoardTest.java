package board;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BoardTest {
    private final String key = "d559b5eb80eee11e6f33ef711a9be7e9";
    private final String token = "884e6f55da965f9e35af908db6a9717a90cdf7f29f20b3ef2011516d174f71a3";

    @Test
    public void createNewBoard() {

        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "My first board")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("My first board", json.get("name"));


        String boardId = json.get("id");

        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createBoardWithEmptyBoardName() {

        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(400)
                .extract()
                .response();

    }

    @Test
    public void createBoardWithoutDefaultLists() {

        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board without default lists")
                .queryParam("defaultLists", false)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board without default lists", json.get("name"));

        String boardId = json.get("id");

        //https://api.trello.com/1/boards/{id}/lists

        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertEquals(0, idList.size());
    }

    @Test
    public void createBoardWithDefaultLists(){

        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board with default lists")
                .queryParam("defaultLists", true)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board with default lists", json.get("name"));

        String boardId = json.get("id");
        //https://api.trello.com/1/boards/{id}/lists
        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();


        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertEquals(3, idList.size());

        List<String>nameList = jsonGet.getList("name");
        Assertions.assertEquals("Do zrobienia", nameList.get(0));
        Assertions.assertEquals("W trakcie", nameList.get(1));
        Assertions.assertEquals("Zrobione", nameList.get(2));



    }

}