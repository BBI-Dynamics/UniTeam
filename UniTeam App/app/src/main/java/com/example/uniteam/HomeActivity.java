package com.example.uniteam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Pantalla principal con menú de cuenta y gestión de tareas. */
public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private Usuario usuario;

    private ShapeableImageView ivAvatar;
    private TextView tvSaludo, tvCorreo;

    // Nuevos componentes de tareas
    private TabLayout tabTareas;
    private RecyclerView rvTareas;
    private LinearLayout layoutVacio;
    private TextView tvVacioTitulo, tvVacioSubtitulo;
    private TareaAdapter adapter;
    private boolean viendoCompletadas = false;

    private final ActivityResultLauncher<PickVisualMediaRequest> selectorFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) guardarFoto(uri);
            });

    /** Acción de un diálogo: devuelve el mensaje de error, o null si todo salió bien. */
    private interface Accion { String ejecutar(String valor); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);
        usuario = db.obtenerPorId(session.getUserId());
        if (usuario == null) { // sesión inválida
            cerrarSesionYSalir();
            return;
        }

        // La barra superior es verde/teal: íconos de estado claros.
        View root = findViewById(R.id.root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(false);

        View appBar = findViewById(R.id.appBar);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            appBar.setPadding(b.left, b.top, b.right, 0);
            findViewById(R.id.contenido).setPadding(b.left, 0, b.right, b.bottom);
            return insets;
        });

        ivAvatar = findViewById(R.id.ivAvatar);
        tvSaludo = findViewById(R.id.tvSaludo);
        tvCorreo = findViewById(R.id.tvCorreo);
        ivAvatar.setOnClickListener(this::mostrarMenuCuenta);

        inicializarTareas();
        pintarUsuario();
        cargarTareas();
    }

    // ---------------------------------------------------------------- TAREAS (NUEVO)

    private void inicializarTareas() {
        tabTareas = findViewById(R.id.tabTareas);
        rvTareas = findViewById(R.id.rvTareas);
        layoutVacio = findViewById(R.id.layoutVacio);
        tvVacioTitulo = findViewById(R.id.tvVacioTitulo);
        tvVacioSubtitulo = findViewById(R.id.tvVacioSubtitulo);
        ExtendedFloatingActionButton fabNuevaTarea = findViewById(R.id.fabNuevaTarea);

        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TareaAdapter(tarea -> {
            boolean nuevoEstado = !tarea.isCompletada();
            if (db.cambiarEstadoTarea(tarea.getId(), nuevoEstado)) {
                toast(nuevoEstado ? "Tarea marcada como completada" : "Tarea movida a pendientes");
                cargarTareas();
            }
        });
        rvTareas.setAdapter(adapter);

        tabTareas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viendoCompletadas = (tab.getPosition() == 1);
                cargarTareas();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        fabNuevaTarea.setOnClickListener(v -> dialogoNuevaTarea());
    }

    private void cargarTareas() {
        List<Tarea> lista = db.obtenerTareas(viendoCompletadas);
        adapter.actualizarLista(lista);

        if (lista.isEmpty()) {
            rvTareas.setVisibility(View.GONE);
            layoutVacio.setVisibility(View.VISIBLE);
            if (viendoCompletadas) {
                tvVacioTitulo.setText("Sin tareas completadas");
                tvVacioSubtitulo.setText("Aquí aparecerán las tareas que vayas terminando.");
            } else {
                tvVacioTitulo.setText("Aún no hay tareas pendientes");
                tvVacioSubtitulo.setText("Presiona '+ Nueva tarea' para agregar una.");
            }
        } else {
            rvTareas.setVisibility(View.VISIBLE);
            layoutVacio.setVisibility(View.GONE);
        }
    }

    private void dialogoNuevaTarea() {
        View v = getLayoutInflater().inflate(R.layout.dialog_nueva_tarea, null);
        TextInputLayout tilTitulo = v.findViewById(R.id.tilTareaTitulo);
        TextInputLayout tilMateria = v.findViewById(R.id.tilTareaMateria);
        TextInputEditText etTitulo = v.findViewById(R.id.etTareaTitulo);
        TextInputEditText etMateria = v.findViewById(R.id.etTareaMateria);
        TextInputEditText etDescripcion = v.findViewById(R.id.etTareaDescripcion);
        RadioGroup rgImportancia = v.findViewById(R.id.rgImportancia);

        AlertDialog d = new MaterialAlertDialogBuilder(this)
                .setTitle("Crear nueva tarea")
                .setView(v)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear tarea", null)
                .create();

        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            tilTitulo.setError(null);
            tilMateria.setError(null);

            String titulo = etTitulo.getText().toString().trim();
            String materia = etMateria.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (titulo.isEmpty()) {
                tilTitulo.setError("Escribe el título de la tarea");
                return;
            }
            if (materia.isEmpty()) {
                tilMateria.setError("Escribe la materia");
                return;
            }

            int selectedId = rgImportancia.getCheckedRadioButtonId();
            RadioButton rb = v.findViewById(selectedId);
            String importancia = (rb != null) ? rb.getText().toString() : "Media";

            long res = db.crearTarea(usuario.getId(), titulo, materia, descripcion, importancia);
            if (res == -1) {
                toast("No se pudo crear la tarea");
                return;
            }

            toast("Tarea creada");
            if (viendoCompletadas && tabTareas.getTabAt(0) != null) {
                tabTareas.getTabAt(0).select();
            } else {
                cargarTareas();
            }
            d.dismiss();
        }));
        d.show();
    }

    // ---------------------------------------------------------------- UI (INTACTA)

    private void pintarUsuario() {
        tvSaludo.setText("¡Hola, " + usuario.getNombre() + "!");
        tvCorreo.setText(usuario.getEmail());

        Bitmap foto = null;
        String path = usuario.getFotoPath();
        if (path != null && new File(path).exists()) {
            foto = BitmapFactory.decodeFile(path);
        }
        if (foto != null) {
            ivAvatar.setPadding(dp(1), dp(1), dp(1), dp(1));
            ivAvatar.setImageBitmap(foto);
        } else {
            ivAvatar.setPadding(dp(8), dp(8), dp(8), dp(8));
            ivAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void recargarUsuario() {
        Usuario u = db.obtenerPorId(usuario.getId());
        if (u != null) usuario = u;
        pintarUsuario();
        cargarTareas();
    }

    private void mostrarMenuCuenta(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenuInflater().inflate(R.menu.menu_cuenta, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.accion_foto) {
                selectorFoto.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            } else if (id == R.id.accion_nombre) {
                dialogoCambiarNombre();
            } else if (id == R.id.accion_email) {
                dialogoCambiarEmail();
            } else if (id == R.id.accion_password) {
                dialogoCambiarPassword();
            } else if (id == R.id.accion_cerrar_sesion) {
                confirmarCerrarSesion();
            } else if (id == R.id.accion_borrar) {
                dialogoBorrarCuenta();
            }
            return true;
        });
        menu.show();
    }

    // ---------------------------------------------------------------- FOTO (INTACTA)

    private void guardarFoto(Uri uri) {
        try {
            Bitmap bmp = decodificar(uri, 512);
            File dir = new File(getFilesDir(), "perfiles");
            if (!dir.exists() && !dir.mkdirs()) throw new IOException("No se pudo crear carpeta");
            File destino = new File(dir, "perfil_" + usuario.getId() + "_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream out = new FileOutputStream(destino)) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
            String anterior = usuario.getFotoPath();
            db.actualizarFoto(usuario.getId(), destino.getAbsolutePath());
            if (anterior != null) //noinspection ResultOfMethodCallIgnored
                new File(anterior).delete();
            runOnUiThread(() -> {
                recargarUsuario();
                toast("Foto de perfil actualizada");
            });
        } catch (Exception e) {
            runOnUiThread(() -> toast("No se pudo cargar la imagen"));
        }
    }

    /** Lee la imagen reducida (máx. 'max' px) y corrige su rotación EXIF. */
    private Bitmap decodificar(Uri uri, int max) throws IOException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, o);
        }
        int muestra = 1;
        while (o.outWidth / muestra > max * 2 || o.outHeight / muestra > max * 2) muestra *= 2;
        o.inJustDecodeBounds = false;
        o.inSampleSize = muestra;

        Bitmap b;
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            b = BitmapFactory.decodeStream(in, null, o);
        }
        if (b == null) throw new IOException("Imagen ilegible");

        int rotacion = 0;
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in != null) {
                int ori = new ExifInterface(in).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (ori == ExifInterface.ORIENTATION_ROTATE_90) rotacion = 90;
                else if (ori == ExifInterface.ORIENTATION_ROTATE_180) rotacion = 180;
                else if (ori == ExifInterface.ORIENTATION_ROTATE_270) rotacion = 270;
            }
        } catch (IOException ignorado) { /* sin EXIF */ }

        float escala = Math.min(1f, (float) max / Math.max(b.getWidth(), b.getHeight()));
        Matrix m = new Matrix();
        m.postRotate(rotacion);
        m.postScale(escala, escala);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
    }

    // ---------------------------------------------------------------- NOMBRE / CORREO (INTACTA)

    private void dialogoCambiarNombre() {
        mostrarDialogoTexto("Cambiar nombre", "Nombre", usuario.getNombre(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS, false, "Guardar",
                valor -> {
                    String nombre = valor.trim();
                    if (nombre.isEmpty()) return "El nombre no puede estar vacío";
                    if (!db.actualizarNombre(usuario.getId(), nombre)) return "No se pudo guardar";
                    recargarUsuario();
                    toast("Nombre actualizado");
                    return null;
                });
    }

    private void dialogoCambiarEmail() {
        mostrarDialogoTexto("Cambiar correo", "Nuevo correo", usuario.getEmail(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, false, "Guardar",
                valor -> {
                    String email = valor.trim();
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Correo no válido";
                    if (db.emailExiste(email, usuario.getId())) return "Ese correo ya está en uso";
                    if (!db.actualizarEmail(usuario.getId(), email)) return "No se pudo guardar";
                    recargarUsuario();
                    toast("Correo actualizado");
                    return null;
                });
    }

    // ---------------------------------------------------------------- CONTRASEÑA (INTACTA)

    private void dialogoCambiarPassword() {
        View v = getLayoutInflater().inflate(R.layout.dialog_password, null);
        TextInputLayout tilActual = v.findViewById(R.id.tilActual);
        TextInputLayout tilNueva = v.findViewById(R.id.tilNueva);
        TextInputLayout tilConfirmar = v.findViewById(R.id.tilConfirmar);
        TextInputEditText etActual = v.findViewById(R.id.etActual);
        TextInputEditText etNueva = v.findViewById(R.id.etNueva);
        TextInputEditText etConfirmar = v.findViewById(R.id.etConfirmar);

        AlertDialog d = new MaterialAlertDialogBuilder(this)
                .setTitle("Cambiar contraseña")
                .setView(v)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", null)
                .create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            tilActual.setError(null);
            tilNueva.setError(null);
            tilConfirmar.setError(null);
            String actual = etActual.getText().toString();
            String nueva = etNueva.getText().toString();
            String conf = etConfirmar.getText().toString();

            if (!db.verificarPassword(usuario.getId(), actual)) {
                tilActual.setError("Contraseña actual incorrecta"); return;
            }
            if (nueva.length() < 6) { tilNueva.setError("Mínimo 6 caracteres"); return; }
            if (!nueva.equals(conf)) { tilConfirmar.setError("No coinciden"); return; }
            if (!db.cambiarPassword(usuario.getId(), nueva)) {
                tilNueva.setError("No se pudo guardar"); return;
            }
            toast("Contraseña actualizada");
            d.dismiss();
        }));
        d.show();
    }

    // ---------------------------------------------------------------- SESIÓN / BORRAR (INTACTA)

    private void confirmarCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Quieres salir de tu cuenta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (d, w) -> cerrarSesionYSalir())
                .show();
    }

    private void dialogoBorrarCuenta() {
        mostrarDialogoTexto("Borrar cuenta",
                "Escribe tu contraseña para confirmar",
                null,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, true, "Borrar",
                valor -> {
                    if (!db.verificarPassword(usuario.getId(), valor)) return "Contraseña incorrecta";
                    String foto = usuario.getFotoPath();
                    if (!db.eliminarCuenta(usuario.getId())) return "No se pudo borrar la cuenta";
                    if (foto != null) //noinspection ResultOfMethodCallIgnored
                        new File(foto).delete();
                    cerrarSesionYSalir();
                    return null;
                });
    }

    private void cerrarSesionYSalir() {
        session.cerrar();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // ---------------------------------------------------------------- AYUDAS (INTACTA)

    /** Diálogo con un campo de texto. Solo se cierra si la acción no devuelve error. */
    private void mostrarDialogoTexto(String titulo, String hint, String inicial, int inputType,
                                     boolean password, String textoBoton, Accion accion) {
        View v = getLayoutInflater().inflate(R.layout.dialog_input, null);
        TextInputLayout til = v.findViewById(R.id.til);
        TextInputEditText et = v.findViewById(R.id.et);
        til.setHint(hint);
        et.setInputType(inputType);
        if (password) til.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        if (inicial != null) {
            et.setText(inicial);
            et.setSelection(inicial.length());
        }

        AlertDialog d = new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setView(v)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(textoBoton, null)
                .create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            til.setError(null);
            String error = accion.ejecutar(et.getText().toString());
            if (error == null) d.dismiss();
            else til.setError(error);
        }));
        d.show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}