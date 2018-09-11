package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.SuccessFailureCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.ViewDelegate;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProblemViewFragment;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class AnswerView extends RelativeLayout {

  private Context context;

  private ProblemViewFragment fragment;

  private AuthenticationService authenticationService;
  private CourseService courseService;

  private ViewDelegate viewDelegate;

  private Answer currentAnswer;

  private HashMap<String, User> problemAuthor;

  private boolean isAnswer;

  private AlertDialog confirmationDialog = new AlertDialog.Builder(context).setTitle("Deleting answer").setMessage(
    "Are you sure you want to delete this answer?").setNegativeButton("NO", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      confirmationDialog.dismiss();
    }
  }).create();

  private AppCompatTextView answerDescriptionTv;
  private AppCompatEditText editDescriptionEt;
  private AppCompatTextView answerAuthorTv;
  private RelativeLayout answerAuthorRl;
  private RelativeLayout problemAuthorRl;
  private AppCompatImageView editAnswerIv;
  private AppCompatImageView deleteAnswerIv;
  private AppCompatTextView markAsAnswerTv;
  private View underlineView;

  public AnswerView(Context context, ProblemViewFragment fragment, Answer answer, HashMap<String, User> problemAuthor,
    boolean isAnswer) {
    super(context);
    this.context = context;
    this.fragment = fragment;
    currentAnswer = answer;
    this.problemAuthor = problemAuthor;
    this.isAnswer = isAnswer;
    authenticationService = AuthenticationService.getInstance();
    courseService = CourseService.getInstance();
    viewDelegate = ViewDelegate.getInstance();
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.answer_view_layout, this);

    answerAuthorTv = view.findViewById(R.id.answerAuthorTv);
    answerDescriptionTv = view.findViewById(R.id.answerDescriptionTv);
    editDescriptionEt = view.findViewById(R.id.editDescriptionTv);
    answerAuthorRl = view.findViewById(R.id.answerAuthorRl);
    problemAuthorRl = view.findViewById(R.id.problemAuthorRl);
    editAnswerIv = view.findViewById(R.id.editAnswerIv);
    deleteAnswerIv = view.findViewById(R.id.deleteAnswerIv);
    markAsAnswerTv = view.findViewById(R.id.markAsAnswerTv);
    underlineView = view.findViewById(R.id.answerUnderline);

    setupAllViews();
    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    editAnswerIv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showEditModeView();
      }
    });

    deleteAnswerIv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        confirmationDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            courseService.deleteAnswerFromProblem(currentAnswer, new SuccessFailureCallback() {
              @Override
              public void onSuccess() {
                confirmationDialog.dismiss();
                viewDelegate.getCurrentActivity().onBackPressed();
              }

              @Override
              public void onFailure() {
                confirmationDialog.dismiss();
                Toast.makeText(context, "This operation cannot be done at this moment. Try again later.",
                  Toast.LENGTH_SHORT).show();
              }
            });
          }
        });
        confirmationDialog.show();
      }
    });

    markAsAnswerTv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isInEditAnswerMode()) {
          courseService.editAnswerDescription(currentAnswer, editDescriptionEt.getText().toString(),
            new SuccessFailureCallback() {
              @Override
              public void onSuccess() {
                currentAnswer.setDescription(editDescriptionEt.getText().toString());
                updateViewsAfterEdit();
              }

              @Override
              public void onFailure() {
                Toast.makeText(context, "This operation cannot be done at this moment. Try again later.",
                  Toast.LENGTH_SHORT).show();
              }
            });
          setupAllViews();
        } else {
          courseService.editProblemsAnswerId(currentAnswer, new SuccessFailureCallback() {
            @Override
            public void onSuccess() {
              isAnswer = true;
              setupAllViews();
              fragment.getCurrentAnswerView().setNotAnswer();
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

  public void setNotAnswer() {
    isAnswer = false;
    underlineView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
    markAsAnswerTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
  }

  private boolean isSameAuthor() {
    return problemAuthor.containsKey(authenticationService.getCurrentUser().getUid()) && currentAnswer.getAuthor()
      .containsKey(authenticationService.getCurrentUser().getUid());
  }

  private boolean isAnswerAuthor() {
    return currentAnswer.getAuthor().containsKey(authenticationService.getCurrentUser().getUid());
  }

  private boolean isProblemAuthor() {
    return problemAuthor.containsKey(authenticationService.getCurrentUser().getUid());
  }

  private boolean isInEditAnswerMode() {
    return editDescriptionEt.getVisibility() == VISIBLE;
  }

  private void setupAllViews() {
    answerDescriptionTv.setText(currentAnswer.getDescription());
    answerAuthorTv.setText(String
      .format(new Locale("en"), "%s %s %s", context.getString(R.string.problem_author_placeholder), new ArrayList<>(
        currentAnswer.getAuthor().values()).get(0).getFirstName(), new ArrayList<>(currentAnswer.getAuthor().values())
        .get(0).getLastName()));

    if (isSameAuthor()) {
      showBothViews();
    } else if (isAnswerAuthor()) {
      showAnswerAuthorView();
    } else if (isProblemAuthor()) {
      showProblemAuthorView();
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private void showAnswerAuthorView() {
    answerAuthorRl.setVisibility(VISIBLE);
    problemAuthorRl.setVisibility(GONE);
  }

  private void showProblemAuthorView() {
    answerAuthorRl.setVisibility(GONE);
    if (isAnswer) {
      markAsAnswerTv.setTextColor(context.getResources().getColor(R.color.ef_grey));
      underlineView.setBackgroundColor(context.getResources().getColor(R.color.answeredProblemColor));
    }
    problemAuthorRl.setVisibility(VISIBLE);
  }

  private void showBothViews() {
    answerAuthorRl.setVisibility(VISIBLE);
    if (isAnswer) {
      markAsAnswerTv.setTextColor(context.getResources().getColor(R.color.ef_grey));
      underlineView.setBackgroundColor(context.getResources().getColor(R.color.answeredProblemColor));
    }
    problemAuthorRl.setVisibility(VISIBLE);
  }

  private void updateViewsAfterEdit() {
    answerDescriptionTv.setText(currentAnswer.getDescription());
    markAsAnswerTv.setText(context.getString(R.string.mark_as_answer));
  }

  private void showEditModeView() {
    answerDescriptionTv.setVisibility(GONE);
    answerAuthorTv.setVisibility(GONE);
    markAsAnswerTv.setText(context.getString(R.string.save));
    editDescriptionEt.setVisibility(VISIBLE);
    showProblemAuthorView();
  }
}
