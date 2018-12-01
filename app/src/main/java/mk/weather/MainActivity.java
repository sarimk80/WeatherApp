package mk.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AsyncPlayer;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//import java.util.Set;
//import java.util.TimeZone;


public class MainActivity extends YouTubeBaseActivity {


    private static final int REQUEST_LOCATION = 1;
    public String TAG = "AWARNESS";
    public int Temperature, Due, Feel_Like, Humadity;
    TextView textview, textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8;
    //YouTubePlayerView youtubeplayerView;
    ImageView imageView, imageView1;
    ImageButton imageButton, imageButton1;
    MediaPlayer mediaPlayer;
    AsyncPlayer asyncPlayer;

    //YouTubePlayer.OnInitializedListener onInitializedListerner;

    RequestQueue requestQueue, requestQueue1, requestQueue2;
    //int MY_SOCKET_TIMEOUT_MS = 5000;
    //Double test;

    WorldSpace worldSpace = new WorldSpace();

    private GoogleApiClient mGoogleApiClient;

    // public String API_KEY = "AIzaSyA6ZjVV1EOnyD4uM8Dlm7j5EftXo8StKqc";

    //String url = "https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400";

    Music music = new Music();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.temperature);
        textView1 = (TextView) findViewById(R.id.Due);
        textView2 = (TextView) findViewById(R.id.FeelLike);
        textView3 = (TextView) findViewById(R.id.Humadity);
        textView4 = (TextView) findViewById(R.id.city);
        textView5 = (TextView) findViewById(R.id.country);
        textView6 = (TextView) findViewById(R.id.Weather_type);
        textView7 = (TextView) findViewById(R.id.SunRise);
        textView8 = (TextView) findViewById(R.id.SunSet);
        imageView = (ImageView) findViewById(R.id.Forcast);
        imageView1 = (ImageView) findViewById(R.id.image_Background);
        imageButton = (ImageButton) findViewById(R.id.PlayButton);
        imageButton1 = (ImageButton) findViewById(R.id.PauseButton);

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        // Weather();
        //HeadPhone();

        requestQueue = Volley.newRequestQueue(this);
        requestQueue1 = Volley.newRequestQueue(this);
        requestQueue2 = Volley.newRequestQueue(this);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Refresh();
            }
        });


        mediaPlayer = new MediaPlayer();

        asyncPlayer = new AsyncPlayer(TAG);

        imageButton1.setVisibility(View.GONE);


    }


    @Override
    protected void onStart() {

        // Log.d(TAG, "In On Start");

        super.onStart();
    }

    @Override
    protected void onResume() {

        imageButton = (ImageButton) findViewById(R.id.PlayButton);
        imageButton1 = (ImageButton) findViewById(R.id.PauseButton);

        Weather();

        HeadPhone();


        //Sunset_SunRise();

        //Temp_Test();

        // Temp_Test();


        super.onResume();
    }

    public void HeadPhone() {

        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient).setResultCallback(new ResultCallback<HeadphoneStateResult>() {
            @Override
            public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                if (!headphoneStateResult.getStatus().isSuccess()) {

                    //  Log.e(TAG, "Could not get headphone state.");
                    return;
                }
                HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                    // Log.i(TAG, "Headphones are plugged in.\n");
                    // Toast.makeText(MainActivity.this, "Head Phone are Plugged in", Toast.LENGTH_SHORT).show();
                } else if (headphoneState.getState() == HeadphoneState.UNPLUGGED) {
                    // Log.i(TAG, "Headphones are NOT plugged in.\n");
                    Toast.makeText(MainActivity.this, "Plug in your HeadPhone for better Experience", Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, "Head Phone are not Plugged in", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    public void Weather() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        } else {
            Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<WeatherResult>() {
                        @Override
                        public void onResult(@NonNull WeatherResult weatherResult) {
                            if (!weatherResult.getStatus().isSuccess()) {
                                // Log.e(TAG, "Could not get weather.");
                                // return;
                            }
                            Weather weather = weatherResult.getWeather();
                            // Log.d(TAG, "Weather: " + weather);

                            Temperature = (int) weather.getTemperature(Weather.CELSIUS);
                            Due = (int) weather.getDewPoint(Weather.CELSIUS);
                            Feel_Like = (int) weather.getFeelsLikeTemperature(Weather.CELSIUS);
                            Humadity = weather.getHumidity();


                        }
                    });

            Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                // Log.e(TAG, "Could not get location.");
                                return;
                            }
                            Location location = locationResult.getLocation();
                            // Log.i(TAG, "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());


                            Double long_tude = location.getLongitude();
                            Double lati_tude = location.getLatitude();

                            worldSpace.setLongitude(long_tude);
                            worldSpace.setLatitude(lati_tude);

                            //test = worldSpace.getLatitude();
                            //Log.d(TAG, String.valueOf(test));
                            Double _Latitude = worldSpace.getLatitude();
                            Double _Longitude = worldSpace.getLongitude();


                            Geocoder geocoder;
                            List<Address> addresses;
                            Locale locale = Locale.getDefault();
                            geocoder = new Geocoder(MainActivity.this, locale);
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String city = addresses.get(0).getLocality();
                                String country = addresses.get(0).getCountryName();
                                textView4.setText(city);
                                textView5.setText(country);


                            } catch (IOException e) {
                                e.printStackTrace();
                                // Log.d(TAG, String.valueOf(e));
                            }

                            //Sunset_SunRise(String.valueOf(_Latitude), String.valueOf(_Longitude));

                            Temp_Test(String.valueOf(_Latitude), String.valueOf(_Longitude));
                            Temp(String.valueOf(_Latitude), String.valueOf(_Longitude));
                        }
                    });


        }


    }


    public void Refresh() {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);

    }


   /* public void Sunset_SunRise(String Latitude, String Longitude) {


        Log.d(TAG, Latitude + "   " + Longitude + "in Sunset_Sunrise");


        //String URL ="https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400";

        String URL_new = "https://api.sunrise-sunset.org/json?lat=" + Latitude + "&lng=" + Longitude;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_new, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    Log.d(TAG, "In Try of sunset and sunrise");
                    response = response.getJSONObject("results");


                    String SunRise_time = response.getString("sunrise");
                    String SunSet_Time = response.getString("sunset");


                    //SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm:ss");

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("H:mm:ss");

                    Log.d(TAG, SunRise_time + "   " + SunSet_Time);


                    change(SunRise_time, SunSet_Time, simpleDateFormat, simpleDateFormat1);

                    Log.d("MainActivity", SunRise_time + "In SunRise Time");
                    Log.d("MainActivity", SunSet_Time + "In SunSet Time");


                } catch (JSONException e) {

                    Toast.makeText(MainActivity.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                Log.d(TAG, String.valueOf(error) + "   IN SunSet Error");

            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(

                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        requestQueue.add(jsonObjectRequest);

    }*/


    public void Temp(String lat, String lon) {

        String URL_NEW = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=3c414449be05bed5857c0cef0686220f";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_NEW, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    response = response.getJSONObject("main");

                    String temp = response.getString("temp");
                    String pressure = response.getString("pressure");
                    String humidity = response.getString("humidity");
                    String max_temp = response.getString("temp_max");

                    float _temp = Float.parseFloat(temp);

                    float new_temp = (float) (_temp - 273.15);

                    int __temp = (int) new_temp;

                    float temp_max = Float.parseFloat(max_temp);

                    float new_max_temp = (float) (temp_max - 273.15);

                    int __max_temp = (int) new_max_temp;

                    //String formattedString = String.format("%.00f", new_temp);


                    textview.setText(String.valueOf(__temp));
                    textView2.setText(String.valueOf(__max_temp));
                    textView3.setText(humidity);
                    textView1.setText(pressure);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue2.add(jsonObjectRequest);
    }

    public void Temp_Test(String lat, String lon) {


        //Double _lon = worldSpace.getLongitude();
        //Double _lat = worldSpace.getLatitude();

        //String lat = String.valueOf(_lat);
        //String lon = String.valueOf(_lon);


        // Log.d(TAG, lat + "  " + lon);


        String URL_NEW = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=3c414449be05bed5857c0cef0686220f";

        //String URL_OLD="http://www.myweather2.com/developer/forecast.ashx?uac=<gmPK8O2bx>&query="+lat+","+lon+"&temp_unit=c";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_NEW, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //  Log.d(TAG, String.valueOf(response));

                try {

                    JSONArray jsonArray = response.getJSONArray("weather");

                    for (int i = 0; i <= jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        //JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        String main = o.getString("main");
                        String icon = o.getString("icon");
                        String id = o.getString("id");

                        //Log.d("MainActivity", main + "  " + icon);


                        Condition(icon, id);


                        response = response.getJSONObject("sys");

                        String sun_rise = response.getString("sunrise");

                        String sun_set = response.getString("sunset");

                        String time = new SimpleDateFormat("HH:mm:ss a").format(new Date(Integer.valueOf(sun_rise) * 1000L));

                        String set_Time = new SimpleDateFormat("HH:mm:ss").format(new Date(Integer.valueOf(sun_set) * 1000L));

                        try {
                            // String _24HourTime = "22:15";
                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm:ss a");
                            Date _24HourDt = _24HourSDF.parse(set_Time);
                            // System.out.println(_24HourDt);
                            // System.out.println(_12HourSDF.format(_24HourDt));

                            textView7.setText(time);

                            textView8.setText(_12HourSDF.format(_24HourDt));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }




                       /* response=response.getJSONObject("main");

                        String temprature=response.getString("temp");
                        String pressure=response.getString("pressure");
                        String humidity=response.getString("humidity");

                        Log.d("MainActivity","temperature is "+ temprature);

                        textview.setText(temprature);
                        textView3.setText(humidity);
                        textView1.setText(pressure);*/


                        // Log.d(TAG, time);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        requestQueue1.add(jsonObjectRequest);

    }


   /* public void change(String rise_time, String Set_Time, SimpleDateFormat simpleDateFormat, SimpleDateFormat simpleDateFormat1) {


        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));


        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        simpleDateFormat1.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

        Date date = null;
        Date date1 = null;

        try {


            Log.d(TAG, "In Change Method");


            date = simpleDateFormat.parse(rise_time);
            date1 = simpleDateFormat1.parse(Set_Time);

        } catch (ParseException e) {

            Log.d(TAG, String.valueOf(e) + " In Change Error  ");

            e.printStackTrace();
        }


        String formattedDate = simpleDateFormat.format(date);
        String formate_date = simpleDateFormat1.format(date1);


        //textView7.setText(formattedDate + " AM");
        //textView8.setText(formate_date + " PM");
        Log.d(TAG, formattedDate + formate_date + "Formated Time ");


    }*/


    public void Condition(String icon, String id) {

        if (icon.equals("11d")) {

            textView6.setText("Stormy");
            imageView.setBackgroundResource(R.drawable.storm);
            imageView1.setBackgroundResource(R.drawable.limeade);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500332247", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500332247", MainActivity.this, false, asyncPlayer);


                }
            });


        } else if (icon.equals("09d")) {


            textView6.setText("Rainy");
            imageView.setBackgroundResource(R.drawable.rainy_day);
            imageView1.setBackgroundResource(R.drawable.ali);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, false, asyncPlayer);


                }
            });


        } else if (icon.equals("10d")) {


            textView6.setText("Rainy");
            imageView.setBackgroundResource(R.drawable.rainy_day);
            imageView1.setBackgroundResource(R.drawable.ali);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (icon.equals("13d")) {


            textView6.setText("Snowy");
            imageView.setBackgroundResource(R.drawable.cold);
            imageView1.setBackgroundResource(R.drawable.dimigo);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500332222", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500332222", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (icon.equals("50d")) {


            textView6.setText("Haze");
            //imageView.setImageResource(R.drawable.haze);


            imageView.setBackgroundResource(R.drawable.haze);
            imageView1.setBackgroundResource(R.drawable.lush);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);

                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514",true);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500326599", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500326599", MainActivity.this, false, asyncPlayer);


                }
            });


        } else if (icon.equals("01d")) {

            textView6.setText("Clear");


            imageView.setBackgroundResource(R.drawable.sunny_day);


            imageView1.setBackgroundResource(R.drawable.sun_on_the_horizon);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });
        } else if (icon.equals("01n")) {

            textView6.setText("Clear");


            imageView.setBackgroundResource(R.drawable.night);


            imageView1.setBackgroundResource(R.drawable.sun_on_the_horizon);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (icon.equals("02d")) {


            textView6.setText("Cloudy");


            imageView.setBackgroundResource(R.drawable.cloudy_day);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (icon.equals("02n")) {


            textView6.setText("Cloudy");

            imageView.setBackgroundResource(R.drawable.dark_night);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (id.equals("903")) {

            textView6.setText("Very Cold");
            imageView.setBackgroundResource(R.drawable.hail_storm);
            imageView1.setBackgroundResource(R.drawable.sweet_morning);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (id.equals("905")) {
            textView6.setText("Windy");
            imageView.setBackgroundResource(R.drawable.windy_day);
            imageView1.setBackgroundResource(R.drawable.blush);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521", MainActivity.this, false, asyncPlayer);


                }
            });

        } else if (icon.equals("03d")) {

            textView6.setText("scattered clouds");

            imageView.setBackgroundResource(R.drawable.partialy_cloudy);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });


        } else if (icon.equals("03n")) {


            textView6.setText("scattered clouds");

            imageView.setBackgroundResource(R.drawable.dark_night);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });


        } else if (icon.equals("04d")) {

            textView6.setText("overcast clouds");

            imageView.setBackgroundResource(R.drawable.partialy_cloudy);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });
        } else if (icon.equals("04n")) {

            textView6.setText("overcast clouds");

            imageView.setBackgroundResource(R.drawable.dark_night);


            imageView1.setBackgroundResource(R.drawable.digital_water);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(MainActivity.this, "Buffering...", Toast.LENGTH_LONG).show();
                    imageButton.setVisibility(View.INVISIBLE);
                    imageButton1.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",true,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, true, asyncPlayer);


                }
            });


            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageButton1.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                    // music.clear("https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330521",false,mediaPlayer);
                    music.BackGround_play("MainActivity", "https://api.jamendo.com/v3.0/playlists/file/?client_id=0c1d77dd&id=500330514", MainActivity.this, false, asyncPlayer);


                }
            });


        }
    }


}
