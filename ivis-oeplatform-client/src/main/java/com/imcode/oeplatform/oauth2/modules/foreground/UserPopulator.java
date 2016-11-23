package com.imcode.oeplatform.oauth2.modules.foreground;

import se.unlogic.hierarchy.core.beans.Group;

/**
 * Created by vitaly on 04.09.15.
 */
public interface UserPopulator<SourceUserType, TargetUserType extends se.unlogic.hierarchy.core.beans.User> {
    void populate(TargetUserType targetUser, SourceUserType sourceUser);

    GroupPopulator<SourceUserType, ? extends Group> getGroupPopulator();
}
