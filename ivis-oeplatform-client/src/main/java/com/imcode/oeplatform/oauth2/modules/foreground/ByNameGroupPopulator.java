package com.imcode.oeplatform.oauth2.modules.foreground;

import com.imcode.entities.Role;
import com.imcode.entities.User;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.handlers.GroupHandler;

import java.util.*;

/**
 * Created by vitaly on 04.09.15.
 */
public class ByNameGroupPopulator implements GroupPopulator<User, Group> {
    private Set<Group> defaultGroupSet;
    private Map<String, List<String>> groupNameMap;
    private GroupHandler groupHandler;

    public ByNameGroupPopulator(GroupHandler groupHandler, Map<String, List<String>> groupNameMap,  Set<Group> defaultGroupSet) {
        this.defaultGroupSet = defaultGroupSet;
        this.groupNameMap = groupNameMap;
        this.groupHandler = groupHandler;
    }

    public ByNameGroupPopulator(GroupHandler groupHandler, Map<String, List<String>> groupNameMap) {
        this(groupHandler, groupNameMap, Collections.emptySet());
    }

    @Override
    public List<Group> populate(User sourceUser) {
        Set<Group> groupSet = new LinkedHashSet<>(defaultGroupSet);
        Set<Role> roleList = sourceUser.getAuthorities();
        List<Group> allGroups = groupHandler.getGroups(false);

        if (roleList != null) {
            Set<String> groupNames = new HashSet<>();
            roleList.stream()
                    .filter(role -> groupNameMap.containsKey(role.getName()))
                    .forEach(role -> groupNames.addAll(groupNameMap.get(role.getName())));

            allGroups.stream().filter(group -> groupNames.contains(group.getName())).forEach(group -> groupSet.add(group));
        }

        return new ArrayList<>(groupSet);
    }

    public Set<Group> getDefaultGroupSet() {
        return defaultGroupSet;
    }

    public void setDefaultGroupSet(Set<Group> defaultGroupSet) {
        this.defaultGroupSet = defaultGroupSet;
    }

    public Map<String, List<String>> getGroupNameMap() {
        return groupNameMap;
    }

    public void setGroupNameMap(Map<String, List<String>> groupNameMap) {
        this.groupNameMap = groupNameMap;
    }

//    @Override
//    public List<Group> populate(User sourceUser) {
//        return null;
//    }
}
