package com.imcode.oeplatform.flowengine.populators;

import se.unlogic.standardutils.populators.LongPopulator;
import se.unlogic.standardutils.validation.StringFormatValidator;
import se.unlogic.standardutils.validation.StringLongValidator;

/**
 * Created by vitaly on 21.09.15.
 */
public class PositiveStringLongPopulator extends LongPopulator{
    public PositiveStringLongPopulator() {
        super(null, new StringLongValidator(1L, null));
    }
}
