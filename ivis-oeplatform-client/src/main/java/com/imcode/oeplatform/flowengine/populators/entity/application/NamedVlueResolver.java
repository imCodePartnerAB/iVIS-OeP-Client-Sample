package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaNamedEntity;
import com.imcode.oeplatform.flowengine.populators.entity.ValueResolver;
import com.imcode.services.NamedService;

/**
 * Created by vitaly on 10.09.15.
 */
@Deprecated
public class NamedVlueResolver<T extends JpaNamedEntity> implements ValueResolver<T> {
    private final NamedService<T> service;

    NamedVlueResolver(NamedService<T> service) {
        this.service = service;
    }

    @Override
    public T apply(String rawValue) {
        return service.findFirstByName(rawValue);
    }
}
