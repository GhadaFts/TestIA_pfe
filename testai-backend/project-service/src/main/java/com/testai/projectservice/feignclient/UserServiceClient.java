package com.testai.projectservice.feignclient;

import com.testai.projectservice.config.FeignClientConfig;
import com.testai.projectservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign Client pour communiquer avec user-service
 *
 * @FeignClient:
 * - name: nom du service dans Eureka (USER-SERVICE)
 * - path: préfixe des endpoints (/api/users)
 */
@FeignClient(name = "user-service", path = "/api/users", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    /**
     * Récupérer un utilisateur par son ID
     *
     * Appelle : GET http://user-service/api/users/{id}
     *
     * @param userId UUID de l'utilisateur
     * @return UserDTO ou exception si non trouvé
     */
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") UUID userId);
}