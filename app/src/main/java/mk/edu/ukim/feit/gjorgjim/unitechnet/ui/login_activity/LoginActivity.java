package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.login_activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.AuthenticationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.NavigationActivity;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.ForgotPasswordDialog;

public class LoginActivity extends AppCompatActivity {

  private AppCompatImageView logoIv;
  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private TextInputLayout passwordIl;
  private AppCompatEditText passwordEt;
  private AppCompatButton signInBtn;
  private AppCompatTextView forgotPasswordTv;

  private WaitingDialog waitingDialog;

  private AuthenticationService authenticationService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    authenticationService = AuthenticationService.getInstance();

    if(authenticationService.getCurrentUser() != null){
      startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
    }

    waitingDialog = new WaitingDialog(LoginActivity.this);

    logoIv = findViewById(R.id.logoIv);
    emailIl = findViewById(R.id.emailIl);
    emailEt = findViewById(R.id.emailEt);
    passwordIl = findViewById(R.id.passwordIl);
    passwordEt = findViewById(R.id.passwordEt);
    signInBtn = findViewById(R.id.signInBtn);
    forgotPasswordTv = findViewById(R.id.forgotPasswordTv);

    Glide.with(this)
      .load(R.drawable.logo_android_v2)
      .into(logoIv);

    logoIv.setAdjustViewBounds(true);

    passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
          signInBtn.performClick();
        }
        return false;
      }
    });

    signInBtn.setOnClickListener(v -> {
      if(validateInput()) {
        showWaitingDialog();
        authenticationService.signIn(emailEt.getText().toString().trim(), passwordEt.getText().toString().trim(),
          LoginActivity.this, new AuthenticationCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
              hideWaitingDialog();
              startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
            }

            @Override
            public void onFailure(String message) {
              emailEt.setText(getString(R.string.empty_string));
              passwordEt.setText(getString(R.string.empty_string));
              hideWaitingDialog();
              emailEt.requestFocus();
              Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
          });
      }
    });

    forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(LoginActivity.this);
        dialog.show();
      }
    });

  }

  boolean validateInput() {
    return Validator.validateEmail(emailIl, emailEt, this)
      && Validator.validateInput(passwordIl, passwordEt, this);
  }

  private void showWaitingDialog(){
    waitingDialog.showDialog("Logging in...");
  }

  private void hideWaitingDialog(){
    waitingDialog.hideDialog();
  }
}
