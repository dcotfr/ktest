FROM redhat/ubi9-minimal:latest AS base-graalvm-build-image
USER root
RUN microdnf --setopt=install_weak_deps=0 --setopt=tsflags=nodocs install -y tar gzip gcc glibc-devel zlib-devel shadow-utils unzip gcc-c++ findutils && rpm -q tar gzip gcc glibc-devel zlib-devel shadow-utils unzip gcc-c++ findutils # buildkit
RUN microdnf --setopt=install_weak_deps=0 --setopt=tsflags=nodocs install -y glibc-langpack-en && rpm -q glibc-langpack-en # buildkit
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
ENV APP_HOME=/home/quarkus
RUN groupadd -r quarkus -g 1001 && useradd -u 1001 -r -g 1001 -m -d ${APP_HOME} -s /sbin/nologin -c "Quarkus user" quarkus # buildkit
RUN mkdir /project # buildkit
RUN chown quarkus:quarkus /project # buildkit
RUN microdnf --setopt=install_weak_deps=0 --setopt=tsflags=nodocs install -y xz && rpm -q xz # buildkit
ENV UPX_VERSION=5.0.1
COPY upx-5.0.1 /usr/bin/upx
ENV JAVA_HOME=/opt/graalvm GRAALVM_HOME=/opt/graalvm
RUN microdnf --setopt=install_weak_deps=0 --setopt=tsflags=nodocs install -y fontconfig freetype-devel && microdnf clean all && rpm -q fontconfig freetype-devel # buildkit
COPY graalvm-jdk-24_linux-x64_bin.tar.gz /tmp/graalvm.tar.gz
RUN mkdir -p /opt/graalvm && tar xzf /tmp/graalvm.tar.gz -C /opt/graalvm --strip-components=1 && rm -Rf /tmp/graalvm.tar.gz # buildkit
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/graalvm/bin
RUN microdnf clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum # buildkit
RUN [ ! -d /tmp/artifacts ] || rm -rf /tmp/artifacts # buildkit
USER 1001
WORKDIR /project
RUN java -version
RUN native-image --version
RUN upx --version
ENTRYPOINT ["native-image"]
