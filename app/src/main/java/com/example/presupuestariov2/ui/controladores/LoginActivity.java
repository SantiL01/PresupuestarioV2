package com.example.presupuestariov2.ui.controladores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.LoginViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.PreferencesManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "UI_Login";

    private LoginViewModel viewModel;

    private EditText etName, etEmail, etPassword, etConfirm;
    private TextView tvFormTitle, tvSubtitle, tvError, tvToggleMode;
    private Button btnLogin;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            PreferencesManager.getInstance().init(getApplicationContext());
            DataManager.getInstance().init(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "init", e);
        }
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Con sesión activa se saltea el login
        if (PreferencesManager.getInstance().hasSession()) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);
        bindViews();

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        observeViewModel();

        btnLogin.setOnClickListener(v -> submit());
        tvToggleMode.setOnClickListener(v -> viewModel.toggleMode());
    }

    private void bindViews() {
        etName = findViewById(R.id.etLoginName);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        etConfirm = findViewById(R.id.etLoginConfirm);
        tvFormTitle = findViewById(R.id.tvFormTitle);
        tvSubtitle = findViewById(R.id.tvLoginSubtitle);
        tvError = findViewById(R.id.tvLoginError);
        tvToggleMode = findViewById(R.id.tvToggleMode);
        btnLogin = findViewById(R.id.btnLogin);
        pbLoading = findViewById(R.id.pbLoading);
    }

    private void observeViewModel() {
        viewModel.getRegisterMode().observe(this, register -> {
            boolean r = register != null && register;
            etName.setVisibility(r ? View.VISIBLE : View.GONE);
            etConfirm.setVisibility(r ? View.VISIBLE : View.GONE);
            tvFormTitle.setText(r ? "Crear cuenta" : "Iniciar sesión");
            tvSubtitle.setText(r ? "Registrate para empezar a presupuestar" : "Iniciá sesión para continuar");
            btnLogin.setText(r ? "Crear cuenta" : "Ingresar");
            tvToggleMode.setText(r ? "¿Ya tenés cuenta? Iniciá sesión" : "¿No tenés cuenta? Registrate");
        });

        viewModel.getStatus().observe(this, state -> {
            boolean loading = state == UiStatus.LOADING;
            pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!loading);
            tvToggleMode.setEnabled(!loading);
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            boolean show = msg != null && !msg.isEmpty();
            tvError.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) tvError.setText(msg);
        });

        viewModel.getLoggedIn().observe(this, ok -> {
            if (ok != null && ok) openMain();
        });
    }

    private void submit() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        Boolean register = viewModel.getRegisterMode().getValue();
        if (register != null && register) {
            viewModel.register(etName.getText().toString(), email, password,
                    etConfirm.getText().toString());
        } else {
            viewModel.login(email, password);
        }
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
