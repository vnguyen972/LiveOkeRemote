package com.vnguyen.liveokeremote.helper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vu Nguyen on 12/7/2015.
 */
public class YouTubeHelper {

    public static ConcurrentHashMap<String, String> ytNumPages(int total, int maxSize) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        int numKeys = 0;
        if (total % maxSize != 0) {
            numKeys = (total / maxSize) + 1;
        } else {
            numKeys = (total / maxSize);
        }
        LogHelper.d("value: " + numKeys);
        int size = maxSize;
        int num = total;
        for (int i = 1; i <= numKeys; i++)  {
            num = num - size;
            if (num < 0) {
                if (i < 10) {
                    map.put("0"+i, (num+maxSize)+"");
                } else {
                    map.put(i+"",(num+maxSize)+"");
                }
            } else {
                if (i < 10) {
                    map.put("0"+i,size+"");
                } else {
                    map.put(i+"",size+"");
                }
            }
        }
        return map;
    }

}
