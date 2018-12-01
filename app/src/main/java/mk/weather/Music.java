package mk.weather;

import android.app.Activity;
import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


public class Music {


    public void clear(String URL, boolean isPlaying, MediaPlayer mediaPlayer) {


        // String URL="https://www.youtube.com/watch?v=0LETadzDGOs&list=RD0LETadzDGOs";

        // Toast.makeText(Activity, "In PlayMusic", Toast.LENGTH_LONG).show();


        if (isPlaying && !mediaPlayer.isPlaying()) {

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


            try {
                mediaPlayer.setDataSource(URL);
            } catch (IOException e) {

                e.printStackTrace();
                // Toast.makeText(context, "Slow Internet Connection", Toast.LENGTH_LONG).show();
            }

            try {
                //Toast.makeText(, "Loading...", Toast.LENGTH_LONG).show();
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                // Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
            mediaPlayer.start();


        }



        else {

            mediaPlayer.pause();

        }


    }


    public void BackGround_play(String TAG, String URL, Context context, boolean isPlaying, AsyncPlayer mAsync) {


        if (isPlaying) {

            Log.d(TAG, "Player is starting");
            mAsync.play(context, Uri.parse(URL), false, AudioManager.STREAM_MUSIC);

        } else {

            Log.d(TAG, "Player is Stopping");


            mAsync.stop();

        }


    }


}










