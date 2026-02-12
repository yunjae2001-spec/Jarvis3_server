package com.yunjae.jarvis3_server.controller;

import com.yunjae.jarvis3_server.service.OmDbService;
import com.yunjae.jarvis3_server.service.PrivateChamberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/private-chamber")
@RequiredArgsConstructor
@Slf4j
public class PrivateChamberController {

    private final PrivateChamberService authService;
    private final OmDbService omDbService;

    /**
     * [입법자 모드 진입]
     * PIN 번호를 검증하여 '비밀의 방' 접근 권한을 확인합니다.
     */
    @PostMapping("/unlock")
    public ResponseEntity<String> unlock(@RequestBody Map<String, String> request) {
        String pin = request.get("pin");

        if (authService.verifyPin(pin)) {
            log.info("🔓 설계자 강윤재 님이 'Private Chamber'에 진입했습니다.");
            return ResponseEntity.ok("입법자 모드가 활성화되었습니다. 사유 경로의 성역에 오신 것을 환영합니다.");
        }

        log.warn("🚫 승인되지 않은 접근 시도가 감지되었습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }

    /**
     * [옴 DB 레이어 개정]
     * 특정 레이어의 내용을 수정합니다. Layer 1(헌법)의 경우 Friction 로직이 작동합니다.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateLayer(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String pin = request.get("pin");
        String newContent = request.get("content");
        String reason = request.get("reason");

        // 1. PIN 재검증 (보안 보고서의 '가장 약한 고리' 방지 원칙)
        if (!authService.verifyPin(pin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("개정 권한이 없습니다.");
        }

        try {
            // 2. Friction 로직이 포함된 서비스 호출
            // Layer 1 수정 시 여기서 10초간 스레드가 멈추며 '숙고의 시간'을 강제합니다.
            omDbService.updateWithFriction(id, newContent, reason);

            return ResponseEntity.ok("성공적으로 개정되었습니다. Jarvis의 세계관에 반영되었습니다.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("개정 프로세스 중 오류가 발생했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    /**
     * [입법자 모드 종료]
     * 명시적으로 세션을 종료하거나 입법 모드를 닫습니다.
     */
    @PostMapping("/lock")
    public ResponseEntity<String> lock() {
        log.info("🔒 설계자 강윤재 님이 'Private Chamber'에서 퇴장했습니다.");
        return ResponseEntity.ok("입법자 모드가 해제되었습니다. 성역의 문이 다시 잠깁니다.");
    }

}