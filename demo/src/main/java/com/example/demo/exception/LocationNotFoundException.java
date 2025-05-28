package com.example.demo.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(Long protectedUserId) {
        super("해당 유저의 위치 정보를 를 찾을 수 없습니다.: " + protectedUserId);
    }
}
