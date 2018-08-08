package mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging;

import java.util.Date;

/**
 * Created by gjmarkov on 08.8.2018.
 */

public class Message {
  private String senderId;
  private String value;
  private String sentDate;

  public Message() {
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getSentDate() {
    return sentDate;
  }

  public void setSentDate(String sentDate) {
    this.sentDate = sentDate;
  }
}
