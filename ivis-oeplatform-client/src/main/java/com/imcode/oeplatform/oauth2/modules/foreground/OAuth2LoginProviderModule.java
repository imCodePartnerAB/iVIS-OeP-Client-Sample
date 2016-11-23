package com.imcode.oeplatform.oauth2.modules.foreground;

/**
 * Created by vitaly on 02.09.15.
 */

import com.imcode.services.UserService;
import imcode.services.IvisServiceFactory;
import imcode.services.restful.*;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.hierarchy.core.annotations.*;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleProviderDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.*;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.login.LoginEvent;
import se.unlogic.hierarchy.foregroundmodules.userproviders.SimpleUser;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class OAuth2LoginProviderModule extends AnnotatedForegroundModule implements LoginProvider {
    private static final String CLIENT_CONTEXT_PARAM_NAME = "OAuth2ClientContext";
    private static final String ACCESS_TOKEN_REQUEST_PARAM_NAME = "OAuth2AccessTokenRequest";
    private static final String DEFAULT_ACCESS_TOKEN_PATH = "/oauth/token";
    private static final String DEFAULT_CLIENT_SCOPE_STRING = "read\nwrite";
    private static final String DEFAULT_AUTHORIZATION_PATH = "/oauth/authorize";
    private static final String SERVICE_FACTORY_NAME = "OAuth2ServiceFactory";
    private static final String DEFAULT_API_PATH = "/api/v1/json";
    private static final String ROW_SPLITER = "\n"; //Regexp splitter
    private static final String PAIR_SPLITER = "[=:]"; //Regexp splitter
    private static final String VALUE_SPLITER = "[,;]"; //Regexp splitter

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server Url", description = "iVIS server url \"http://localhost:8080\"", required = true)
    protected String serverUrl;


    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Client id", description = "iVIS client id", required = true)
    protected String clientId;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Client secret", description = "iVIS secret", required = true)
    protected String clientSecret;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server access token path", description = "iVIS server access token path to retrive new access token \"/oauth/token\";", required = true)
    protected String accessTokenPath = DEFAULT_ACCESS_TOKEN_PATH;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server authorization path", description = "iVIS server authorization path to retrive new authorization code \"/oauth/authorize\";", required = true)
    protected String authorizationPath = DEFAULT_AUTHORIZATION_PATH;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server API path", description = "iVIS server API path to negotiating with server \"/api/v1/json\";", required = true)
    protected String apiPath = DEFAULT_API_PATH;

    @ModuleSetting
    @TextAreaSettingDescriptor(name = "Client scopeList", description = "iVIS client scopeList", required = true)
    protected String clientScope = DEFAULT_CLIENT_SCOPE_STRING;

    @ModuleSetting(id = "userTimeout")
    @TextFieldSettingDescriptor(id = "userTimeout", name = "User session timeout", description = "Session timeout for normal users (in minutes)", required = true, formatValidator = PositiveStringIntegerValidator.class)
    protected int userSessionTimeout = 30;

    @ModuleSetting(id = "adminTimeout")
    @TextFieldSettingDescriptor(id = "adminTimeout", name = "Admin session timeout", description = "Session timeout for administrators (in minutes)", required = true, formatValidator = PositiveStringIntegerValidator.class)
    protected int adminSessionTimeout = 60;

    @ModuleSetting
    @TextAreaSettingDescriptor(name = "Logout module aliases", description = "The aliases of the logout modules (one per line)", required = true)
    protected String logoutModuleAliases = "/logout\n/logout/logout";

    @ModuleSetting(id = "default")
    @CheckboxSettingDescriptor(id = "default", name = "Add to login handler", description = "Controls if this module should add itself to the login handler as a login provider")
    protected boolean addToLoginHandler = true;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Login provider priority", description = "The priority of the login provider from this module (lower value means higher priority)", required = true, formatValidator = NonNegativeStringIntegerValidator.class)
    protected int priority = 100;

    @ModuleSetting(allowsNull = true)
    @TextFieldSettingDescriptor(name = "New password module alias", description = "The full alias of the new password module", required = false)
    protected String newPasswordModuleAlias;

    @ModuleSetting(allowsNull = true)
    @TextFieldSettingDescriptor(name = "Registration module alias", description = "The full alias of the registration module", required = false)
    protected String registrationModuleAlias;

    @ModuleSetting(allowsNull = true)
    @TextFieldSettingDescriptor(name = "Default redirect alias", description = "The full alias that users should be redirected to after login unless a redirect paramater is present in the URL. If this value is not set and no redirect paramater is present users will be redirected to the root of the context path.", required = false)
    protected String defaultRedirectAlias;

    @ModuleSetting(allowsNull = true)
    @TextAreaSettingDescriptor(name = "Role name to group name map", description = "", required = false)
    protected String roleGroupString;

    @ModuleSetting(allowsNull = true)
    @GroupMultiListSettingDescriptor(name = "Default groups", description = "Groups would be added for default to new user")
    protected List<Integer> defaultGroupIDs = Collections.EMPTY_LIST;

    private Map<String, List<String>> groupNameMap;

    private List<String> scopeList;

    protected List<String> logoutModuleAliasesList;

    protected ProviderDescriptor providerDescriptor;

    private UserHandler userHandler;

    private GroupHandler groupHandler;

    private OAuth2ProtectedResourceDetails resource;

    private AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();

    @Override
    public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
        super.init(moduleDescriptor, sectionInterface, dataSource);
    }

    private OAuth2ProtectedResourceDetails createAuthorizationCodeResourceDetails() {
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setClientId(clientId);
        resource.setGrantType("authorization_code");
        resource.setClientSecret(clientSecret);
        resource.setAccessTokenUri(serverUrl + accessTokenPath);
        resource.setScope(scopeList);
        resource.setUserAuthorizationUri(serverUrl + DEFAULT_AUTHORIZATION_PATH);

        return resource;
    }

    @Override
    public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user) throws Exception {
        return false;
    }

    @Override
    protected void parseSettings(MutableSettingHandler mutableSettingHandler) throws Exception {

        super.parseSettings(mutableSettingHandler);

        this.userHandler = systemInterface.getUserHandler();
        this.groupHandler = systemInterface.getGroupHandler();

        if (logoutModuleAliases != null) {

            logoutModuleAliasesList = Arrays.asList(logoutModuleAliases.split(ROW_SPLITER));
        }

        if (addToLoginHandler) {

            this.sectionInterface.getSystemInterface().getLoginHandler().addProvider(this);

        } else {

            this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);
        }

        if (clientScope != null) {
            scopeList = Arrays.asList(clientScope.split("\n"));
            scopeList = scopeList.stream().map(String::trim).collect(Collectors.toList());
        }

        groupNameMap = mapGroups(roleGroupString);


    }

    private static Map<String, List<String>> mapGroups(String roleGroupString) {
        Map<String, List<String>> groupNameMap = new HashMap<>();

        if (roleGroupString != null && !roleGroupString.isEmpty()) {
            String[] pairStrings = roleGroupString.split(ROW_SPLITER);
            for (String pairString : pairStrings) {
                String[] pair = pairString.split(PAIR_SPLITER);
                if (pair.length > 0 && pair.length < 3 && !pair[0].trim().isEmpty()) {
                    List<String> values = Collections.EMPTY_LIST;
                    if (pair.length > 1) {
                        values = Arrays.stream(pair[1].split(VALUE_SPLITER)).map(String::trim).collect(Collectors.toList());
                    }
                    groupNameMap.put(pair[0].trim(), values);
                }
            }
        }

        return groupNameMap;
    }

    public static void main(String[] args) {
        Map<String, List<String>> m = mapGroups("ROLE_ADMIN=Systemadministratörer, Medborgare, E-tjänst administratörer\nROLE_DEVELOPER=E-tjänst administratörer\n\nasdfasdf\n\n=\nROLE_USER = User");
        System.out.println(m);
    }

    protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request,
                                HttpServletResponse response) throws IOException {

        String redirectUri = e.getRedirectUri();
        StringBuilder builder = new StringBuilder(redirectUri);
        Map<String, String> requestParams = e.getRequestParams();
        char appendChar = redirectUri.indexOf('?') < 0 ? '?' : '&';
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            try {
                builder.append(appendChar).append(param.getKey()).append('=')
                        .append(URLEncoder.encode(param.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException(uee);
            }
            appendChar = '&';
        }

        if (e.getStateKey() != null) {
            builder.append(appendChar).append("state").append('=').append(e.getStateKey());
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, builder.toString());

    }

    /**
     * Acquire or renew an access token for the current context if necessary. This method will be called automatically
     * when a request is executed (and the result is cached), but can also be called as a standalone method to
     * pre-populate the token.
     *
     * @return an access token
     */
    public OAuth2AccessToken getAccessToken(OAuth2ClientContext context) throws UserRedirectRequiredException {

        OAuth2AccessToken accessToken = context.getAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
            try {
                accessToken = acquireAccessToken(context);
            } catch (UserRedirectRequiredException e) {
                context.setAccessToken(null); // No point hanging onto it now
                accessToken = null;
                String stateKey = e.getStateKey();
                if (stateKey != null) {
                    Object stateToPreserve = e.getStateToPreserve();
                    if (stateToPreserve == null) {
                        stateToPreserve = "NONE";
                    }
                    context.setPreservedState(stateKey, stateToPreserve);
                }
                throw e;
            }
        }
        return accessToken;
    }


    protected OAuth2AccessToken acquireAccessToken(OAuth2ClientContext oauth2Context)
            throws UserRedirectRequiredException {

        AccessTokenRequest accessTokenRequest = oauth2Context.getAccessTokenRequest();
        if (accessTokenRequest == null) {
            throw new AccessTokenRequiredException(
                    "No OAuth 2 security context has been established. Unable to access resource '"
                            + this.resource.getId() + "'.", resource);
        }

        // Transfer the preserved state from the (longer lived) context to the current request.
        String stateKey = accessTokenRequest.getStateKey();
        if (stateKey != null) {
            accessTokenRequest.setPreservedState(oauth2Context.removePreservedState(stateKey));
        }

        OAuth2AccessToken existingToken = oauth2Context.getAccessToken();
        if (existingToken != null) {
            accessTokenRequest.setExistingToken(existingToken);
        }

        OAuth2AccessToken accessToken = null;
        accessToken = accessTokenProvider.obtainAccessToken(resource, accessTokenRequest);
        if (accessToken == null || accessToken.getValue() == null) {
            throw new IllegalStateException(
                    "Access token provider returned a null access token, which is illegal according to the contract.");
        }
        oauth2Context.setAccessToken(accessToken);
        return accessToken;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {
        try {
            OAuth2ClientContext clientContext = getClientContext(req);
            getAccessToken(clientContext);
            IvisServiceFactory factory = createIvisServiceFactory(clientContext);
            UserService ivisUserService = factory.getService(UserService.class);
            com.imcode.entities.User ivisUser = ivisUserService.getCurrentUser();

            if (ivisUser != null) {
                IvisOAuth2User oAuth2User = null;
                User hierachyUser = userHandler.getUserByAttribute(IvisOAuth2User.IVIS_ATTRIBUTE_NAME, ivisUser.getId().toString(), true, true);

                if (hierachyUser == null) {
                    SimpleUser newHierachyUser = new SimpleUser();
                    hierachyUser = newHierachyUser;

                    oAuth2User = new IvisOAuth2User(hierachyUser, factory);
                    Set<Group> defaultGroupSet = defaultGroupIDs != null ? new LinkedHashSet<>(groupHandler.getGroups(defaultGroupIDs, false)) : Collections.EMPTY_SET;

                    ByNameGroupPopulator groupPupulator = new ByNameGroupPopulator(groupHandler, groupNameMap, defaultGroupSet);
                    IvisUserPopulator userPopulator = new IvisUserPopulator(groupPupulator);
                    userPopulator.populate(oAuth2User, ivisUser);
                    userHandler.addUser(hierachyUser);
                } else {
                    oAuth2User = new IvisOAuth2User(hierachyUser, factory);
                }

                if (oAuth2User.isEnabled()) {

                    setLoggedIn(req, uriParser, oAuth2User);

                    return this.sendRedirect(req, res, uriParser, oAuth2User);

                } else {
                    log.warn("Login refused for user " + oAuth2User + " (account disabled) accessing from address " + req.getRemoteHost());

                    Document doc = this.createDocument(req, uriParser);

                    doc.getDocumentElement().appendChild(doc.createElement("AccountDisabled"));

                    return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
                }
            }
        } catch (UserRedirectRequiredException e) {
            redirectUser(e, req, res);
        }

        return new SimpleForegroundModuleResponse("ОЙ, Что-то случилось!!! :(");
    }

    private IvisServiceFactory createIvisServiceFactory(OAuth2ClientContext clientContext) {
        ProxyIvisServiceFactoryBuilder builder = new ProxyIvisServiceFactoryBuilder();
        builder.setApiUrl(serverUrl + apiPath)
                .setClientContext(clientContext)
                .setClient(resource);

        return builder.build();

//        ProxyIvisServiceFactory factory = new ProxyIvisServiceFactory(serverUrl + apiPath, clientContext, resource);
//        AbstractOAuth2Service pupilService = new OAuth2PupilService(factory, factory.getApiUrl() + "/pupils");
//        AbstractOAuth2Service guardianService = new OAuth2GuardianService(factory, factory.getApiUrl() + "/guardians");
//        AbstractOAuth2Service personService = new OAuth2PersonService(factory, factory.getApiUrl() + "/persons");
//        AbstractOAuth2Service userService = new OAuth2UserService(factory, factory.getApiUrl() + "/users");
//        factory.setFavoriteServiceList(Arrays.asList(pupilService, guardianService, personService, userService));
//        factory.initialize();
//
//        return factory;
    }


    private OAuth2ClientContext getClientContext(HttpServletRequest req) {
        OAuth2ClientContext clientContext = null;
        HttpSession session = req.getSession();
        Object value = session.getAttribute(CLIENT_CONTEXT_PARAM_NAME);
        AccessTokenRequest accessTokenRequest = getAccessTokenRequest(req);

        if (value instanceof OAuth2ClientContext) {
            clientContext = (OAuth2ClientContext) value;
        } else {
            clientContext = new DefaultOAuth2ClientContext(accessTokenRequest);
            session.setAttribute(CLIENT_CONTEXT_PARAM_NAME, clientContext);
        }

        AccessTokenRequest contextRequest = clientContext.getAccessTokenRequest();
        contextRequest.clear();
        Map<String, List<String>> params = new HashMap<>();

        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            params.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        contextRequest.putAll(params);
//        if (accessTokenRequest.getAuthorizationCode() != null) {
//            contextRequest.setAuthorizationCode(accessTokenRequest.getAuthorizationCode());
//        }

//        if (accessTokenRequest.getStateKey() != null) {
//            contextRequest.setStateKey(accessTokenRequest.getStateKey());
//        }

//        if (accessTokenRequest.getCurrentUri() != null) {
        contextRequest.setCurrentUri(accessTokenRequest.getCurrentUri());
//        }

        return clientContext;
    }

    private AccessTokenRequest getAccessTokenRequest(HttpServletRequest req) {
        Object value = req.getAttribute(ACCESS_TOKEN_REQUEST_PARAM_NAME);

        if (value instanceof AccessTokenRequest) {
            return (AccessTokenRequest) value;
        }

        DefaultAccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest(req.getParameterMap());
        String currentUrl = calculateCurrentUri(req);
        accessTokenRequest.setCurrentUri(currentUrl);

        req.setAttribute(ACCESS_TOKEN_REQUEST_PARAM_NAME, accessTokenRequest);

        return accessTokenRequest;
    }

    public void setLoggedIn(HttpServletRequest req, URIParser uriParser, User loginUser) throws Exception {

        // Set last login timestamp
//        this.setLastLogin(loginUser);

        HttpSession session = req.getSession(true);

        session.setAttribute("user", loginUser);
        session.removeAttribute("usedRetries");

        session.removeAttribute(CLIENT_CONTEXT_PARAM_NAME);
        req.removeAttribute(ACCESS_TOKEN_REQUEST_PARAM_NAME);

        // Set session timeout
        if (loginUser.isAdmin()) {
            session.setMaxInactiveInterval(this.adminSessionTimeout * 60);
        } else {
            session.setMaxInactiveInterval(this.userSessionTimeout * 60);
        }

        log.info("User " + loginUser + " logged in from address " + req.getRemoteHost());

        systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(loginUser, session), EventTarget.ALL);
    }

    public SimpleForegroundModuleResponse sendRedirect(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, User loginUser) throws Exception {

        String redirectUrl = getRedirectUri(req);

        if (redirectUrl != null) {
            res.sendRedirect(redirectUrl);
        }

        return null;
    }

    protected String calculateCurrentUri(HttpServletRequest request) {//} throws UnsupportedEncodingException {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequest(request);
        // Now work around SPR-10172...
        String queryString = request.getQueryString();
        boolean legalSpaces = queryString != null && queryString.contains("+");
        if (legalSpaces) {
            builder.replaceQuery(queryString.replace("+", "%20"));
        }
        UriComponents uri = null;
        try {
            uri = builder.replaceQueryParam("code").build(true);
        } catch (IllegalArgumentException ex) {
            // ignore failures to parse the url (including query string). does't make sense
            // for redirection purposes anyway.
            return null;
        }
        String query = uri.getQuery();
        if (legalSpaces) {
            query = query.replace("%20", "+");
        }
        return ServletUriComponentsBuilder.fromUri(uri.toUri()).replaceQuery(query).build().toString();
    }

    @SuppressWarnings("unchecked")
    private String getRedirectUri(HttpServletRequest req) {
        String redirectParam = null;
        try {
            redirectParam = URLDecoder.decode(req.getParameter("redirect"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (redirectParam != null && redirectParam.startsWith("/")) {

            return req.getContextPath() + redirectParam;

        } else if (defaultRedirectAlias != null) {

            return req.getContextPath() + defaultRedirectAlias;

        } else {

            if (StringUtils.isEmpty(req.getContextPath())) {

                return "/";

            } else {

                return req.getContextPath();
            }
        }
    }

    protected Document createDocument(HttpServletRequest req, URIParser uriParser) {

        Document doc = XMLUtils.createDomDocument();
        Element document = doc.createElement("document");
        doc.appendChild(document);
        document.appendChild(this.moduleDescriptor.toXML(doc));
        XMLUtils.appendNewCDATAElement(doc, document, "newPasswordModuleAlias", this.newPasswordModuleAlias);
        XMLUtils.appendNewCDATAElement(doc, document, "registrationModuleAlias", this.registrationModuleAlias);
        XMLUtils.appendNewCDATAElement(doc, document, "uri", req.getContextPath() + uriParser.getFormattedURI());
        XMLUtils.appendNewCDATAElement(doc, document, "redirect", req.getParameter("redirect"));
        XMLUtils.appendNewCDATAElement(doc, document, "contextpath", req.getContextPath());

        return doc;
    }

    @Override
    public int getPriority() {

        return priority;
    }


    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable {

        if (redirectURI != null) {

            res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirectURI, "ISO-8859-1"));

        } else {

            redirectToDefaultMethod(req, res);
        }
    }

    @Override
    protected void moduleConfigured() throws Exception {

//        retryLimiter = new RetryLimiter(loginLockoutActivated, loginLockoutTime, loginRetries, loginRetryInterval);

        providerDescriptor = new SimpleProviderDescriptor(moduleDescriptor);
        resource = createAuthorizationCodeResourceDetails();
    }

    @Override
    public void unload() throws Exception {

        this.sectionInterface.getSystemInterface().getLoginHandler().removeProvider(this);

        super.unload();
    }

    @Override
    public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

        return this.processRequest(req, res, user, uriParser);
    }

    @Override
    public ProviderDescriptor getProviderDescriptor() {

        return providerDescriptor;
    }


    @Override
    public boolean supportsRequest(HttpServletRequest req, URIParser uriParser) throws Throwable {
        return true;
    }
}

