
/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.



 JSON EXAMPLE

{
  "region_metadata": [
    {
      "name": "west",
      "label_location": {
        "latitude": 1.35735,
        "longitude": 103.7
      }
    },
    {
      "name": "national",
      "label_location": {
        "latitude": 0,
        "longitude": 0
      }
    },
    {
      "name": "east",
      "label_location": {
        "latitude": 1.35735,
        "longitude": 103.94
      }
    },
    {
      "name": "central",
      "label_location": {
        "latitude": 1.35735,
        "longitude": 103.82
      }
    },
    {
      "name": "south",
      "label_location": {
        "latitude": 1.29587,
        "longitude": 103.82
      }
    },
    {
      "name": "north",
      "label_location": {
        "latitude": 1.41803,
        "longitude": 103.82
      }
    }
  ],
  "items": [
    {
      "timestamp": "2018-04-23T10:00:00+08:00",
      "update_timestamp": "2018-04-23T10:06:18+08:00",
      "readings": {
        "o3_sub_index": {
          "west": 1,
          "national": 6,
          "east": 5,
          "central": 6,
          "south": 4,
          "north": 3
        },
        "pm10_twenty_four_hourly": {
          "west": 28,
          "national": 31,
          "east": 26,
          "central": 31,
          "south": 30,
          "north": 23
        },
        "pm10_sub_index": {
          "west": 28,
          "national": 31,
          "east": 26,
          "central": 31,
          "south": 30,
          "north": 23
        },
        "co_sub_index": {
          "west": 9,
          "national": 9,
          "east": 5,
          "central": 5,
          "south": 6,
          "north": 6
        },
        "pm25_twenty_four_hourly": {
          "west": 15,
          "national": 15,
          "east": 13,
          "central": 14,
          "south": 15,
          "north": 15
        },
        "so2_sub_index": {
          "west": 2,
          "national": 3,
          "east": 2,
          "central": 3,
          "south": 3,
          "north": 3
        },
        "co_eight_hour_max": {
          "west": 0.9,
          "national": 0.9,
          "east": 0.51,
          "central": 0.48,
          "south": 0.59,
          "north": 0.64
        },
        "no2_one_hour_max": {
          "west": 14,
          "national": 29,
          "east": 27,
          "central": 29,
          "south": 16,
          "north": 23
        },
        "so2_twenty_four_hourly": {
          "west": 3,
          "national": 5,
          "east": 4,
          "central": 5,
          "south": 5,
          "north": 5
        },
        "pm25_sub_index": {
          "west": 55,
          "national": 55,
          "east": 52,
          "central": 53,
          "south": 55,
          "north": 55
        },
        "psi_twenty_four_hourly": {
          "west": 55,
          "national": 55,
          "east": 52,
          "central": 53,
          "south": 55,
          "north": 55
        },
        "o3_eight_hour_max": {
          "west": 2,
          "national": 14,
          "east": 13,
          "central": 14,
          "south": 10,
          "north": 8
        }
      }
    }
  ],
  "api_info": {
    "status": "healthy"
  }
}
 */

package com.journaldev.MapsInAction;

        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONException;
        import org.json.JSONObject;

        import android.os.Bundle;

        import android.support.v4.app.FragmentActivity;
        import android.util.Log;

        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;

        import java.util.Iterator;

        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

/**
 * @author lorkh
 */
public class MainActivity extends FragmentActivity {
    private static final String LOG_TAG = "PSIMap";

    private static final String SERVICE_URL = "https://api.data.gov.sg/v1/environment/psi";

    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Retrieve the city data from the web service
        // In a worker thread since it's a network operation.
        new Thread(new Runnable() {
            public void run() {
                try {
                    retrieveAndAddCities();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Cannot retrive cities", e);
                    return;
                }
            }
        }).start();
    }

    protected void retrieveAndAddCities() throws IOException {
        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {
            // Connect to the web service
            URL url = new URL(SERVICE_URL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Read the JSON data into the StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to service", e);
            throw new IOException("Error connecting to service", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Create markers for the city data.
        // Must run this on the UI thread since it's a UI operation.
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    createMarkersFromJson(json.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error processing JSON", e);
                }
            }
        });
    }

    void createMarkersFromJson(String json) throws JSONException {
        // De-serialize the JSON string into an array of city objects
        Log.i("json is ", json);
        JSONObject jsonObj = new JSONObject(json);
        JSONObject jsonObjRegion;
        JSONObject jsonObjReadings;
        int y = jsonObj.getJSONArray("region_metadata").length();


        Log.i("json obj length is ", String.valueOf(y));

        // get readings
        jsonObjReadings = (JSONObject) jsonObj.getJSONArray("items").get(0);
        jsonObjReadings = jsonObjReadings.getJSONObject("readings");




        for (int x = 0; x < y; x++)
        {
            String strSnippet = "";

            // get region
            jsonObjRegion = (JSONObject) jsonObj.getJSONArray("region_metadata").get(x);
            Log.i("jsonObjRegion is ", jsonObjRegion.toString());
            Log.i("latitude is ", String.valueOf(jsonObjRegion.getJSONObject("label_location").get("latitude")));
            Log.i("longitude is ", String.valueOf(jsonObjRegion.getJSONObject("label_location").get("longitude")));



            Log.i("jsonObjReadings is ", String.valueOf(jsonObjReadings));
           // Log.i("jsonObjReadings length is ", String.valueOf(jar.length()));


            Iterator<String> iter = jsonObjReadings.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Log.i("key is ", key);

                try {
                    Object value = jsonObjReadings.get(key);
                    Log.i("value is ", value.toString());

                    JSONObject jsonObjReadings2 = new JSONObject(String.valueOf(value));
                    Iterator<String> iter2 = jsonObjReadings2.keys();
                    while (iter2.hasNext()) {
                        String key2 = iter2.next();
                        try {
                            Object value2 = jsonObjReadings2.get(key2);
                            if (key2.equals(jsonObjRegion.getString("name"))){
                                strSnippet = strSnippet + key + " " + value2 + " ";

                            }
                            Log.i("key2 is ", key2.toString());
                            Log.i("value2 is ", value2.toString());
                        } catch (JSONException e) {

                        }
                    }

                } catch (JSONException e) {
                    // Something went wrong!
                }
            }


            map.addMarker(new MarkerOptions()
                    .title(jsonObjRegion.getString("name"))
                    .snippet(strSnippet)
                    .position(new LatLng(

                            jsonObjRegion.getJSONObject("label_location").getDouble("latitude"),
                            jsonObjRegion.getJSONObject("label_location").getDouble("longitude")
                    ))
            );



        }



    }
}
