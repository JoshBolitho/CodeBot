package main.JSONClasses;

public class Log {
      String message;
      String error_message;
      String date_time;

    public String getMessage() {
        return message;
    }

    public String getError_message() {
        return error_message;
    }

    public String getDate_time() {
        return date_time;
    }

    public Log(String message, String error_message, String date_time) {
        this.message = message;
        this.error_message = error_message;
        this.date_time = date_time;
    }
}
