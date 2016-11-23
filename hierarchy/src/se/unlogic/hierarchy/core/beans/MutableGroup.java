package se.unlogic.hierarchy.core.beans;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.annotations.WebPopulate;

public abstract class MutableGroup extends Group{

	private static final long serialVersionUID = -4445466850235052502L;

	public abstract void setGroupID(Integer groupID);
	
	@WebPopulate(required = true, maxLength = 255)
	public abstract void setName(String name);

	@WebPopulate(required = true, maxLength = 255)
	public abstract void setDescription(String description);

	@WebPopulate
	public abstract void setEnabled(boolean enabled);
	
	/**
	 * @return A {@link MutableAttributeHandler} or null if the current implementation does not support this feature.
	 */
	@Override
	public MutableAttributeHandler getAttributeHandler(){

		return null;
	}	
}