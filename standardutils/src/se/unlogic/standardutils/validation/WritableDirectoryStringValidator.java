package se.unlogic.standardutils.validation;

import java.io.File;


public class WritableDirectoryStringValidator implements StringFormatValidator {

	public boolean validateFormat(String value) {

		File file = new File(value);

		if(file.exists() && file.isDirectory() && file.canWrite()){

			return true;
		}

		return false;
	}

}
