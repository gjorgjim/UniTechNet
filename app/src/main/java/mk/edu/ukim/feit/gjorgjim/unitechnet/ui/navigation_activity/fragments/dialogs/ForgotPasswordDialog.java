package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class ForgotPasswordDialog extends Dialog {

  private RelativeLayout mainLayout;
  private RelativeLayout confirmationLayout;
  private AppCompatTextView dialogDescriptionTv;
  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private AppCompatButton sendEmailBtn;
  private AppCompatTextView statusTv;
  private ProgressBar progressBar;

  private Context context;
  private Activity activity;

  private AuthenticationService authenticationService;

  public ForgotPasswordDialog(@NonNull Context context) {
    super(context);
    this.context = context;
    activity = (Activity) context;
    authenticationService = AuthenticationService.getInstance();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_forgot_password);

    mainLayout = findViewById(R.id.mainLayout);
    confirmationLayout = findViewById(R.id.confirmationLayout);
    dialogDescriptionTv = findViewById(R.id.dialogDescriptionTv);
    emailIl = findViewById(R.id.emailIl);
    emailEt = findViewById(R.id.emailEt);
    sendEmailBtn = findViewById(R.id.sendEmailBtn);
    statusTv = findViewById(R.id.statusTv);
    progressBar = findViewById(R.id.progressBar);

    sendEmailBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(Validator.validateEmail(emailIl, emailEt, activity)) {
          KeyboardDelegate.hideSoftKeyboard(activity, emailEt);
          showWaitingStatus();
          authenticationService.forgotPassword(emailEt.getText().toString().trim(), new AuthenticationCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
              ForgotPasswordDialog.this.onSuccess();
            }

            @Override
            public void onFailure(String message) {
              ForgotPasswordDialog.this.onFailure(message);
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
    statusTv.setText(context.getString(R.string.email_sent_success));
    setCancelable(true);
  }

  private void onFailure(String message) {
    dialogDescriptionTv.setText(String.format("%s %s", message, context.getString(R.string.try_again)));
    confirmationLayout.setVisibility(GONE);
    mainLayout.setVisibility(View.VISIBLE);
    setCancelable(true);
  }
}
