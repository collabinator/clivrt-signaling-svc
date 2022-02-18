If you need to connect to a private git repo, in order to import it with CodeReady Workspaces, these instructions should help.

First, you'll need to create a credential secret.
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
3. Create a new workspace using the `devfile.yaml` in your repository.
