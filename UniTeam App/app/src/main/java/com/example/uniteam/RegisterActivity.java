package com.example.uniteam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Pantalla de creación de cuenta. */
public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextInputLayout tilNombre, tilEmail, tilPassword, tilConfirmar;
    private TextInputEditText etNombre, etEmail, etPassword, etConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        UiUtils.aplicarInsets(findViewById(R.id.main));

        db = new DatabaseHelper(this);
        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);

        findViewById(R.id.btnRegistrar).setOnClickListener(v -> registrar());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void registrar() {
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirmar = etConfirmar.getText().toString();

        if (nombre.isEmpty()) { tilNombre.setError("Escribe tu nombre"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo no válido"); return;
        }
        if (pass.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres"); return;
        }
        if (!pass.equals(confirmar)) {
            tilConfirmar.setError("Las contraseñas no coinciden"); return;
        }
        if (db.emailExiste(email)) {
            tilEmail.setError("Ya existe una cuenta con este correo"); return;
        }

        long id = db.registrar(nombre, email, pass);
        if (id == -1) {
            Toast.makeText(this, "No se pudo crear la cuenta", Toast.LENGTH_SHORT).show();
            return;
        }
        new SessionManager(this).guardar(id);
        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
