package com.example.phuc.navigation.Controller;

import android.content.Intent;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by phuc on 7/17/17.
 */

public class TTS {

    public static final String viURL = "https://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&tl=vi&client=tw-ob&q=";
    public static final String enURL = "https://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&tl=en&client=tw-ob&q=";

    public void speak (int mode, String text)
    {
        String url;
        url = enURL;
        if (mode == 0) {
            url = viURL;
        }

        url += text;

        MediaPlayer player = new MediaPlayer();
        if (url != null) {
            try {
                player.setDataSource(url);
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
    }

}
