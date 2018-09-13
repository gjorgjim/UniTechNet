package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class NotificationService {
  private static final NotificationService ourInstance = new NotificationService();

  public static NotificationService getInstance() {
    return ourInstance;
  }

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private NotificationService() {
    allNotifications = new HashMap<>();
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  private HashMap<String, Notification> allNotifications;

  public void getNotifications() {
  }
}
