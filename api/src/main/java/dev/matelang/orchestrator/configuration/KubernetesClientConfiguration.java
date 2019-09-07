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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.IOException;

@Configuration
public class KubernetesClientConfiguration {

    @Configuration
    @ConditionalOnProperty(value = "dev.matelang.orchestrator.k8s-client.kubeconfig")
    public static class KubernetesClientFileConfigurer {
        @Bean
        public ApiClient apiClientFromFile(@Value("dev.matelang.orchestrator.k8s-client.kubeconfig") String kubeConfigPath) {
            try {
                return ClientBuilder
                        .kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath)))
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read Kube Config!", e);
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "dev.matelang.orchestrator.k8s-client.kubeconfig", matchIfMissing = true)
    public static class KubernetesClientExplicitConfigurer {
        @Bean
        public ApiClient apiClientFromProperties(@Value("dev.matelang.orchestrator.k8s-client.config.basePath") String basePath,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.ca") String ca,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.auth.accessToken") String accessToken,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.auth.username") String userName,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.auth.password") String password,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.auth.clientcert.cert") String clientCert,
                                                 @Value("dev.matelang.orchestrator.k8s-client.config.auth.clientcert.key") String clientKey) {
            Authentication auth;
            if (!StringUtils.isEmpty(accessToken)) {
                auth = new AccessTokenAuthentication(accessToken);
            } else if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)) {
                auth = new UsernamePasswordAuthentication(userName, password);
            } else if (!StringUtils.isEmpty(clientCert) && !StringUtils.isEmpty(clientKey)) {
                auth = new ClientCertificateAuthentication(clientCert.getBytes(), clientKey.getBytes());
            } else {
                throw new RuntimeException("A form of authentication must be set for the K8S Client!");
            }

            try {
                return ClientBuilder
                        .standard()
                        .setAuthentication(auth)
                        .setBasePath(basePath)
                        .setCertificateAuthority(ca.getBytes())
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

}
