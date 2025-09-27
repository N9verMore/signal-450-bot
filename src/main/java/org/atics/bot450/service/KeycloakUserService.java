package org.atics.bot450.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.atics.bot450.exception.UserAlreadyExistsException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final String keycloakRealm;
    private final QrService qrService;
    private final SecurityContextRepository securityContextRepository;

    public Map<String, Object> checkStatus(HttpServletRequest request, HttpServletResponse response, String mobileNumber) {
        boolean authorized = qrService.isAuthorized(mobileNumber);
        if (authorized) {
            UserDetails userDetails = User.withUsername(mobileNumber)
                    .password("")
                    .authorities("ROLE_USER")
                    .build();

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(context, request, response);

            request.getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }
        return Map.of("authorized", authorized);
    }

    public void registerUser(String mobilePhone, String password) {
        RealmResource realmResource = keycloak.realm(keycloakRealm);
        UsersResource usersResource = realmResource.users();
        boolean existsKeycloak = !usersResource.searchByUsername(mobilePhone, true).isEmpty();
        boolean existsSignal = qrService.isAuthorized(mobilePhone);
        if (existsKeycloak && existsSignal) {
            throw new UserAlreadyExistsException("User already exists");
        }
        if(!existsKeycloak && existsSignal) {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(mobilePhone);
            user.setEnabled(true);
            user.setEmailVerified(true);

            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(password);
            user.setCredentials(Collections.singletonList(cred));

            try {
                Response resp = usersResource.create(user);
                if (resp.getStatus() == 409) {
                    throw new UserAlreadyExistsException("User already exists");
                }
            } catch (ClientErrorException e) {
                if (e.getResponse() != null && e.getResponse().getStatus() == 409) {
                    throw new UserAlreadyExistsException("User already exists");
                }
                throw e;
            }
        }
    }
}
