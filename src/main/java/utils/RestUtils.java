package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import data.PeriodType;
import data.RequestStates;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestUtils {
    private Properties property;

    public RestUtils() {
        FileInputStream fileInputStream;
        property = new Properties();

        try {
            fileInputStream = new FileInputStream("src/main/resources/endpoints.properties");
            property.load(fileInputStream);
        } catch (IOException e) {
            System.err.println("Property file is missing");
        }
    }
    private JSONObject getMostEmailedArticles(String section, PeriodType period){
        return getMostEmailedArticles(section, period, 0);
    }

    private JSONObject getMostEmailedArticles(String section, PeriodType period, Integer offset){
        Response getMostEmailedArticles = RestAssured.
                given()
                .header("apiKey",property.getProperty("api.key"))
                .pathParam("section", section)
                .pathParam("timePeriod", period.toString())
                .param("offset",offset)
                .when()
                .get(property.getProperty("api.URL"));
        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject)parser.parse(getMostEmailedArticles.asString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
         return  response;
    }

    //TODO : Added this workaround, because of API limitation
    protected Map getMostEmailedArticlesBySectionAndTimePeriod(String section, PeriodType period){
        JSONArray sectionJSON = (JSONArray)getMostEmailedArticles(section, period).get("results");
        Integer numberOfResults = Integer.parseInt(getMostEmailedArticles(section, period).get("num_results").toString());
        Integer iterator = 20;
        while ( iterator < numberOfResults){
            sectionJSON.addAll((JSONArray)getMostEmailedArticles(section, period,iterator).get("results"));
            iterator = iterator + 20;
        }
        Map sectionMap = new HashMap();
        for (Object el : sectionJSON) {
            JSONObject ob = (JSONObject) el;
            Object value = sectionMap.get(ob.get("section").toString());
            if (value != null) {
                sectionMap.put(ob.get("section").toString(), Integer.parseInt(value.toString()) + 1);
            } else {
                sectionMap.put(ob.get("section").toString(), 1);
            }
        }
        return sectionMap;
    }

    public Boolean verifySectionNumber(){
        Boolean state = true;
        Map resultForAllSections = getMostEmailedArticlesBySectionAndTimePeriod("all-sections", PeriodType.DAY);
        Iterator section = resultForAllSections.entrySet().iterator();
        while (section.hasNext()) {
            Map.Entry pair = (Map.Entry)section.next();
            Map sectionMap = getMostEmailedArticlesBySectionAndTimePeriod(pair.getKey().toString(), PeriodType.DAY);
            state = state && sectionMap.get(pair.getKey().toString()).equals(pair.getValue());
            section.remove();
        }
        return state;
    }

    public Boolean verifyInvalidPeriod(){
        JSONObject response = getMostEmailedArticles("all-sections",PeriodType.INVALID);
        JSONArray errorsJSON = (JSONArray)response.get("errors");
        return response.get("status").equals(RequestStates.ERROR.toString()) &&
                errorsJSON.get(0).equals(property.getProperty("api.invalid.param.message"));
    }

    public Boolean verifyInvalidSection(){
        JSONObject response = getMostEmailedArticles("fake",PeriodType.DAY);
        return response.get("status").equals(RequestStates.OK.toString()) &&
                response.get("num_results").toString().equals("0");
    }
}
