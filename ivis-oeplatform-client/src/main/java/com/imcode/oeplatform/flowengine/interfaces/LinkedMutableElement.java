package com.imcode.oeplatform.flowengine.interfaces;

import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLParserPopulateable;

import java.io.Serializable;

/**
 * Created by vitaly on 21.09.15.
 */
public interface LinkedMutableElement<ID extends Serializable> extends Elementable, XMLParserPopulateable {
    ID getId();

    void setId(ID id);

    String getRepresentation();

    void setRepresentation(String representation);
}
