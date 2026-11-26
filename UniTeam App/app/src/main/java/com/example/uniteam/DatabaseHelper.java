package com.example.uniteam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Base de datos local SQLite de UniTeam.
 * Las contraseñas NUNCA se guardan en texto plano: se guarda un hash PBKDF2 + sal aleatoria.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "uniteam.db";
    private static final int DB_VERSION = 2;

    // Tabla Usuarios (INTACTA)
    private static final String TABLA = "usuarios";
    private static final String COL_ID = "id";
    private static final String COL_NOMBRE = "nombre";
    private static final String COL_EMAIL = "email";
    private static final String COL_HASH = "password_hash";
    private static final String COL_SALT = "salt";
    private static final String COL_FOTO = "foto_path";

    // Tabla Tareas (NUEVA)
    private static final String TABLA_TAREAS = "tareas";
    private static final String T_ID = "id";
    private static final String T_USUARIO_ID = "usuario_id";
    private static final String T_TITULO = "titulo";
    private static final String T_MATERIA = "materia";
    private static final String T_DESCRIPCION = "descripcion";
    private static final String T_IMPORTANCIA = "importancia";
    private static final String T_COMPLETADA = "completada";

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLA + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOMBRE + " TEXT NOT NULL, "
                + COL_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_HASH + " TEXT NOT NULL, "
                + COL_SALT + " TEXT NOT NULL, "
                + COL_FOTO + " TEXT, "
                + "creado_en DATETIME DEFAULT CURRENT_TIMESTAMP)");

        crearTablaTareas(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            crearTablaTareas(db);
        }
    }

    private void crearTablaTareas(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_TAREAS + " ("
                + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + T_USUARIO_ID + " INTEGER NOT NULL, "
                + T_TITULO + " TEXT NOT NULL, "
                + T_MATERIA + " TEXT NOT NULL, "
                + T_DESCRIPCION + " TEXT, "
                + T_IMPORTANCIA + " TEXT NOT NULL, "
                + T_COMPLETADA + " INTEGER NOT NULL DEFAULT 0, "
                + "creado_en DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    // ---------------------------------------------------------------- CREAR / LOGIN

    /** @return id del nuevo usuario, o -1 si falló (por ejemplo, correo repetido). */
    public synchronized long registrar(String nombre, String email, String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        ContentValues v = new ContentValues();
        v.put(COL_NOMBRE, nombre.trim());
        v.put(COL_EMAIL, normalizar(email));
        v.put(COL_HASH, hash(password, salt));
        v.put(COL_SALT, Base64.encodeToString(salt, Base64.NO_WRAP));
        return getWritableDatabase().insert(TABLA, null, v);
    }

    /** @return el usuario si correo y contraseña son correctos; null si no. */
    public synchronized Usuario iniciarSesion(String email, String password) {
        Cursor c = getReadableDatabase().query(TABLA, null, COL_EMAIL + "=?",
                new String[]{normalizar(email)}, null, null, null);
        try {
            if (c.moveToFirst() && coincide(c, password)) {
                return mapear(c);
            }
        } finally {
            c.close();
        }
        return null;
    }

    public synchronized Usuario obtenerPorId(long id) {
        Cursor c = getReadableDatabase().query(TABLA, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? mapear(c) : null;
        } finally {
            c.close();
        }
    }

    public synchronized boolean emailExiste(String email) {
        return emailExiste(email, -1);
    }

    /** ¿Existe el correo en otro usuario distinto a excluirId? */
    public synchronized boolean emailExiste(String email, long excluirId) {
        Cursor c = getReadableDatabase().query(TABLA, new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_ID + "<>?",
                new String[]{normalizar(email), String.valueOf(excluirId)}, null, null, null);
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }

    // ---------------------------------------------------------------- ACTUALIZAR

    public synchronized boolean actualizarNombre(long id, String nombre) {
        return actualizar(id, COL_NOMBRE, nombre.trim());
    }

    public synchronized boolean actualizarEmail(long id, String email) {
        return actualizar(id, COL_EMAIL, normalizar(email));
    }

    public synchronized boolean actualizarFoto(long id, String path) {
        return actualizar(id, COL_FOTO, path);
    }

    public synchronized boolean verificarPassword(long id, String password) {
        Cursor c = getReadableDatabase().query(TABLA, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() && coincide(c, password);
        } finally {
            c.close();
        }
    }

    public synchronized boolean cambiarPassword(long id, String nueva) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        ContentValues v = new ContentValues();
        v.put(COL_HASH, hash(nueva, salt));
        v.put(COL_SALT, Base64.encodeToString(salt, Base64.NO_WRAP));
        return getWritableDatabase().update(TABLA, v, COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public synchronized boolean eliminarCuenta(long id) {
        getWritableDatabase().delete(TABLA_TAREAS, T_USUARIO_ID + "=?",
                new String[]{String.valueOf(id)});
        return getWritableDatabase().delete(TABLA, COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // ---------------------------------------------------------------- TAREAS (NUEVO)

    public synchronized long crearTarea(long usuarioId, String titulo, String materia,
                                        String descripcion, String importancia) {
        ContentValues v = new ContentValues();
        v.put(T_USUARIO_ID, usuarioId);
        v.put(T_TITULO, titulo.trim());
        v.put(T_MATERIA, materia.trim());
        v.put(T_DESCRIPCION, descripcion.trim());
        v.put(T_IMPORTANCIA, importancia);
        v.put(T_COMPLETADA, 0);
        return getWritableDatabase().insert(TABLA_TAREAS, null, v);
    }

    public synchronized List<Tarea> obtenerTareas(boolean completadas) {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT t.*, u." + COL_NOMBRE + " AS creador_nombre, u." + COL_EMAIL + " AS creador_email "
                + "FROM " + TABLA_TAREAS + " t "
                + "LEFT JOIN " + TABLA + " u ON t." + T_USUARIO_ID + " = u." + COL_ID + " "
                + "WHERE t." + T_COMPLETADA + " = ? "
                + "ORDER BY t." + T_ID + " DESC";

        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{completadas ? "1" : "0"});
        try {
            while (c.moveToNext()) {
                String nombre = c.getString(c.getColumnIndexOrThrow("creador_nombre"));
                String email = c.getString(c.getColumnIndexOrThrow("creador_email"));
                String creador = (nombre != null) ? nombre + " (" + email + ")" : "Cuenta eliminada";

                lista.add(new Tarea(
                        c.getLong(c.getColumnIndexOrThrow(T_ID)),
                        c.getLong(c.getColumnIndexOrThrow(T_USUARIO_ID)),
                        creador,
                        c.getString(c.getColumnIndexOrThrow(T_TITULO)),
                        c.getString(c.getColumnIndexOrThrow(T_MATERIA)),
                        c.getString(c.getColumnIndexOrThrow(T_DESCRIPCION)),
                        c.getString(c.getColumnIndexOrThrow(T_IMPORTANCIA)),
                        c.getInt(c.getColumnIndexOrThrow(T_COMPLETADA)) == 1
                ));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public synchronized boolean cambiarEstadoTarea(long tareaId, boolean completada) {
        ContentValues v = new ContentValues();
        v.put(T_COMPLETADA, completada ? 1 : 0);
        return getWritableDatabase().update(TABLA_TAREAS, v, T_ID + "=?",
                new String[]{String.valueOf(tareaId)}) > 0;
    }

    // ---------------------------------------------------------------- INTERNOS

    private boolean actualizar(long id, String columna, String valor) {
        ContentValues v = new ContentValues();
        v.put(columna, valor);
        return getWritableDatabase().update(TABLA, v, COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    private Usuario mapear(Cursor c) {
        return new Usuario(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NOMBRE)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_FOTO)));
    }

    private boolean coincide(Cursor c, String password) {
        String guardado = c.getString(c.getColumnIndexOrThrow(COL_HASH));
        byte[] salt = Base64.decode(c.getString(c.getColumnIndexOrThrow(COL_SALT)), Base64.NO_WRAP);
        return MessageDigest.isEqual(guardado.getBytes(), hash(password, salt).getBytes());
    }

    private static String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String hash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                    .generateSecret(spec).getEncoded();
            return Base64.encodeToString(h, Base64.NO_WRAP);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}