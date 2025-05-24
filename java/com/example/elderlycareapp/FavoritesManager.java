package com.example.elderlycareapp;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashSet;
import java.util.Set;


public class FavoritesManager {
    private static final String PREFS_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorites";


    public static void saveFavorite(Context context, String videoId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        favorites = new HashSet<>(favorites); // make mutable
        favorites.add(videoId);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }


    public static void removeFavorite(Context context, String videoId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        favorites = new HashSet<>(favorites); // make mutable
        favorites.remove(videoId);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }


    public static boolean isFavorite(Context context, String videoId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favorites.contains(videoId);
    }


    public static Set<String> getFavoriteVideoIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }
}