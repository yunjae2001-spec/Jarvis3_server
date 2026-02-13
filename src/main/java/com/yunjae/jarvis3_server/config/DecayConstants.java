package com.yunjae.jarvis3_server.config;

public class DecayConstants {
    // 계층별 감쇠율 (1.0 = 100%)
    public static final double OM_RATE = 0.0;        // 헌법: 망각 없음
    public static final double INSIGHT_RATE = 0.0001; // 깨달음: 0.01% (초저속 감쇠)
    public static final double SESSION_RATE = 0.10;   // 일상: 10% (고속 감쇠)

    // 임계값 및 보호 설정
    public static final double MIN_FLOOR = 0.1;       // 삭제 하한선
    public static final double PROTECTION_THRESHOLD = 2.0; // [추가] 가중치 2.0 이상은 '영구 각인'
    public static final int PROTECTION_DAYS = 7;     // 최근 7일간 활성화된 시냅스는 보호
}