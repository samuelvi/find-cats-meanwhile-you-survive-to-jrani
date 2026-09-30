FROM debian:bookworm-slim@sha256:3783cc01769c7b2b1b83a5c5ad96c815348e28ed7da68e2e3687004faa906251

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates curl python3 libstdc++6 libfreetype6 fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/downloads
COPY docker/j2me/dependencies.sha256 ./
RUN curl -fsSL --retry 3 https://cdn.azul.com/zulu/bin/zulu6.22.0.3-ca-jdk6.0.119-linux_x64.tar.gz -o jdk.tar.gz \
    && curl -fsSL --retry 3 https://repo.maven.apache.org/maven2/org/microemu/cldcapi11/2.0.4/cldcapi11-2.0.4.jar -o cldcapi11.jar \
    && curl -fsSL --retry 3 https://repo.maven.apache.org/maven2/org/microemu/midpapi20/2.0.4/midpapi20-2.0.4.jar -o midpapi20.jar \
    && curl -fsSL --retry 3 https://repo.maven.apache.org/maven2/net/sf/proguard/proguard-base/5.3.3/proguard-base-5.3.3.jar -o proguard.jar \
    && curl -fsSL --retry 3 https://repo.maven.apache.org/maven2/org/apache/ant/ant/1.9.16/ant-1.9.16.jar -o ant.jar \
    && curl -fsSL --retry 3 https://repo.maven.apache.org/maven2/org/apache/ant/ant-launcher/1.9.16/ant-launcher-1.9.16.jar -o ant-launcher.jar \
    && sha256sum -c dependencies.sha256 \
    && mkdir -p /opt/jdk /opt/j2me /opt/ant/lib \
    && tar -xzf jdk.tar.gz -C /opt/jdk --strip-components=1 \
    && mv cldcapi11.jar midpapi20.jar proguard.jar /opt/j2me/ \
    && mv ant.jar ant-launcher.jar /opt/ant/lib/ \
    && rm jdk.tar.gz

ENV JAVA_HOME=/opt/jdk ANT_HOME=/opt/ant
ENV PATH="/opt/jdk/bin:${PATH}"
RUN java -version && java -cp '/opt/ant/lib/*' org.apache.tools.ant.Main -version
WORKDIR /workspace
ENTRYPOINT ["java", "-cp", "/opt/ant/lib/*:/opt/jdk/lib/tools.jar", "org.apache.tools.ant.Main", "-f", "mobile/build.xml"]
