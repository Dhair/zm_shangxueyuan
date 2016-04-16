package com.zm.shangxueyuan.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/16 21:22
 * Email: deng.shengjin@zuimeia.com
 */
public class HotWordsModel extends BaseModel implements Serializable {
    private String mWord;
    private long mId;

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public static String[] parseNews(String jsonStr) {
        try {
            String[] newsArr = new String[2];
            JSONObject jsonObject = new JSONObject(jsonStr);
            jsonObject = jsonObject.optJSONObject("news");
            newsArr[0] = jsonObject.optString("title");
            newsArr[1] = jsonObject.optString("link");
            return newsArr;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static List<HotWordsModel> parseHotWords(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray hotWordsArr = jsonObject.optJSONArray("hot_words");
            if (hotWordsArr == null || hotWordsArr.length() == 0) {
                return null;
            }
            List<HotWordsModel> hotList = new LinkedList<>();
            for (int i = 0, len = hotWordsArr.length(); i < len; i++) {
                JSONObject hotObj = hotWordsArr.optJSONObject(i);
                HotWordsModel navModel = new HotWordsModel();
                navModel.setWord(hotObj.optString("word"));
                navModel.setId(hotObj.optLong("id"));
                hotList.add(navModel);
            }
            return hotList;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
