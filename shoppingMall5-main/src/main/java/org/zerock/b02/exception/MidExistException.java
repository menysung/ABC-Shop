package org.zerock.b02.exception;

public class MidExistException extends RuntimeException {

    // 기본 생성자
    public MidExistException() {
        super("The provided member ID already exists.");
    }

    // 메시지를 추가할 수 있는 생성자
    public MidExistException(String message) {
        super(message);
    }

    // 원인을 포함한 생성자
    public MidExistException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인을 포함한 생성자
    public MidExistException(Throwable cause) {
        super(cause);
    }
}
