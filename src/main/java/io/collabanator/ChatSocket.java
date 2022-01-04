package io.collabanator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import io.vertx.core.json.JsonObject;

@ServerEndpoint("/chat/{username}")         
@ApplicationScoped
public class ChatSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>(); 

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        JsonObject message = new JsonObject();
        message.put("type", "userlist");
        message.put("users", getUserList());
        broadcast(message.encode());
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast("User " + username + " left");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast("User " + username + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String incomingMessage, @PathParam("username") String username) {
        JsonObject message = new JsonObject(incomingMessage);
        message.put("name", username);

        String outgoingMessage = message.encode();

        if (message.getString("target") != null) {
            sendPrivateMessage(message.getString("target"), outgoingMessage);
        } else {
            broadcast(outgoingMessage);
        }
    }

    private void broadcast(Object message) {
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    private void sendPrivateMessage(String remoteUser, Object message) {
        sessions.get(remoteUser).getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

    private List<String> getUserList() {
        return sessions.keySet().stream().collect(Collectors.toList());
    }
}