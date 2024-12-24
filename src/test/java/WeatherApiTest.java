import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WeatherApiTest {
	
	//http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=a3c2848fbc5fc3f2a9aa69c05fbe9278

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "a3c2848fbc5fc3f2a9aa69c05fbe9278"; // Replace with your actual API key

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        String baseURI = RestAssured.baseURI;
        System.out.println("URL: " + baseURI);
    }

    @Test
    public void testGetWeatherByCityName() {
        String cityName = "London";
        Response response = RestAssured.given()
                .param("q", cityName)
                .param("APPID", API_KEY)
                .get();
        
        String responseAfter = response.toString();
        System.out.println("Response: " + responseAfter);

        int statusCode = response.getStatusCode();
        System.out.println("StatusCode = " + statusCode );
        Assert.assertEquals(response.getStatusCode(), 200);
        String name = response.jsonPath().getString("name");
        System.out.println("Name = " + name );
        Assert.assertTrue(response.jsonPath().getString("name").equalsIgnoreCase(cityName));
        Assert.assertNotNull(response.jsonPath().get("weather"));
        Assert.assertNotNull(response.jsonPath().get("main"));
    }

    @Test
    public void testGetWeatherByCoordinates() {
        double lat = 51.5073;
        double lon = -0.1276;

        Response response = RestAssured.given()
                .param("lat", lat)
                .param("lon", lon)
                .param("appid", API_KEY)
                .get();

        Assert.assertEquals(response.getStatusCode(), 200);
        Double latitud = response.jsonPath().getDouble("coord.lat");
        System.out.println("Latitud: " + latitud);
        Assert.assertEquals(response.jsonPath().getDouble("coord.lat"), lat);
        Double longitud = response.jsonPath().getDouble("coord.lon");
        System.out.println("longitud: " + longitud);
        Assert.assertEquals(response.jsonPath().getDouble("coord.lon"), lon);
        Assert.assertNotNull(response.jsonPath().get("weather"));
        Assert.assertNotNull(response.jsonPath().get("main"));
    }

    @Test
    public void testErrorHandlingForInvalidCityName() {
        String invalidCityName = "InvalidCityName";

        Response response = RestAssured.given()
                .param("q", invalidCityName)
                .param("appid", API_KEY)
                .get();

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("cod"), "404");
        Assert.assertNotNull(response.jsonPath().getString("message"));
    }

    @Test
    public void testErrorHandlingForInvalidCoordinates() {
        String invalidLat = "abc";
        String invalidLon = "xyz";

        Response response = RestAssured.given()
                .param("lat", invalidLat)
                .param("lon", invalidLon)
                .param("appid", API_KEY)
                .get();

        Assert.assertEquals(response.getStatusCode(), 400);
        Assert.assertEquals(response.jsonPath().getString("cod"), "400");
        Assert.assertNotNull(response.jsonPath().getString("message"));
    }
}