package ca.cmpt276.PracticalParent.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.HistoryLogic;
import ca.cmpt276.PracticalParent.model.Task;

// resource: https://www.youtube.com/watch?v=TsASX0ZK9ak
/**
 * To save child
 */
public class PrefConfig {

    private static final String LIST_KEY = "list_key";
    private static final String ORDER_CHILD_LIST_KEY = "ORDER_CHILD_LIST_KEY";
    private static final String LIST_KEY2 = "list_key2";
    private static final String LIST_KEY3 = "list_key3";
    private static final String LIST_KEY4 = "list_key4";

    public static void writeListInPref(Context context, List<Child> list){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LIST_KEY, jsonString);
        editor.apply();
    }

    public static List<Child> readListFromPref(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(LIST_KEY, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Child>>() {}.getType();
        List<Child> list = gson.fromJson(jsonString, type);
        return list;
    }

    public static void writeChildOrderListInPref(Context context, List<Integer> list){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ORDER_CHILD_LIST_KEY, jsonString);
        editor.apply();
    }

    public static List<Integer> readChildOrderListFromPref(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(ORDER_CHILD_LIST_KEY, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        List<Integer> list = gson.fromJson(jsonString, type);
        return list;
    }


    //To save history
    public static void writeHistoryListInPref(Context context, List<HistoryLogic> list2){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list2);
        SharedPreferences pref2  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref2.edit();
        editor.putString(LIST_KEY2, jsonString);
        editor.apply();
    }

    public static List<HistoryLogic> readHistoryListFromPref(Context context){
        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref2.getString(LIST_KEY2, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HistoryLogic>>() {}.getType();
        List<HistoryLogic> list2 = gson.fromJson(jsonString, type);
        return list2;
    }

    public static void writeTempListInPref(Context context, List<HistoryLogic> list3){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list3);
        SharedPreferences pref3  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref3.edit();
        editor.putString(LIST_KEY3, jsonString);
        editor.apply();
    }

    public static List<HistoryLogic> readTempHistoryListFromPref(Context context){
        SharedPreferences pref3 = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref3.getString(LIST_KEY3, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HistoryLogic>>() {}.getType();
        List<HistoryLogic> list3 = gson.fromJson(jsonString, type);
        return list3;
    }


    //To save task
    public static void writeTaskListInPref(Context context, List<Task> list){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref4  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref4.edit();
        editor.putString(LIST_KEY4, jsonString);
        editor.apply();
    }

    public static List<Task> readTaskListFromPref(Context context){
        SharedPreferences pref4 = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref4.getString(LIST_KEY4, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> list4 = gson.fromJson(jsonString, type);
        return list4;
    }
}
