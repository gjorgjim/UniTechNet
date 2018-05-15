package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.login_activity;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.WindowManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

public class LoginActivity extends AppCompatActivity {

  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private TextInputLayout passwordIl;
  private AppCompatEditText passwordEt;
  private AppCompatButton signInBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    emailIl = findViewById(R.id.emailIl);
    emailEt = findViewById(R.id.emailEt);
    passwordIl = findViewById(R.id.passwordIl);
    passwordEt = findViewById(R.id.passwordEt);
    signInBtn = findViewById(R.id.signInBtn);

    signInBtn.setOnClickListener(v -> {
      if(validateInput()) {
        //TODO: Sign in the user
      }
    });
  }

  boolean validateInput() {
    if(!validateEmail()) {
      return false;
    }
    if(!validatePassword()) {
      return false;
    }

    return true;
  }

  boolean validateEmail(){
    if(emailEt.getText().toString().trim().isEmpty()) {
      emailIl.setError(getString(R.string.edit_text_empty_error));
      requestFocus(emailEt);
      return false;
    } else {
      if(!isEmailValid(emailEt.getText().toString().trim())) {
        emailIl.setError(getString(R.string.invalid_email_error));
        requestFocus(emailEt);
        return false;
      } else {
        emailIl.setErrorEnabled(false);
      }
    }

    return true;
  }

  public boolean isEmailValid(String email)
  {
    String regExpn =
      "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    CharSequence inputStr = email;

    Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputStr);

    if(matcher.matches())
      return true;
    else
      return false;
  }

  boolean validatePassword() {
    if(passwordEt.getText().toString().trim().isEmpty()) {
      passwordIl.setError(getString(R.string.edit_text_empty_error));
      requestFocus(passwordEt);
      return false;
    } else {
      passwordIl.setErrorEnabled(false);
    }
    return true;
  }

  private void requestFocus(View view) {
    if (view.requestFocus()) {
      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
  }
}
