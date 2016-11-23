/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailUtils {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.(([0-9]{1,3})|([a-zA-Z]{2,3})|(aero|coop|info|museum|name))$");

	public static boolean isValidEmailAddress(String emailAddress) {
		return EMAIL_PATTERN.matcher(emailAddress).matches();
	}

	public static Address[] getAddresses(List<String> stringAddressList) {

		if (stringAddressList != null && !stringAddressList.isEmpty()) {
			ArrayList<Address> internetAddressList = new ArrayList<Address>();

			for (String stringAddress : stringAddressList) {

				Address address = getAddress(stringAddress);

				if (address != null) {
					internetAddressList.add(address);
				}
			}

			if (!internetAddressList.isEmpty()) {
				return internetAddressList.toArray(new Address[internetAddressList.size()]);
			}
		}

		return null;
	}

	public static Address getAddress(String address) {

		if (address == null) {
			return null;
		}

		try {
			return new InternetAddress(address);
		} catch (AddressException e) {
			return null;
		}
	}

	public static Address getAddresses(String address, String name) {

		if (address == null) {
			return null;
		} else if (name == null) {
			return getAddress(address);
		}

		try {
			return new InternetAddress(address, name);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
