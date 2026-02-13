package com.yunjae.jarvis3_server.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DescriptiveMathPreprocessor implements MathPreprocessor {

    // LaTeX 수식 감지 패턴 ($ ... $)
    private static final Pattern MATH_PATTERN = Pattern.compile("\\$(.*?)\\$");

    // 전기공학 핵심 도메인 사전 (수식 -> 기술적 의미)
    private static final Map<String, String> TECH_DICT = Map.of(
            "SoC_{t}", "쿨롱 카운팅 기반의 실시간 배터리 충전 상태(State of Charge) 추정값",
            "\\eta", "충방전 사이클에서의 에너지 변환 효율 계수",
            "C_n", "BMS 설계 시 기준이 되는 배터리 정격 용량(Nominal Capacity)",
            "R_{int}", "배터리 노화 및 온도에 따른 내부 저항(Internal Resistance)",
            "Li^{+}", "리튬 이온 배터리 및 전고체 배터리의 전하 운반체"
    );

    @Override
    public String preprocess(String input) {
        if (input == null || input.isEmpty()) return input;

        Matcher matcher = MATH_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String rawFormula = matcher.group(1).trim();
            // 사전에 정의된 수식이면 설명을 추가, 아니면 '공학 수식'으로 일반화
            String description = TECH_DICT.getOrDefault(rawFormula, "전기공학적 물리 관계식");

            // AI가 수식과 의미를 동시에 학습하도록 변환
            // 예: $SoC_{t}$ -> [수식: SoC_{t} | 의미: 배터리 충전 상태 추정값]
            matcher.appendReplacement(sb, String.format("[수식: %s | 물리적 의미: %s]", rawFormula, description));
        }
        matcher.appendTail(sb);

        // 추가로 '전고체 배터리' 등 핵심 키워드 맥락 강화 (필요 시)
        String processed = sb.toString();
        if (processed.contains("BMS") || processed.contains("SoC")) {
            processed = "[BMS 도메인 맥락] " + processed;
        }

        return processed;
    }
}