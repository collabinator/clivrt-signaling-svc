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

### Setting up a development environment in CodeReady Workspaces
This repository is currently private, so in order to import it with CodeReady Workspaces, you'll need to create a credential secret.
1. Set variables for your github username and a [personal access token](https://github.com/settings/tokens) with full **repo** permissions.
    ```bash
    GITHUB_USERNAME=andykrohg
    GITHUB_TOKEN=ghp_000000000000000000000
    ```
2. Switch to (or create) the namespace where your workspaces will be provisioned, which by default is `${username}-codeready`. For example:
    ```bash
    oc project user1-codeready
    ```
2. Create a secret to hold your git credentials. The annotations will inform the CodeReady Server that it needs to mount the secret into your workspace once it's created.
    ```bash
    oc apply -f - << EOF
    apiVersion: v1
    kind: Secret
    metadata:
        name: git-credentials-secret
        labels:
            app.kubernetes.io/part-of: che.eclipse.org
            app.kubernetes.io/component: workspace-secret
        annotations:
            che.eclipse.org/automount-workspace-secret: 'true'
            che.eclipse.org/mount-path: /home/theia/.git-credentials
            che.eclipse.org/mount-as: file
            che.eclipse.org/git-credential: 'true'
    stringData:
        credentials: https://$GITHUB_USERNAME:$GITHUB_TOKEN@github.com
    EOF
    ```
3. Create a new workspace using the `devfile.yaml` in this repository.
## Message Schema
The server expects most messages to take the form of a JSON object, giving particular regard to the following attributes:
* **type**: the message type. Supported values:
    * *message*: this indicates a text message to convey to one person or to everyone
    * *userlist*: this indcates that the list of users has updated in some way
* **text**: the text to send to the desired recipient(s), only applicable for **type: message**
* **users**: a list of usernames, only applicable for **type: userlist**
* **target**: the desired recipient of this message. If omitted, the message will be transmitted to all users currently connected to the socket.
