/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.unlogic.standardutils.bool.BooleanSignal;
import se.unlogic.standardutils.operation.ProgressListener;


public class StreamUtils {

	public static byte[] toByteArray(InputStream inputStream) throws IOException{

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		transfer(inputStream, outputStream);

		return outputStream.toByteArray();
	}

	public static void transfer(InputStream inputStream, OutputStream outputStream) throws IOException{

		transfer(inputStream, outputStream, 8192);
	}

	public static void transfer(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException{

		byte[] buf = new byte[bufferSize];
		int count = 0;

		while ((count = inputStream.read(buf)) >= 0) {

			outputStream.write(buf, 0, count);
		}
	}

	public static boolean transfer(InputStream inputStream, OutputStream outputStream, int bufferSize, ProgressListener progressListener, BooleanSignal abortSignal) throws IOException{

		byte[] buf = new byte[bufferSize];
		int count = 0;

		while ((count = inputStream.read(buf)) >= 0) {

			if(abortSignal.isSignal()){

				return false;
			}

			outputStream.write(buf, 0, count);

			progressListener.incrementCurrentPosition(count);
		}

		return true;
	}

	public static void closeStream(InputStream stream){

		if(stream != null){

			try {
				stream.close();
			} catch (IOException e) {}
		}
	}

	public static void closeStream(OutputStream stream){

		if(stream != null){

			try {
				stream.close();
			} catch (IOException e) {}
		}
	}
}
