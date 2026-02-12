package com.yunjae.jarvis3_server.service;

import com.yunjae.jarvis3_server.config.GlobalConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * [보안 서비스]
 * Private Chamber(비밀의 방) 진입 및 데이터 수정을 위한 PIN 검증을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateChamberService {

    private final GlobalConfig config;

    /**
     * 사용자가 입력한 PIN 번호가 시스템 설정(.env)과 일치하는지 확인합니다.
     * @param inputPin 프론트엔드에서 전달된 PIN 번호
     * @return 일치 여부
     */
    public boolean verifyPin(String inputPin) {
        // 보안 지침: 하드코딩을 피하고 GlobalConfig를 통해 환경변수 값을 가져옵니다.
        String masterPin = config.getPrivateChamberPin();

        if (inputPin == null || masterPin == null) {
            log.warn("🚫 PIN 검증 실패: 입력값 또는 시스템 설정값이 누락되었습니다.");
            return false;
        }

        boolean isValid = masterPin.equals(inputPin);

        if (isValid) {
            log.info("🔑 설계자 인증 성공: 성역으로의 접근이 허가되었습니다.");
        } else {
            log.warn("⚠️ 인증 실패: 잘못된 PIN 번호가 입력되었습니다.");
        }

        return isValid;
    }
}