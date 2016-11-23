package com.imcode.oeplatform.oauth2.modules.foreground;

import imcode.services.IvisServiceFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.foregroundmodules.userproviders.SimpleUser;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.i18n.Language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Created by vitaly on 03.09.15.
 */
@Table(name = "simple_users")
public class IvisOAuth2User<UserType extends User> extends User {
    public static final String IVIS_ATTRIBUTE_NAME = "ivisUserId";

    private UserType user;
    private IvisServiceFactory serviceFactory;
    private OAuth2ClientContext clientContext;

    public IvisOAuth2User(UserType user, IvisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.clientContext = serviceFactory.getClientContext();
        this.user = user;
    }

    public UserType getUser() {
        return user;
    }

    public void setUser(UserType user) {
        this.user = user;
    }

    public IvisServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(IvisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public OAuth2ClientContext getClientContext() {
        return clientContext;
    }

    public void setClientContext(OAuth2ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public boolean isAdmin() {
        return user.isAdmin();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getFirstname() {
        return user.getFirstname();
    }

    @Override
    public Timestamp getCurrentLogin() {
        return user.getCurrentLogin();
    }

    @Override
    public Timestamp getLastLogin() {
        return user.getLastLogin();
    }

    @Override
    public String getLastname() {
        return user.getLastname();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Integer getUserID() {
        return user.getUserID();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public Timestamp getAdded() {
        return user.getAdded();
    }

    @Override
    public Collection<Group> getGroups() {
        return user.getGroups();
    }

    @Override
    public Language getLanguage() {
        return user.getLanguage();
    }

    @Override
    public String getPreferedDesign() {
        return user.getPreferedDesign();
    }

    @Override
    public boolean hasFormProvider() {
        return user.hasFormProvider();
    }

    @Override
    public void setCurrentLogin(Timestamp currentLogin) {
        user.setCurrentLogin(currentLogin);
    }
}

