package com.example.uniteam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Pantalla de inicio de sesión (launcher). */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        // Si ya había sesión iniciada, entra directo.
        long id = session.getUserId();
        if (id != -1) {
            if (db.obtenerPorId(id) != null) {
                irAHome();
                return;
            }
            session.cerrar(); // la cuenta ya no existe
        }

        setContentView(R.layout.activity_main);
        UiUtils.aplicarInsets(findViewById(R.id.main));

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnMainRegister);

        btnLogin.setOnClickListener(v -> iniciarSesion());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void iniciarSesion() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();

        if (email.isEmpty()) { tilEmail.setError("Escribe tu correo"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo no válido"); return;
        }
        if (pass.isEmpty()) { tilPassword.setError("Escribe tu contraseña"); return; }

        Usuario u = db.iniciarSesion(email, pass);
        if (u == null) {
            tilPassword.setError("Correo o contraseña incorrectos");
            return;
        }
        session.guardar(u.getId());
        irAHome();
    }

    private void irAHome() {
        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
