package com.imcode.oeplatform.flowengine.populators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.entities.Pupil;
import se.unlogic.standardutils.string.Stringyfier;

import java.util.ArrayList;

/**
 * Created by vitaly on 27.10.15.
 */
public class JsonStringfier implements Stringyfier<Object> {
    @Override
    public String format(Object bean) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;

    }
}
