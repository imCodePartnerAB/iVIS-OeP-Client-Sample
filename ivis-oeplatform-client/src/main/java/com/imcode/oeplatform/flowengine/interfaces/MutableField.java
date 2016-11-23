package com.imcode.oeplatform.flowengine.interfaces;

public interface MutableField {

	boolean isExported();

	void setExported(boolean exported);

	String getXsdElementName();

	void setXsdElementName(String xsdElementName);

    String getLabel();

    void setLabel(String name);

    Integer getSortIndex();

    void setSortIndex(Integer sortIndex);

}
