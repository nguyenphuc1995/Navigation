package com.example.navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuc on 7/18/17.
 */

public class QueueArray {
    private int mode;
    private static TTS tts = new TTS();
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

    public synchronized void dequeue()
    {
        if (arr.size() == 0)
        {
            return;
        }
        if (arr.size()>0)
        {
            tts.speak(mode,arr.get(0));
            arr.remove(0);

//            long endtime = System.currentTimeMillis() + 4000;
//            while (true)
//            {
//                if (System.currentTimeMillis() > endtime)
//                {
//                    dequeue();
//                    return;
//                }
//            }
            dequeue();
        }

    }


}