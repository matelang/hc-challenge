package dev.matelang.orchestrator.configuration;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;
import io.kubernetes.client.util.credentials.ClientCertificateAuthentication;
import io.kubernetes.client.util.credentials.UsernamePasswordAuthentication;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.IOException;

@Configuration
public class KubernetesClientConfiguration {

    @Configuration
    @ConditionalOnProperty(value = "dev.matelang.orchestrator.k8s-client.kubeconfigpath")
    public static class KubernetesClientFileConfigurer {

        @Bean
        public ApiClient apiClientFromFile(KubernetesClientProperties properties) {
            try {
                return ClientBuilder
                        .kubeconfig(KubeConfig.loadKubeConfig(new FileReader(properties.getKubeconfigpath())))
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read Kube Config!", e);
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "dev.matelang.orchestrator.k8s-client.config.base-path")
    @Slf4j
    public static class KubernetesClientExplicitConfigurer {
        @Bean
        public ApiClient apiClientFromProperties(KubernetesClientProperties properties) {
            Authentication auth;
            KubernetesClientProperties.Config.Auth authConfig = properties.getConfig().getAuth();
            log.info("Cfg = {}", authConfig);

            if (!StringUtils.isEmpty(authConfig.getAccessToken())) {
                auth = new AccessTokenAuthentication(authConfig.getAccessToken());
            } else if (!StringUtils.isEmpty(authConfig.getUsername()) && !StringUtils.isEmpty(authConfig.getPassword())) {
                auth = new UsernamePasswordAuthentication(authConfig.getUsername(), authConfig.getPassword());
            } else if (!StringUtils.isEmpty(authConfig.getClientCert()) && !StringUtils.isEmpty(authConfig.getClientKey())) {
                auth = new ClientCertificateAuthentication(authConfig.getClientCert().getBytes(),
                        authConfig.getClientKey().getBytes()
                );
            } else {
                throw new RuntimeException("A form of authentication must be set for the K8S Client!");
            }

            try {
                return ClientBuilder
                        .standard()
                        .setAuthentication(auth)
                        .setBasePath(properties.getConfig().getBasePath())
                        .setCertificateAuthority(properties.getConfig().getCa().getBytes())
                        .setVerifyingSsl(true)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Failed to set up K8S Client!", e);
            }
        }
    }

    @Bean
    public CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }

    @Bean
    public AppsV1Api appsV1Api(ApiClient apiClient) {
        return new AppsV1Api(apiClient);
    }

    @Component
    @Data
    @ConfigurationProperties("dev.matelang.orchestrator.k8s-client")
    public static class KubernetesClientProperties {
        private String kubeconfigpath;
        private Config config;

        @Data
        public static class Config {
            private String basePath;
            private String ca;
            private Auth auth;

            @Data
            public static class Auth {
                private String accessToken;
                private String username;
                private String password;
                private String clientCert;
                private String clientKey;
            }
        }
    }

}
