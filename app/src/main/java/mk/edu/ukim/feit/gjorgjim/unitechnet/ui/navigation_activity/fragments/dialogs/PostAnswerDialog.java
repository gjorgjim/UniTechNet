package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.SuccessFailureCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProblemViewFragment;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class PostAnswerDialog extends Dialog {

  private Context context;

  private CourseService courseService;

  private ProblemViewFragment fragment;

  private AppCompatEditText descriptionEt;
  private AppCompatButton postBtn;

  public PostAnswerDialog(@NonNull Context context, ProblemViewFragment fragment) {
    super(context);
    this.context = context;
    courseService = CourseService.getInstance();
    this.fragment = fragment;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_post_asnwer);

    descriptionEt = findViewById(R.id.descriptionEt);
    postBtn = findViewById(R.id.postBtn);

    postBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!TextUtils.isEmpty(descriptionEt.getText().toString())) {
          Answer answer = new Answer();
          answer.setDescription(descriptionEt.getText().toString());
          answer.setDate(Date.formatToString(Date.getDate()));
          answer.setIsAnswer(false);

          courseService.postAnswerToProblem(answer, new SuccessFailureCallback() {
            @Override
            public void onSuccess() {
              Toast.makeText(context, "Answer posted successfully.", Toast.LENGTH_SHORT).show();
              fragment.showAnswers();
              dismiss();
            }

            @Override
            public void onFailure() {
              Toast.makeText(context, "This operation cannot be done at this moment. Try again later.",
                Toast.LENGTH_SHORT).show();
            }
          });
        }
      }
    });
  }
}
