package com.keniding.dragcoach.soccer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.keniding.dragcoach.soccer.model.Formation;

import java.util.ArrayList;
import java.util.List;

public class FormationManager {
    private static final String FORMATIONS_PREFS = "formations_prefs";
    private final Context context;
    private final Gson gson;

    public FormationManager(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void saveFormation(Formation formation) {
        SharedPreferences prefs = context.getSharedPreferences(FORMATIONS_PREFS, Context.MODE_PRIVATE);
        String formationJson = gson.toJson(formation);
        prefs.edit().putString(formation.getName(), formationJson).apply();
    }

    public Formation loadFormation(String name) {
        SharedPreferences prefs = context.getSharedPreferences(FORMATIONS_PREFS, Context.MODE_PRIVATE);
        String formationJson = prefs.getString(name, null);
        if (formationJson != null) {
            return gson.fromJson(formationJson, Formation.class);
        }
        return null;
    }

    public List<String> getAllFormationNames() {
        SharedPreferences prefs = context.getSharedPreferences(FORMATIONS_PREFS, Context.MODE_PRIVATE);
        return new ArrayList<>(prefs.getAll().keySet());
    }

    public void deleteFormation(String name) {
        SharedPreferences prefs = context.getSharedPreferences(FORMATIONS_PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(name).apply();
    }
}
