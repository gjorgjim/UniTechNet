package mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging;

import java.io.Serializable;
import java.util.HashMap;
/**
 * Created by gjmarkov on 08.8.2018.
 */

public class Chat implements Serializable{

  public static String TAG = "CHAT";

  private String firstName;
  private String lastName;
  private Message lastMessage;

  public Chat() {
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Message getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(Message lastMessage) {
    this.lastMessage = lastMessage;
  }
}
