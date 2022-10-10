package GoRest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class GoRestUsersTests {

    @BeforeClass
    void Setup(){
        //RestAssured kendi statik değişkeni tanımlı değer atanıyor.
        baseURI="https://gorest.co.in/public/v2/";
    }

    public String getRandomName()  //randomName almak için bu kod ekleniyor.Cucumber'da da yapmıştık.
    {
        return RandomStringUtils.randomAlphabetic(8);
    }
    public String getRandomEmail()     //randomEmail almak için bu kod ekleniyor.Cucumber'da da yapmıştık.
    {
        return RandomStringUtils.randomAlphabetic(8)+"@gmail.com";
    }

    int userID=0;
    User newUser;

    @Test
    public void createUserObject()      //ikinci yöntem: object oluşturmak. daha cok tercih edilir. hata riski düşük.
    {
        newUser= new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID=
                given()
                    // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri
                    .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                    .contentType(ContentType.JSON)
                    .body(newUser)
                    .log().body()
                    .when()
                    .post("users")

                    .then()
                    .log().body()
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    //.extract().path("id")
                    .extract().jsonPath().getInt("id")
    ;

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

        System.out.println("userID = " + userID);
    }


    @Test (dependsOnMethods = "createUserObject")
    public void updateUserObject()
    {
//        Map<String, String> updateUser=new HashMap<>();
//        updateUser.put("name","sinan ince");

        newUser.setName("sinan ince");

                given()
                        .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .pathParam("userID", userID)

                        .when()
                        .put("users/{userID}")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name", equalTo("sinan ince"))
                ;
    }

    @Test(dependsOnMethods = "createUserObject",priority = 2)
    public void getUserByID()
    {
        given()
                .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .get("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "createUserObject",priority = 3)
    public void deleteUserById()
    {
        given()
                .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteUserById")
    public void deleteUserByIdNegative()
    {
        given()
                .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(404)
        ;
    }

    @Test
    public void getUsers() {
        Response response =
                given()
                        .header("Authorization", "Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")

                        .when()
                        .get("users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();

        // TODO : 3 usersın id sini alınız (path ve jsonPath ile ayrı ayrı yapınız)
        //aşağıdaki şekilde 2 farklı şekilde alınabilir.
        int idUser3path = response.path("[2].id");
        int idUser3JsonPath = response.jsonPath().getInt("[2].id");
        System.out.println("idUser3path = " + idUser3path);      //idUser3path = 3283
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);    //idUser3JsonPath = 3283


        // TODO : Tüm gelen veriyi bir nesneye atınız
        User[] usersPath= response.as(User[].class);
        System.out.println("Arrays.toString(userPath) = " + Arrays.toString(usersPath));

        List<User> usersJsonPath=response.jsonPath().getList("", User.class);
        System.out.println("usersJsonPath = " + usersJsonPath);

    }
        @Test
        public void getUserByIDExtract()

        // TODO : GetUserByID testinde dönen user ı bir nesneye atınız.
        {
            User user=
            given()
                    .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                    .contentType(ContentType.JSON)
                    .pathParam("userID", 3414)

                    .when()
                    .get("users/{userID}")

                    .then()
                    .log().body()
                    .statusCode(200)
//                  .extract().as(User.class);
                    .extract().jsonPath().getObject("", User.class)
                    ;

            System.out.println("user = " + user);
        }

    @Test
    public void getUsersV1() {
        Response response =
                given()
                        .header("Authorization", "Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().response();

//      response.as();   // tüm gelen respons a uygun nesnelerin yapılması gerekiyor

        List<User> dataUsers = response.jsonPath().getList("data", User.class);  //JSONPATH te response içindeki bir parçayı
                                                                //nesneye dönüştürebiliyoruz. farkı bu. üstteki yazılıştan farkı bu.

        System.out.println("datausers = " + dataUsers);

        // Daha önceki örneklerde (as) Class dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise (JsanPath) aradaki bir veriyi class a dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık. Böylece tek class ile veri alınmış oldu
        // diğer class lara gerek kalmadan.

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.


    }













    @Test (enabled = false)
    public void createUser()
    {
        int userID=
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri

                        .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\""+getRandomName()+"\", \"gender\":\"male\", \"email\":\""+getRandomEmail()+"\", \"status\":\"active\"}")

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        ;

        System.out.println("userID = " + userID);
    }


    @Test (enabled = false)
    public void createUserMap()     //birinci yöntem: üstteki yöntemde body kısmı çok karışık yazıldığı için bu iki yöntemi uygulayabiliriz.
    {
        Map<String,String> newUser=new HashMap<>();
        newUser.put("name",getRandomName());
        newUser.put("gender","male");
        newUser.put("email",getRandomEmail());
        newUser.put("status","active");

        int userID=
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri

                        .header("Authorization","Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        ;

        System.out.println("userID = " + userID);

    }
}

class User{     //bir class açarak değişkenler tanımlanır, getter setter ları alınır.
    private int id;
    private String name;
    private String gender;
    private String email;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}



