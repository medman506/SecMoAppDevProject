package ims.fhj.at.emergencyalerter.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by michael.stifter on 12.11.2016.
 */

public class PlacesJsonParser {

    private static String TAG = "PlacesJsonParser";

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> placeMap = null;

        int placesCount = jsonArray.length();

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlacesJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();

        String placeId = "";
        String iconLink = "";
        String placeName = "-";
        String vicinity = "-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if (!googlePlacesJson.isNull("place_id")) {
                placeId = googlePlacesJson.getString("place_id");
            }

            if (!googlePlacesJson.isNull("name")) {
                placeName = googlePlacesJson.getString("name");
            }

            if (!googlePlacesJson.isNull("icon")) {
                iconLink = googlePlacesJson.getString("icon");
            }

            if (!googlePlacesJson.isNull("vicinity")) {
                vicinity = googlePlacesJson.getString("vicinity");
            }

            if (!googlePlacesJson.isNull("geometry") && !googlePlacesJson.getJSONObject("geometry").isNull("location") && !googlePlacesJson.getJSONObject("geometry").getJSONObject("location").isNull("lat")) {
                latitude = googlePlacesJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            }

            if (!googlePlacesJson.isNull("geometry") && !googlePlacesJson.getJSONObject("geometry").isNull("location") && !googlePlacesJson.getJSONObject("geometry").getJSONObject("location").isNull("lng")) {
                longitude = googlePlacesJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            }

            if (!googlePlacesJson.isNull("reference")) {
                reference = googlePlacesJson.getString("reference");
            }

            googlePlaceMap.put("place_id", placeId);
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("icon_link", iconLink);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

}
