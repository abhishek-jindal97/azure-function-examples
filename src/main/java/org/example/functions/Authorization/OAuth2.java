package org.example.functions.Authorization;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OAuth2 extends AbstractAppEngineAuthorizationCodeServlet {

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher requestBodyPublisher = HttpRequest.BodyPublishers.ofString("grant_type=authorization_code");
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.google.com/o/oauth2/v2/auth")).POST(requestBodyPublisher).build();

        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl("xyz"),
                new BasicAuthentication("abhishek","abhishek"),
                "xyz",
                "https://localhost:8081/authorize")
                .setCredentialDataStore(StoredCredential.getDefaultDataStore
                        (new FileDataStoreFactory(new File("credentials"))));
        return builder.build();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest request) throws ServletException, IOException {
        GenericUrl redirectUrl = new GenericUrl(request.getRequestURL().toString());
        redirectUrl.setRawPath("/oauth2Callback");
        return redirectUrl.build();
    }

}
