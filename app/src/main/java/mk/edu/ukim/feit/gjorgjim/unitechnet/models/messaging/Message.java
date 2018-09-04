package mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging;

import java.io.Serializable;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;

/**
 * Created by gjmarkov on 08.8.2018.
 */

public class Message implements Serializable{
  private String senderId;
  private String value;
  private Date sentDate;

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

  public Date getSentDate() {
    return sentDate;
  }

  public void setSentDate(Date sentDate) {
    this.sentDate = sentDate;
  }

  @Override
  public String toString() {
    return String.format(
      new Locale("en"),
      "%s %s %s",
      senderId,
      value,
      sentDate
    );
  }
}
