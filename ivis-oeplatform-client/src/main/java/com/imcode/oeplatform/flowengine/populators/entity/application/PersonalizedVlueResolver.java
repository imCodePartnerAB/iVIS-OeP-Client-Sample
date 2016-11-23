package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaPersonalizedEntity;
import com.imcode.oeplatform.flowengine.populators.entity.ValueResolver;
import com.imcode.services.PersonalizedService;

/**
 * Created by vitaly on 10.09.15.
 */
@Deprecated
public class PersonalizedVlueResolver<T extends JpaPersonalizedEntity> implements ValueResolver<T> {
    private final PersonalizedService<T> service;

    PersonalizedVlueResolver(PersonalizedService<T> service) {
        this.service = service;
    }

    @Override
    public T apply(String rawValue) {
        return service.findFirstByPersonalId(rawValue);
    }
}
