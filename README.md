# clivrt Signaling Service
Backend service that provides WebRTC signaling for connecting clients together. This service offers:
* User registration and management
* A basic chat feature whereby a user can send a message to all other connected users
* Generic message handling for signaling and ICE negotiation to support a WebRTC session between two users

## Getting started
### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw quarkus:dev
``` 

#### Connecting using the GUI
Once the app is running, open http://localhost:8080 in your browser. Enter your desired username where indicated, and send a message to the socket using the Chat box at the bottom of the page.

#### Connecting using a Terminal
You can also just connect directly to the socket from the terminal using a websocket client, like `wscat`:
```shell script
# Assuming you have npm installed, install the wscat package
npm install wscat

# Then open a connection to the server (ws://localhost:8080/chat/${username})like this: 
wscat -c ws://localhost:8080/chat/andy
```

Once you're connected, send a message to chat using JSON, like this:
```shell script
{"type":"message", "text":"This is a test message"}
```

## Message Schema
The server expects most messages to take the form of a JSON object, giving particular regard to the following attributes:
* **type**: the message type. Supported values:
    * *message*: this indicates a text message to convey to one person or to everyone
    * *userlist*: this indcates that the list of users has updated in some way
* **text**: the text to send to the desired recipient(s), only applicable for **type: message**
* **users**: a list of usernames, only applicable for **type: userlist**
* **target**: the desired recipient of this message. If omitted, the message will be transmitted to all users currently connected to the socket.
