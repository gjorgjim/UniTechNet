package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.login_activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.NavigationActivity;

public class LoginActivity extends AppCompatActivity {

  private AppCompatImageView logoIv;
  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private TextInputLayout passwordIl;
  private AppCompatEditText passwordEt;
  private AppCompatButton signInBtn;
  private AppCompatTextView incorrectInfoTv;

  private AuthenticationService authenticationService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    authenticationService = AuthenticationService.getInstance();
    authenticationService.setMyActivity(this);

    logoIv = findViewById(R.id.logoIv);
    emailIl = findViewById(R.id.emailIl);
    emailEt = findViewById(R.id.emailEt);
    passwordIl = findViewById(R.id.passwordIl);
    passwordEt = findViewById(R.id.passwordEt);
    signInBtn = findViewById(R.id.signInBtn);
    incorrectInfoTv = findViewById(R.id.incorrectInfoTv);

    Glide.with(this)
            .load(R.drawable.utn_logo)
            .fitCenter()
            .into(logoIv);

    signInBtn.setOnClickListener(v -> {
      if(incorrectInfoTv.getVisibility() == View.VISIBLE) {
        incorrectInfoTv.setVisibility(View.INVISIBLE);
      }
      if(validateInput()) {
        FirebaseUser user = authenticationService.signIn(
          emailEt.getText().toString().trim(),
          passwordEt.getText().toString().trim()
        );
        if(user != null) {
          startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
        } else {
          incorrectInfoTv.setVisibility(View.VISIBLE);
          emailEt.setText(getString(R.string.empty_string));
          passwordEt.setText(getString(R.string.empty_string));
        }
      }
    });
  }

  boolean validateInput() {
    return Validator.validateEmail(emailIl, emailEt, this)
      && Validator.validateInput(passwordIl, passwordEt, this);
  }
}
