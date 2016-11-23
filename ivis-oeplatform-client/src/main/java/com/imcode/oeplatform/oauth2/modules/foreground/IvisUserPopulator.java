package com.imcode.oeplatform.oauth2.modules.foreground;

import com.imcode.entities.Person;
import com.imcode.entities.Role;
import com.imcode.entities.User;
import com.imcode.entities.embed.Email;
import com.imcode.entities.enums.CommunicationTypeEnum;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.interfaces.AttributeSource;
import se.unlogic.standardutils.i18n.Language;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by vitaly on 04.09.15.
 */
public class IvisUserPopulator implements UserPopulator<User, IvisOAuth2User<MutableUser>> {
    private static final String DEFAULT_ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private static final String DEFAULT_PASSWORD = "f7f76321-6a2b-4941-be0a-2ef0c8e70c25";
    private static final Language DEFAULT_LANGUAGE = Language.English;

    private Language language = DEFAULT_LANGUAGE;
    private GroupPopulator groupPopulator;
    private String password = DEFAULT_PASSWORD;
    private String adminRoleName = DEFAULT_ADMIN_ROLE_NAME;

    IvisUserPopulator(GroupPopulator groupPopulator) {
        this.groupPopulator = groupPopulator;
    }

    @Override
    public void populate(IvisOAuth2User<MutableUser> targetUser, User sourceUser) {

        MutableUser user = targetUser.getUser();
        if (user != null) {

            user.setUsername(validateUsername(sourceUser.getUsername()));
            user.setEnabled(sourceUser.isEnabled());
            user.setAdmin(isAdmin(sourceUser));
            user.setLanguage(language);
            user.setPassword(password);
            user.setCurrentLogin(Timestamp.valueOf(LocalDateTime.now()));
            if (user instanceof AttributeSource) {
                AttributeSource attributeSource = (AttributeSource) user;
                attributeSource.addAttribute(targetUser.IVIS_ATTRIBUTE_NAME, sourceUser.getId().toString());
            }

            if (groupPopulator != null) {
                user.setGroups(groupPopulator.populate(sourceUser));
            }

            Person person = sourceUser.getPerson();

            if (person != null) {
                String firstName = isBlank(person.getFirstName()) ? sourceUser.getUsername() : person.getFirstName();
                user.setFirstname(firstName);
                user.setLastname(blankIfNull(person.getLastName()));

                Map<CommunicationTypeEnum, Email> emails = person.getEmails();

                if (emails != null && !emails.isEmpty()) {
                    Email email = emails.values().iterator().next();
                    user.setEmail(email.getAddress());
                }
            } else {
                user.setFirstname(sourceUser.getUsername());
                user.setLastname("");
                user.setEmail("");
            }
        }
    }

    private boolean isAdmin(User sourceUser) {
        Set<Role> sourceRoleSet = sourceUser.getAuthorities();
        if (sourceRoleSet != null && !sourceRoleSet.isEmpty()) {
            for (Role role : sourceRoleSet) {
                if (adminRoleName.equals(role.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String validateUsername(String username) {
        username = username.trim();
        if (username == null || username.trim().length() == 0) {
            username = UUID.randomUUID().toString();
        }

        return username;
    }


    private static String blankIfNull(String string) {
        return string == null ? "" : string;
    }

    private static boolean isBlank(String s) {
        int strLen;

        if (s == null || (strLen = s.length()) == 0) {
            return true;
        }

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GroupPopulator<User, ? extends Group> getGroupPopulator() {
        return groupPopulator;
    }

    public void setGroupPopulator(GroupPopulator groupPopulator) {
        this.groupPopulator = groupPopulator;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminRoleName() {
        return adminRoleName;
    }

    public void setAdminRoleName(String adminRoleName) {
        this.adminRoleName = adminRoleName;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
