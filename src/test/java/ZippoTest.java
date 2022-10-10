import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {


    @Test
    public void test() {

        given()
                    //hazırlık işlemleri yapılacak (token, send body, parametreler)

                .when()
                    //link ve metod verilecek

                .then()
                    //assertion ve verileri ele alınacak, extract
;
    }

    @Test
    public void statusCodeTest() {

        given()
                //hazırlık işlemleri yapılacak (token, send body, parametreler)

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()            // log.all() bütün response'ı gösterir
                .statusCode(200)       // status kontrolü
        ;                                 //bu kısımlar postman'deki menülerin aynısıdır.

    }

    @Test
    public void contentTypeTest() {

        given()
                //hazırlık işlemleri yapılacak (token, send body, parametreler)

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)   //dönen bölüm JSON türünde mi diye kontrol ettik. TEXT yazsaydık hata alırdık.
        ;

    }

    @Test
    public void checkStateInResponseBody() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States"))   //body.country == United States?
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest2() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California"))
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest3() {

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasItem("Dervişler Köyü"))  //verilen path de belirtilen item var mı?

                .statusCode(200)
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))  // verilen path deki listin size kontrolü
                .statusCode(200)
        ;
    }

    @Test
    public void combiningTest() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))  // verilen path deki listin size kontrolü
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'",equalTo("Beverly Hills"))
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country", "us")
                .pathParam("ZipKod", "90210")
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest2() {
        //90210 dan 90213 ye kadar test sonuçlarında places size nın hepsinde 1 geldiğini test ediniz.

        for(int i=90210; i<=90213; i++){
        given()
                .pathParam("Country", "us")
                .pathParam("ZipKod", "90210")
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                .then()
                .log().body()
                .body("places",hasSize(1))
                .statusCode(200)
        ;
    }
    }

    @Test
    public void queryParamTest() {
        //https://gorest.co.in/public/v1/users?/page=1

        given()
                .param("page", 1)   //param yazınca gelen bilgileri parametre şeklinde verir
                .log().uri()  //request linki

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest2() {
        //https://gorest.co.in/public/v1/users?/page=1

        for (int pageNo=1; pageNo<=10; pageNo++) {
        given()
                .param("page", pageNo)
                .log().uri()  //request linki

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(pageNo))
                .statusCode(200)
        ;
        }
    }

    // sürekli uygulanan işlemler için aşağıdaki şekilde ortak metod yazılır.
    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeClass   //her çalışandan önce çalışacak
    void Setup(){

      baseURI = "https://gorest.co.in/public/v1";

      requestSpecs = new RequestSpecBuilder()
              .log(LogDetail.URI)
              .setAccept(ContentType.JSON)
              .build();

      responseSpecs = new ResponseSpecBuilder()
              .expectStatusCode(200)
              .expectContentType(ContentType.JSON)
              .log(LogDetail.BODY)
              .build();
    }
    //Örnek
    @Test
    public void requestResponseSpecification() {
        //https://gorest.co.in/public/v1/users?/page=1

            given()
                    .param("page", 1)
                    .spec(requestSpecs)

                    .when()
                    .get("/users")  //url nin başında http yoksa baseUri deki değer otomatik atanır

                    .then()
                    .body("meta.pagination.page", equalTo(1))
                    .spec(responseSpecs)
            ;
        }
    //Json extract
    @Test
    public void extractingJsonPath() {

        String placeName=
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
//                .log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'")
                //extract metodu il given ile başlayan satır, bir değer döndürür hale geldi
                //en sonunda extract olmalı
        ;
        System.out.println("placeName = " + placeName);
    }

    @Test
    public void extractingJsonPathInt() {

        int limit=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit")   //jasonpath.com sitesinde deneyerek bu pathi aldık
                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }

    @Test
    public void extractingJsonPathInt2() {

        int id=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data[2].id");
                ;
        System.out.println("id = " + id);
    }

    @Test
    public void extractingJsonPathIntList() {

        List<Integer> idler=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.id");   //data daki bütün id leri bir List şeklinde verir
//        idler = [3047, 3046, 3045, 3044, 3043, 3042, 3041, 3040, 3039, 3038]
        ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(3040));   // 3040 var mı yok mu kontrolü
    }

    @Test
    public void extractingJsonPathStringList() {

        List<String> isimler=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.name");   //data daki bütün id leri bir List şeklinde verir
//
        ;
        System.out.println("isimler = " + isimler);
        Assert.assertTrue(isimler.contains("Datta Achari"));   // var mı yok mu kontrolü
    }

    @Test
    public void extractingJsonPathStringList2() {

        Response body=    // response yöntemi ile body içindeki tüm bilgiler alınıyor, onlar içinden istenilenler listeleniyor
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().response();   //bütün body alındı
        ;
        List<Integer> idler=body.path("data.id");
        List<String> isimler=body.path("data.name");
        int limit=body.path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        System.out.println("isimler = " + isimler);
        System.out.println("idler = " + idler);
    }

    @Test
    public void extractingJsonPOJO() {  // POJO : JSon Object i  (Plain Old Java Object)

        Location yer=
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().as(Location.class); // Location şablocnu
        ;

        System.out.println("yer. = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlacename() = " +
                yer.getPlaces().get(0).getPlacename());
    }

}
