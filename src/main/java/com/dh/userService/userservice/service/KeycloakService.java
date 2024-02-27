package com.dh.userService.userservice.service;

import com.dh.userService.userservice.config.KeycloakClientConfig;
import com.dh.userService.userservice.entities.AccessKeycloak;
import com.dh.userService.userservice.entities.Login;
import com.dh.userService.userservice.entities.User;
import com.dh.userService.userservice.entities.dto.UserKeycloak;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KeycloakService {
    @Autowired
    private KeycloakClientConfig keycloakClientConfig;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.serverUrl}")
    private String serverUrl;
    @Value("${keycloak.clientId}")
    private String clientId;
    @Value("${keycloak.clientSecret}")
    private String clientSecret;
    @Value("${keycloak.tokenEndpoint}")
    private String tokenEndpoint;

    public RealmResource getRealm() {
        return keycloakClientConfig.getInstance().realm(realm);
    }


    public UserKeycloak createUser(UserKeycloak userKeycloak) throws Exception {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userKeycloak.userName());
        userRepresentation.setEmail(userKeycloak.email());
        userRepresentation.setFirstName(userKeycloak.name());
        userRepresentation.setLastName(userKeycloak.lastName());
        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userKeycloak.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> credentialRepresentationList = new ArrayList<>();
        credentialRepresentationList.add(credentialRepresentation);

        userRepresentation.setCredentials(credentialRepresentationList);


        Response response = getRealm().users().create(userRepresentation);


        if(response.getStatus() == 409) {
            throw new Exception("(!) User already exists");
        }

        if (response.getStatus() >= 400) {
            throw new BadRequestException("(!) something happened, try again later");
        }

        List<UserRepresentation> emailsFound = getRealm().users().searchByEmail(userKeycloak.email(), true);
        if(emailsFound.isEmpty()) {
            System.out.println("No hay emails registrados");
        }

        List<UserRepresentation> userRepresentations = getRealm().users().searchByUsername(userKeycloak.userName(), true);
        if(!CollectionUtils.isEmpty(userRepresentations)) {
            UserRepresentation userRepresentation1 = userRepresentations.stream().filter(userRep -> Objects.equals(false, userRep.isEmailVerified())).findFirst().orElse(null);
            assert userRepresentation1 != null;
            emailVerification(userRepresentation1.getId());
            System.out.println("------ EMAIL ENVIADO A USUARIO " + userRepresentation1.getId());
        }

       // userRepresentation.setId(CreatedResponseUtil.getCreatedId(response));

        return userKeycloak;
    }


    public AccessKeycloak login(Login login) throws Exception {
        try{

            AccessKeycloak tokenAccess = null;
            Keycloak keycloakClient = null;
            TokenManager tokenManager = null;

            keycloakClient = Keycloak.getInstance(serverUrl,realm,login.getEmail(), login.getPassword(), clientId, clientSecret);

            tokenManager = keycloakClient.tokenManager();

            tokenAccess = AccessKeycloak.builder()
                    .accessToken(tokenManager.getAccessTokenString())
                    .expiresIn(tokenManager.getAccessToken().getExpiresIn())
                    .refreshToken(tokenManager.refreshToken().getRefreshToken())
                    .scope(tokenManager.getAccessToken().getScope())
                    .build();

            return tokenAccess;

        }  catch (Exception e) {
            throw new AuthenticationException("Invalid Credentials");
        }
    }

    public void logout(String userId) {
        getRealm().users().get(userId).logout();
    }

    public void emailVerification(String userId) {
        UsersResource usersResource = getRealm().users();
        usersResource.get(userId).sendVerifyEmail();
    }

/*
    private UsersResource getUsersResource() {
        RealmResource realmUsed = keycloak.realm(realm);
        UsersResource usersResource = realmUsed.users();
        return usersResource;
    }
*/
}
