package lukaszb.food.detector.fooddetector;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Iterator;

public class ParseJson {

    private final String rawData;
    final String[] food_names = new String[5];
    final int[] percentages = new int[5];
    public ParseJson(String rawData) {
        this.rawData = rawData;
    }

    public void parse(){
        try {
            JSONObject res = new JSONObject(rawData);
            Iterator<String> iterator = res.keys();
            int i = 0;
            while (iterator.hasNext()){

                String key = iterator.next();
                food_names[i] = key;
                percentages[i] = res.getInt(key);
                i ++;
            }
            sort();
        } catch (JSONException e){
            Log.e("PARSE JSON EXCEPTION: ",e.getMessage());
        }
    }

    private void sort(){
        int n = percentages.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (percentages[j] > percentages[j + 1]){
                    int temp = percentages[j];
                    percentages[j] =  percentages[j + 1];
                    percentages[j + 1] = temp;

                    String temp2 = food_names[j];
                    food_names[j] =  food_names[j + 1];
                    food_names[j + 1] = temp2;
                }
            }
        }
    }
}
