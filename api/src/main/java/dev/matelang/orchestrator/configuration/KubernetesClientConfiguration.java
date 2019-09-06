package dev.matelang.orchestrator.configuration;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;

@Configuration
public class KubernetesClientConfiguration {

    private final static String KUBE_CONFIG_PATH = "/home/mate/.kube/config";

    public ApiClient apiClient() {
        try {
            return ClientBuilder
                    .kubeconfig(KubeConfig.loadKubeConfig(new FileReader(KUBE_CONFIG_PATH)))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Kube Config!");
        }
    }

    @Bean
    public CoreV1Api coreV1Api() {
        return new CoreV1Api(this.apiClient());
    }

    @Bean
    public AppsV1Api appsV1Api() {
        return new AppsV1Api(this.apiClient());
    }

}
