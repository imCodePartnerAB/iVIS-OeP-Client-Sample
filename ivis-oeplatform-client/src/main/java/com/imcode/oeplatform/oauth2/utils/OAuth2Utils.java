package com.imcode.oeplatform.oauth2.utils;

import imcode.services.IvisServiceFactory;
import imcode.services.restful.ProxyIvisServiceFactory;
import imcode.services.restful.ProxyIvisServiceFactoryBuilder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by vitaly on 23.09.15.
 */
public final class OAuth2Utils {

    public static final String DEFAULT_API_PATH = "/api/v1/json";
    public static final String DEFAULT_ACCESS_TOKEN_PATH = "/oauth/token";
    public static final String DEFAULT_AUTHORIZATION_PATH = "/oauth/authorize";

    private OAuth2Utils() {
    }

    public static OAuth2ProtectedResourceDetails createPsswordResourceDetails(String clientId, String clientSecret, String accessTokenUrl, List<String> clientScope, String username, String password) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setClientId(clientId);
        resource.setGrantType("password");
        resource.setClientSecret(clientSecret);
        resource.setAccessTokenUri(accessTokenUrl);
        resource.setScope(clientScope);
        resource.setUsername(username);
        resource.setPassword(password);

        return resource;
    }

    public static IvisServiceFactory createIvisServiceFactory(String apiUrl, OAuth2ProtectedResourceDetails resource) {
        ProxyIvisServiceFactoryBuilder builder = new ProxyIvisServiceFactoryBuilder();
        builder.setApiUrl(apiUrl)
                .setClient(resource);
//        OAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
//
//        ProxyIvisServiceFactory factory = new ProxyIvisServiceFactory(apiUrl, clientContext, resource);
//        factory.setFavoriteServiceList(Collections.emptyList());
//        factory.initialize();

        return builder.build();
    }

    public static Map<Node, Map> mapNodeList(NodeList nodeList) {
        Map<Node, Map> map = new LinkedHashMap<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.hasChildNodes()) {
                map.put(node, mapNodeList(node.getChildNodes()));
            } else {
                map.put(node, Collections.emptyMap());
            }

        }

        return map;
    }

}
