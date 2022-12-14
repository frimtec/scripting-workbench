FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 17.0.5-zulu && \
    sdk default java 17.0.5-zulu && \
    wget https://github.com/PowerShell/PowerShell/releases/download/v7.1.0/powershell_7.1.0-1.ubuntu.20.04_amd64.deb && \
    sudo add-apt-repository universe && \
    sudo dpkg --force-all -i powershell_7.1.0-1.ubuntu.20.04_amd64.deb && \
    rm powershell_7.1.0-1.ubuntu.20.04_amd64.deb"
