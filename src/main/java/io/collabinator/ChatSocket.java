package io.collabinator;

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

    /**
     * Initialize a user session. Grab their username from the context
     * path (ws://localhost/chat/${username}), add their details to 
     * {@link ChatSocket#sessions}, and broadcast a message
     * to all connected users with an updated user list.
     * 
     * @param session the user's session
     * @param username the user's username
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        JsonObject message = new JsonObject()
            .put("type", "userlist")
            .put("users", getUserList());
        broadcast(message.encode());
    }

    /**
     * End a user session. Remove their details from {@link ChatSocket#sessions}
     * And notify all users of their departure.
     * 
     * @param session the user's session
     * @param username the user's username
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        JsonObject message = new JsonObject()
            .put("name", "SERVER")
            .put("type", "message")
            .put("text", "User " + username + " left");
        broadcast(message.encode());
    }

    /**
     * Report an error
     * 
     * @param session the user's session
     * @param username the user's username
     * @param throwable the relevant {@link Throwable}
     */
    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        JsonObject message = new JsonObject()
            .put("name", "SERVER")
            .put("type", "message")
            .put("text", "Error by user " + username + ": " + throwable.getMessage());
        broadcast(message.encode());
    }

    /**
     * Broker a message and route it to the 
     * user indicated by the {@code target} attribute or, 
     * if absent, to all users.
     * 
     * @param incomingMessage the incoming message
     * @param username the sender's username
     */
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

    /**
     * Send a message to all connected users.
     * 
     * @param message the message to send
     */
    private void broadcast(Object message) {
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    /**
     * Send a message to the indicated user
     * 
     * @param remoteUser the recipient's username
     * @param message the message to send
     */
    private void sendPrivateMessage(String remoteUser, Object message) {
        sessions.get(remoteUser).getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

    /**
     * Get a list of all currently connected users
     * 
     * @return a {@link List} of usernames
     */
    private List<String> getUserList() {
        return sessions.keySet().stream().collect(Collectors.toList());
    }
}