package com.spring.app.chatting.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.chatting.domain.MessageDTO;
import com.spring.app.chatting.domain.Mongo_messageDTO;
import com.spring.app.chatting.service.ChattingMongoOperations;
import com.spring.app.domain.MemberDTO;

import lombok.RequiredArgsConstructor;

//=== (#웹채팅관련6) ===
@Component
@RequiredArgsConstructor
public class WebsocketEchoHandler extends TextWebSocketHandler {

    // 현재 연결된 웹소켓 세션들(탭마다 1개씩 생김)
    private final List<WebSocketSession> connectedUsers = new ArrayList<>();

    // 사용자 메타(표시용 테이블 데이터)
    private final List<MemberDTO> memberDto_list = new ArrayList<>();

    // MongoDB
    private final ChattingMongoOperations chattingMongo;
    private final Mongo_messageDTO document;

    // 사용자별 세션들(알림 1:1 전송용)
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    // JSON 직렬화기
    private final ObjectMapper om = new ObjectMapper();

    // 알림 페이로드(JSON)
    @lombok.Getter @lombok.Setter @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class NotifyPayload {
        private String kind = "notify";
        private String title;
        private String body;
        private String link;
        private String notiId;
        private String createdAt;
    }

    public void init() throws Exception {}

    // === 접속 시 ===
    @Override
    public void afterConnectionEstablished(WebSocketSession wsession) throws Exception {
        connectedUsers.add(wsession);	// 현재 연결된 세션 목록에 추가 (브라우저 탭 기준 1세션)

        // 현재 접속 사용자의 로그인 정보
        Map<String, Object> attrs = wsession.getAttributes();
        MemberDTO loginuser = (MemberDTO) attrs.get("loginuser");

        // userSessions 등록(현재 세션만 등록하면 됨)
        if (loginuser != null) {
            userSessions
                .computeIfAbsent(loginuser.getMemberUserid(), k -> ConcurrentHashMap.newKeySet())
                .add(wsession);
        }

        // memberDto_list 에 사용자 단위로 중복 없이 추가
        if (loginuser != null) {
            boolean exists = false;
            for (MemberDTO m : memberDto_list) {
                if (m.getMemberUserid().equals(loginuser.getMemberUserid())) {
                    exists = true; break;
                }
            }
            if (!exists) {
                memberDto_list.add(loginuser);
            }
        }

        // ===== 접속자 문자열(「 ... 」) 생성: userid 기준 중복 제거 =====
        String connectingUserName = buildUniqueConnectingUsersString();

        // 브로드캐스트(접속자 문자열)
        broadcastToAll(new TextMessage(connectingUserName));

        // ===== 테이블(⊆ prefix) 갱신 → 테이블 뷰가 있다면 즉시 갱신 =====
        String v_html = buildMemberTableHtml();
        if (v_html != null) {
            broadcastToAll(new TextMessage(v_html));
        }

        // ===== 과거 대화 로딩 (Mongo) =====
        List<Mongo_messageDTO> list = chattingMongo.listChatting();
        SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN);

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String str_created = sdfrmt.format(list.get(i).getCreated());

                boolean is_newDay = true;
                if (i > 0 && str_created.equals(sdfrmt.format(list.get(i - 1).getCreated()))) {
                    is_newDay = false;
                }
                if (is_newDay) {
                    wsession.sendMessage(new TextMessage(
                        "<div style='text-align: center; background-color: #ccc;'>" + str_created + "</div>"));
                }

                MemberDTO cur = (MemberDTO) wsession.getAttributes().get("loginuser");
                // 본인/타인 메시지에 따라 말풍선 정렬과 스타일을 달리해 재생
                if (cur != null && cur.getMemberUserid().equals(list.get(i).getUserid())) {
                    wsession.sendMessage(new TextMessage(
                        "<div style='background-color: #ffff80; display: inline-block; max-width: 60%; float: right; padding: 7px; border-radius: 15%; word-break: break-all;'>"
                            + list.get(i).getMessage() + "</div> <div style='display: inline-block; float: right; padding: 20px 5px 0 0; font-size: 7pt;'>"
                            + list.get(i).getCurrentTime() + "</div> <div style='clear: both;'>&nbsp;</div>"));
                } else {
                    wsession.sendMessage(new TextMessage(
                        "[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>"
                            + list.get(i).getName()
                            + "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all;'>"
                            + list.get(i).getMessage()
                            + "</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"
                            + list.get(i).getCurrentTime() + "</div> <div>&nbsp;</div>"));
                }
            }
        }
    }

    // === 메시지 수신 시 ===
    @Override
    public void handleTextMessage(WebSocketSession wsession, TextMessage message) throws Exception {
        Map<String, Object> map = wsession.getAttributes();
        MemberDTO loginuser = (MemberDTO) map.get("loginuser");
        MessageDTO messageDto = MessageDTO.convertMessage(message.getPayload());

        Date now = new Date();
        String currentTime = String.format("%tp %tl:%tM", now, now, now);

        // [3] 공개(all)면 본인 제외 전체에게, 귓속말(one)이면 대상 세션 1곳에만 송신
        for (WebSocketSession webSocketSession : connectedUsers) {
            if ("all".equals(messageDto.getType())) {
                if (!wsession.getId().equals(webSocketSession.getId())) {
                    webSocketSession.sendMessage(new TextMessage(
                        "<span style='display:none;'>" + wsession.getId()
                            + "</span>&nbsp;[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>"
                            + loginuser.getMemberName()
                            + "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all;'>"
                            + messageDto.getMessage()
                            + "</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"
                            + currentTime + "</div> <div>&nbsp;</div>"));
                }
            } else {
                String ws_id = webSocketSession.getId();
                if (messageDto.getTo().equals(ws_id)) {
                    webSocketSession.sendMessage(new TextMessage(
                        "<span style='display:none;'>" + wsession.getId()
                            + "</span>&nbsp;[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>"
                            + loginuser.getMemberName()
                            + "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all; color: red;'>"
                            + messageDto.getMessage()
                            + "</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"
                            + currentTime + "</div> <div>&nbsp;</div>"));
                    break;	// 대상은 오직 1세션
                }
            }
        }

        // 공개대화만 Mongo 저장(귓속말은 비저장)
        if ("all".equals(messageDto.getType())) {
            String str_now_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String _id = str_now_date + "_" + uuid;

            document.set_id(_id);
            document.setMessage(messageDto.getMessage());
            document.setTo(messageDto.getTo());
            document.setUserid(loginuser.getMemberUserid());
            document.setName(loginuser.getMemberName());
            document.setCurrentTime(currentTime);
            document.setCreated(new Date());

            chattingMongo.insertMessage(document);
        }
    }

    // === 종료 시 ===
    @Override
    public void afterConnectionClosed(WebSocketSession wsession, CloseStatus status) throws Exception {
        Map<String, Object> map = wsession.getAttributes();
        MemberDTO loginuser = (MemberDTO) map.get("loginuser");

        // 연결 세션 목록에서 제거
        connectedUsers.remove(wsession);

        // userSessions에서 제거
        if (loginuser != null) {
            Set<WebSocketSession> set = userSessions.get(loginuser.getMemberUserid());
            if (set != null) {
                set.remove(wsession);
                if (set.isEmpty()) {
                    userSessions.remove(loginuser.getMemberUserid());
                }
            }
        }

        // 접속자 문자열 재생성(중복 제거) 및 브로드캐스트
        String connectingUserName = buildUniqueConnectingUsersString();
        broadcastToAll(new TextMessage(connectingUserName));

        // memberDto_list에서 해당 사용자 제거(안전 방식)
        if (loginuser != null && !memberDto_list.isEmpty()) {
            Iterator<MemberDTO> it = memberDto_list.iterator();
            while (it.hasNext()) {
                if (it.next().getMemberUserid().equals(loginuser.getMemberUserid())) {
                    it.remove();
                    break;
                }
            }
        }

        // 테이블(⊆ prefix) 갱신
        String v_html = buildMemberTableHtml();
        if (v_html != null) {
            broadcastToAll(new TextMessage(v_html));
        }
    }

    // 특정 사용자에게 알림 푸시
    public void pushNotify(String memberUserid, NotifyPayload payload) {
        Set<WebSocketSession> sessions = userSessions.get(memberUserid);
        if (sessions == null || sessions.isEmpty()) return;

        try {
            String json = om.writeValueAsString(payload);
            TextMessage msg = new TextMessage(json);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    try { s.sendMessage(msg); } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
    }

    // ===== 유틸 =====

    // 현재 connectedUsers를 돌며 userid 기준으로 유니크한 이름만 모아 「이름들 」 문자열 생성
    private String buildUniqueConnectingUsersString() {
        // 순서 보존 + 유니크
        Set<String> seenUserIds = new LinkedHashSet<>();
        StringBuilder sb = new StringBuilder("「");

        for (WebSocketSession s : connectedUsers) {
            Map<String, Object> attrs = s.getAttributes();
            MemberDTO u = (MemberDTO) attrs.get("loginuser");
            if (u == null) continue;
            if (seenUserIds.add(u.getMemberUserid())) {
                sb.append(u.getMemberName()).append(' ');
            }
        }
        sb.append('」');
        return sb.toString();
    }

    // memberDto_list 기반 사용자 테이블(⊆ prefix) 생성
    private String buildMemberTableHtml() {
        if (memberDto_list.isEmpty()) return null;

        StringBuilder v = new StringBuilder("⊆");
        for (MemberDTO m : memberDto_list) {
            v.append("<tr>")
             .append("<td>").append(m.getMemberUserid()).append("</td>")
             .append("<td>").append(m.getMemberName()).append("</td>")
             .append("<td>").append(m.getMemberEmail()).append("</td>")
             .append("</tr>");
        }
        return v.toString();
    }

    // 모든 연결에 전송
    private void broadcastToAll(TextMessage msg) throws Exception {
        for (WebSocketSession s : connectedUsers) {
            if (s.isOpen()) s.sendMessage(msg);
        }
    }
}
