package com.minjiki2.section9.constants;

public final class ApplicationConstants {
    // 환경변수에서 JWT 비밀키 가져올 때 키이름
    public static final String JWT_SECRET_KEY = "JWT_SECRET";

    // 환경변수에 비밀키가 정의되어 있지 않을 경우, 사용할 기본 비밀키값
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";

    // JWT 토큰을 저장할 HTTP 헤더명
    public static final String JWT_HEADER = "Authorization";
}
