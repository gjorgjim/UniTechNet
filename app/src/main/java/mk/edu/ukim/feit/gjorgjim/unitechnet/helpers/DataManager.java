package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
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
    for(String key : alLAnswers.keySet()) {
      if(alLAnswers.get(key) == answer) {
        return key;
      }
    }

    return null;
  }

  public String getProblemKey(HashMap<String, Problem> allProblems, Problem problem) {
    for(String key : allProblems.keySet()) {
      if(allProblems.get(key) == problem) {
        return key;
      }
    }

    return null;
  }

  public String getNotificationKey(HashMap<String, Notification> allNotifications, Notification notification) {
    for(String key : allNotifications.keySet()) {
      if(allNotifications.get(key) == notification) {
        return key;
      }
    }

    return null;
  }
}
