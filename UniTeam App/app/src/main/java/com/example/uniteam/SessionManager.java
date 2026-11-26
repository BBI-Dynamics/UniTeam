package com.example.uniteam;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda solo el id del usuario con sesión activa. */
public class SessionManager {
    private static final String PREFS = "uniteam_session";
    private static final String KEY_USER_ID = "user_id";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardar(long userId) { prefs.edit().putLong(KEY_USER_ID, userId).apply(); }
    public long getUserId() { return prefs.getLong(KEY_USER_ID, -1); }
    public void cerrar() { prefs.edit().remove(KEY_USER_ID).apply(); }
}
