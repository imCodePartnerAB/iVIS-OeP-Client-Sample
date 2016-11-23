package com.nordicpeak.flowengine.beans;

import java.lang.reflect.Field;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;

@Table(name = "flowengine_internal_messages")
@XMLElement
public class InternalMessage extends BaseMessage {

	public static final Field ATTACHMENTS_RELATION = ReflectionUtils.getField(InternalMessage.class, "attachments");
	public static final Field FLOWINSTANCE_RELATION = ReflectionUtils.getField(InternalMessage.class, "flowInstance");
	
	@DAOManaged(columnName="flowInstanceID")
	@ManyToOne
	@XMLElement
	private FlowInstance flowInstance;

	@DAOManaged
	@OneToMany(autoAdd=true)
	@XMLElement
	private List<InternalMessageAttachment> attachments;

	@DAOManaged
	@OneToMany
	@XMLElement
	private List<InternalMessageReadReceipt> readReceipts;

	@Override
	public List<InternalMessageAttachment> getAttachments() {

		return attachments;
	}

	public void setAttachments(List<InternalMessageAttachment> attachments) {

		this.attachments = attachments;
	}

	@Override
	public List<InternalMessageReadReceipt> getReadReceipts() {

		return readReceipts;
	}

	public void setReadReceipts(List<InternalMessageReadReceipt> readRecipts) {

		this.readReceipts = readRecipts;
	}

	@Override
	public FlowInstance getFlowInstance() {

		return flowInstance;
	}

	public void setFlowInstance(FlowInstance flowInstance) {

		this.flowInstance = flowInstance;
	}

}
