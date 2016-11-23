/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name="validationError")
public class ValidationError extends GeneratedElementable{

	@XMLElement
	private final String fieldName;

	@XMLElement
	private final String displayName;

	@XMLElement
	private final ValidationErrorType validationErrorType;

	@XMLElement
	private final String messageKey;

	public ValidationError(String fieldName, ValidationErrorType validationErrorType, String messageKey) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
		this.messageKey = messageKey;
		this.displayName = null;
	}

	public ValidationError(String fieldName, ValidationErrorType validationErrorType) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
		this.displayName = null;
		this.messageKey = null;
	}

	public ValidationError(String fieldName, String displayName, ValidationErrorType validationErrorType) {
		super();
		this.fieldName = fieldName;
		this.displayName = displayName;
		this.validationErrorType = validationErrorType;
		this.messageKey = null;
	}

	public ValidationError(String messageKey) {
		super();

		this.fieldName = null;
		this.displayName = null;
		this.validationErrorType = null;

		this.messageKey = messageKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public ValidationErrorType getValidationErrorType() {
		return validationErrorType;
	}

	public String getMessageKey() {
		return messageKey;
	}

	@Override
	public String toString() {

		return "ValidationError [" + (fieldName != null ? "fieldName=" + fieldName + ", " : "") + (validationErrorType != null ? "validationErrorType=" + validationErrorType + ", " : "") + (messageKey != null ? "messageKey=" + messageKey : "") + "]";
	}


	public String getDisplayName() {

		return displayName;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((messageKey == null) ? 0 : messageKey.hashCode());
		result = prime * result + ((validationErrorType == null) ? 0 : validationErrorType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ValidationError other = (ValidationError) obj;
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (fieldName == null) {
			if (other.fieldName != null) {
				return false;
			}
		} else if (!fieldName.equals(other.fieldName)) {
			return false;
		}
		if (messageKey == null) {
			if (other.messageKey != null) {
				return false;
			}
		} else if (!messageKey.equals(other.messageKey)) {
			return false;
		}
		if (validationErrorType != other.validationErrorType) {
			return false;
		}
		return true;
	}
}
