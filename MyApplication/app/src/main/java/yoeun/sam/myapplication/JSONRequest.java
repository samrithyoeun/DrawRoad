package yoeun.sam.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mister_Brown on 11/26/2016.
 */

public class JSONRequest {

    static float LINE_WIDTH =20;
    static String LINE_COLOR ="#FF0000";

    public static PolylineOptions GetDirection(final Context context, final GoogleMap mMap, String origin, String destination){
        final PolylineOptions[] polyline = new PolylineOptions[1];

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=AIzaSyDZX40EuY1U9PsnHdtJb60AamHBKPeoltM";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Volley: ", "response success");
                Log.d("Volley: ", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray geo_waypoints = jsonObject.getJSONArray("routes");
                    JSONObject route_1 = (JSONObject) geo_waypoints.get(0);
                    JSONObject overview_polyline = route_1.getJSONObject("overview_polyline");
                    String points = overview_polyline.getString("points");
                    Log.d("JSONObject: ", points);

                    List<LatLng> pointList = PolyUtil.decode(points);
                    Log.d("PolyUtil: ", pointList.toString());

                    PolylineOptions polylineOptions = new PolylineOptions();

                    polylineOptions.addAll(pointList);


                    polylineOptions.color(Color.parseColor(LINE_COLOR));
                    polylineOptions.width(LINE_WIDTH);
                    mMap.addPolyline(polylineOptions);


                    // create bounds for camera to move to direction
                    LatLngBounds.Builder builder = LatLngBounds.builder();

                    for (LatLng point : pointList
                            ) {
                        builder.include(point);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
                    polyline[0] =polylineOptions;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley: ", "response Error");
                Toast.makeText(context, "cant access to the network", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
        PolylineOptions polylineOptions=polyline[0];
        return polylineOptions;
    }
}
