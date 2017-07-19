package com.example.phuc.navigation.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuc on 7/18/17.
 */

public class QueueArray {
    private int mode;
    private TTS tts = new TTS();
    private List<String> arr = new ArrayList<>();


    public int getTotal()
    {
       return arr.size();
    }
    public void AddQeue (int mode, String element)
    {
        this.mode = mode;
        arr.add(element);

    }

    public void dequeue()
    {
        if (arr.size() == 0)
        {
            return;
        }
        if (arr.size()>0)
        {
            tts.speak(mode, arr.get(0));
            arr.remove(0);
            dequeue();
        }

    }


}