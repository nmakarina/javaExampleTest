package ui.services;


public final class Message {

    public String exceptionFirstLine(Exception e) {
        return throwableFirstLine(e);
    }

    public String throwableFirstLine(Throwable t) {
        return t.getMessage().split("\n")[0];
    }

}
