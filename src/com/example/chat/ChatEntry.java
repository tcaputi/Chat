package com.example.chat;

import java.io.Serializable;
import java.util.Date;

public class ChatEntry implements Serializable {
  private static final long serialVersionUID = -3580225830905858058L;
  
  private final String name;
  private final Date timestamp;
  private final String message;
  
  public ChatEntry(final String name, final Date timestamp, final String message) {
    this.name = name;
    this.timestamp = timestamp;
    this.message = message;
  }
  
  public String getName() {
    return name;
  }
  
  public Date getTimestamp() {
    return timestamp;
  }
  
  public String getMessage() {
    return message;
  }
}