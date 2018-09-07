package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseUser;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.AuthenticationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.KeyboardDelegate;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;

import static android.view.View.GONE;

/**
 * Created by gjmarkov on 07.9.2018.
 */

public class ChangePasswordDialog extends Dialog {

  private AuthenticationService authenticationService;

  private Context context;
  private Activity activity;

  private RelativeLayout mainLayout;
  private RelativeLayout confirmationLayout;
  private TextInputLayout oldPwIl;
  private AppCompatEditText oldPwEt;
  private TextInputLayout newPwIl;
  private AppCompatEditText newPwEt;
  private TextInputLayout repeatPwIl;
  private AppCompatEditText repeatPwEt;
  private AppCompatButton changePwBtn;
  private AppCompatTextView statusTv;
  private ProgressBar progressBar;


  public ChangePasswordDialog(@NonNull Context context) {
    super(context);
    this.context = context;
    activity = (Activity) context;
    authenticationService = AuthenticationService.getInstance();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_change_password);

    mainLayout = findViewById(R.id.mainLayout);
    confirmationLayout = findViewById(R.id.confirmationLayout);
    oldPwIl = findViewById(R.id.oldPwIl);
    oldPwEt = findViewById(R.id.oldPwEt);
    newPwIl = findViewById(R.id.newPwIl);
    newPwEt = findViewById(R.id.newPwEt);
    repeatPwIl = findViewById(R.id.repeatPwIl);
    repeatPwEt = findViewById(R.id.repeatPwEt);
    changePwBtn = findViewById(R.id.changePwBtn);
    statusTv = findViewById(R.id.statusTv);
    progressBar = findViewById(R.id.progressBar);

    changePwBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(validateInput()) {
          KeyboardDelegate.hideSoftKeyboard(activity, repeatPwEt);
          showWaitingStatus();
          authenticationService.changePassword(oldPwEt.getText().toString().trim(), newPwEt.getText().toString().trim(),
            new AuthenticationCallback() {
              @Override
              public void onSuccess(FirebaseUser user) {
                ChangePasswordDialog.this.onSuccess();
              }

              @Override
              public void onFailure(String message) {
                ChangePasswordDialog.this.onFailure(message);
              }
            });
        }
      }
    });
  }

  private void showWaitingStatus() {
    setCancelable(false);
    mainLayout.setVisibility(GONE);
    confirmationLayout.setVisibility(View.VISIBLE);
  }

  private void onSuccess() {
    progressBar.setVisibility(GONE);
    statusTv.setText(context.getString(R.string.password_changed));
    setCancelable(true);
  }

  private void onFailure(String message) {
    statusTv.setText(String.format("%s %s", message, context.getString(R.string.try_again)));
    progressBar.setVisibility(GONE);
    setCancelable(true);
  }

  private boolean validateInput() {
    return Validator.validateInput(oldPwIl, oldPwEt, activity) &&
      Validator.validateInput(newPwIl, newPwEt, activity) &&
      Validator.validateInput(repeatPwIl, repeatPwEt, activity) &&
      Validator.validatePassword(newPwEt, repeatPwEt, repeatPwIl, activity);
  }
}
