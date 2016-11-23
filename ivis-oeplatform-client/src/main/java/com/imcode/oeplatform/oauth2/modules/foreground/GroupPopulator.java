package com.imcode.oeplatform.oauth2.modules.foreground;

import se.unlogic.hierarchy.core.beans.Group;

import java.util.List;

/**
 * Created by vitaly on 04.09.15.
 */
public interface GroupPopulator<SourceUserType, GroupType extends Group> {
    List<GroupType> populate(SourceUserType sourceUser);
}
