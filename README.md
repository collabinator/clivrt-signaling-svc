# clivrt Signaling Service
Backend service that provides WebRTC signaling for connecting clients together. This service offers:
* User registration and management
* A basic chat feature whereby a user can send a message to all other connected users
* Generic message handling for signaling and ICE negotiation to support a WebRTC session between two users

## Running this app
There are many ways to run this Quarkus app, but likely you want it available on the internet so it is widely accessible. You have options but our typical way of running this is in [an OpenShift Developer Sandbox](https://developers.redhat.com/developer-sandbox/get-started). We are working on a deployment of a versioned container release but aren't quite polished enough yet. For now you have a few steps to follow:
1. Download or clone this git repo locally
2. oc login to your account
3. edit the `resources/application.properties` to set the `quarkus.container-image.group` to your namespace
4. run `mvnw clean package`

It should do everything needed to create all the Kubernetes reqsources and deploy the app

## Developers
Below are 2 options for developing on this app: 
1) via a web IDE called Code Ready Workspaces
2) locally on a machine with the JDK, Quarkus, wscat, and other dev tools installed.

### Option 1 - Setting up a development environment in CodeReady Workspaces (CRW)
1. Login your CRW and navigate to the "Create Workspace" page
2. Paste this repo's URL into the text box
3. Click "Create & Open"

Note: this works because the repo has a [devfile.yaml](https://devfile.io/docs/devfile/2.1.0/user-guide/index.html) which has instructions for config and running our development environment.

![Screenshot](docs/CRW-createscreen.png?raw=true)


### Option 2 - Running the application locally in dev mode
Alternatively, and doing a bit more work, you can run your application locally in dev mode.

Exec the quarkus command that enables [live coding](https://quarkus.io/vision/developer-joy) using:
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
