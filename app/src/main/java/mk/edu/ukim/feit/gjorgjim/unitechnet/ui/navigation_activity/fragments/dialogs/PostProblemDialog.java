package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.SuccessFailureCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CourseViewFragment;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class PostProblemDialog extends Dialog{

  private Context context;

  private Activity activity;

  private CourseViewFragment fragment;

  private CourseService courseService;

  private AppCompatEditText descriptionEt;
  private TextInputLayout nameIl;
  private AppCompatEditText nameEt;
  private AppCompatButton postProblemBtn;

  public PostProblemDialog(@NonNull Context context, Activity activity, CourseViewFragment fragment) {
    super(context);
    this.context = context;
    this.activity = activity;
    courseService = CourseService.getInstance();
    this.fragment = fragment;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_post_problem);

    descriptionEt = findViewById(R.id.problemDescriptionEt);
    nameIl = findViewById(R.id.problemNameIl);
    nameEt = findViewById(R.id.problemNameEt);
    postProblemBtn = findViewById(R.id.postProblemBtn);

    postProblemBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(validateInput()) {
          Problem problem = new Problem();
          problem.setName(nameEt.getText().toString());
          problem.setDescription(descriptionEt.getText().toString());
          problem.setAnswers(new HashMap<>());
          problem.setDate(Date.formatToString(Date.getDate()));
          problem.setAnswerid("false");

          courseService.postProblemToCourse(problem, new SuccessFailureCallback() {
            @Override
            public void onSuccess() {
              Toast.makeText(context, "Problem posted successfully.", Toast.LENGTH_SHORT).show();
              fragment.showProblems();
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

  private boolean validateInput() {
    return Validator.validateInput(nameIl, nameEt, activity) && !TextUtils.isEmpty(descriptionEt.getText().toString());
  }
}
