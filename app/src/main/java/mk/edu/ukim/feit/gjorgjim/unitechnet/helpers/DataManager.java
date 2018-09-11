package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

import java.util.HashMap;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;

/**
 * Created by gjmarkov on 11.9.2018.
 */

public class DataManager {
  private static final DataManager ourInstance = new DataManager();

  public static DataManager getInstance() {
    return ourInstance;
  }

  private DataManager() {
  }

  public String getAnswerKey(HashMap<String, Answer> alLAnswers, Answer answer) {
    for(Map.Entry<String, Answer> current : alLAnswers.entrySet()) {
      if(current == answer) {
        return current.getKey();
      }
    }

    return null;
  }

  public String getProblemKey(HashMap<String, Problem> allProblems, Problem problem) {
    for(Map.Entry<String, Problem> current : allProblems.entrySet()) {
      if(current == problem) {
        return current.getKey();
      }
    }

    return null;
  }
}
