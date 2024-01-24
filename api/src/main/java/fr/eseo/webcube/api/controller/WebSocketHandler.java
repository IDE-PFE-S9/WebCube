package fr.eseo.webcube.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, UserSessionDetails> userSessionDetailsMap = new HashMap<>();


    private static class UserSessionDetails {
        private String username;
        private String ipAddress;

        public UserSessionDetails(String username, String ipAddress) {
            this.username = username;
            this.ipAddress = ipAddress;
        }

        // Getters
        public String getUsername() {
            return username;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        // Setters
        public void setUsername(String username) {
            this.username = username;
        }
    }

    private String getJwtToken(WebSocketSession session) {
        // Assuming the JWT token is sent in a header named 'Authorization'
        String authorizationHeader = session.getHandshakeHeaders().getFirst("Authorization");
    
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token by removing 'Bearer ' prefix
            return authorizationHeader.substring(7);
        }
    
        return null; // Return null if the token is not present or not in the expected format
    }

    private String extractUsernameFromJwt(String token) throws ParseException {
        JWTClaimsSet claimsAzure = JWTParser.parse(token).getJWTClaimsSet();
        String uniqueName = (String) claimsAzure.getClaim("unique_name");
        return uniqueName;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String clientIpAddress = getClientIpAddress(session);

        userSessionDetailsMap.put(session.getId(), new UserSessionDetails("Not connected yet", clientIpAddress));
        sessions.add(session);
        broadcastUserDetails(); // Broadcast updated user details
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse the message payload
        String payload = message.getPayload();

        // Use a JSON parser to parse the payload
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(payload);
            String token = (String) jsonObject.get("data");

            if (token != null && !token.isEmpty()) {
                // Extract username from the JWT token
                String username = extractUsernameFromJwt(token);
                
                // Update the session details with the username
                UserSessionDetails details = userSessionDetailsMap.get(session.getId());
                if (details != null) {
                    details.setUsername(username); // Make sure setUsername method exists in UserSessionDetails class
                    broadcastUserDetails(); // Update all clients with the new user details
                }
            }
        } catch (ParseException e) {
            // Handle JSON parsing error
            System.err.println(e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        sessions.remove(session);
        userSessionDetailsMap.remove(session.getId()); // Remove the user from the map
        broadcastUserDetails(); // Broadcast updated user details
    }

    // Helper method to broadcast the user count
    private void broadcastUserCount() throws IOException {
        String userCountMessage = constructJsonMessage("userCount", sessions.size());
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                sendMessage(webSocketSession, new TextMessage(userCountMessage));
            }
        }
    }

    // Helper method to broadcast user details including IP addresses
    private void broadcastUserDetails() throws IOException {
        List<Map<String, String>> userDetailsList = new ArrayList<>();

        // Building the list of user details
        for (WebSocketSession webSocketSession : sessions) {
            String clientIpAddress = getClientIpAddress(webSocketSession);
            Map<String, String> userDetail = new HashMap<>();
            try {
                userDetail.put("username", userSessionDetailsMap.get(webSocketSession.getId()).getUsername());
            } catch (NullPointerException e) {
                userDetail.put("username", "Not connected yet");
            }
            userDetail.put("ip", clientIpAddress);
            userDetailsList.add(userDetail);
        }

        // Constructing the JSON message
        String userDetailsMessage = constructJsonMessage("userDetails", userDetailsList);

        // Sending the message to all open sessions
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                sendMessage(webSocketSession, new TextMessage(userDetailsMessage));
            }
        }
    }

    private String getClientIpAddress(WebSocketSession session) {
        String clientIp = session.getHandshakeHeaders().getFirst("X-Real-IP");
        if (clientIp == null) {
            clientIp = session.getHandshakeHeaders().getFirst("X-Forwarded-For");
        }
        return clientIp != null ? clientIp : session.getRemoteAddress().getAddress().getHostAddress();
    }

    // Helper method to construct a JSON message
    private String constructJsonMessage(String type, Object data) {
        try {
            return objectMapper.writeValueAsString(Map.of("type", type, "data", data));
        } catch (IOException e) {
            // Log error, handle exception, or return a fallback message
            return "{\"type\": \"error\", \"data\": \"An error occurred\"}";
        }
    }

    // Synchronized method to send a message to a session
    private synchronized void sendMessage(WebSocketSession session, TextMessage message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(message);
        }
    }
}
