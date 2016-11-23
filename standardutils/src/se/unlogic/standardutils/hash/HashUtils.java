package se.unlogic.standardutils.hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import se.unlogic.standardutils.operation.ProgressMeter;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;


public class HashUtils {

	public static String hash(String string, String algorithm) {

		return hash(string, algorithm, Charset.defaultCharset().toString());
	}

	public static String hash(String string, String algorithm, String encoding){

		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			digest.update(string.getBytes(encoding));

			byte[] bytes = digest.digest();

			return StringUtils.toHexString(bytes);

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException(e);

		}catch(UnsupportedEncodingException e){

			throw new RuntimeException(e);
		}
	}

	public static String hash(File file, String algorithm) throws IOException{

		return hash(file, algorithm, null);
	}

	public static String hash(File file, String algorithm, ProgressMeter progressMeter) throws IOException{

		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(file);

			return hash(fileInputStream, algorithm, progressMeter, file.length());

		}finally{

			StreamUtils.closeStream(fileInputStream);
		}
	}

	public static String hash(InputStream inputStream, String algorithm, ProgressMeter progressMeter, Long length) throws IOException{

		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest);

			if(progressMeter != null){

				progressMeter.setStartTime();

				if(length != null){

					progressMeter.setFinish(length);
				}
			}

			byte[] buffer = new byte[8192];

			int bytesRead = 1;

			if(progressMeter != null){

				while ((bytesRead = digestInputStream.read(buffer)) != -1){

					progressMeter.incrementCurrentPosition(bytesRead);
				}

			}else{

				while ((bytesRead = digestInputStream.read(buffer)) != -1){}
			}

			byte[] bytes = digest.digest();

			if(progressMeter != null){

				progressMeter.setEndTime();
			}

			return StringUtils.toHexString(bytes);

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException(e);
		}
	}

	public static String mysqlPasswordHash(String string){

		try {
			MessageDigest digest = MessageDigest.getInstance(HashAlgorithms.SHA1);

			try {
				digest.update(string.getBytes("UTF-8"));

			} catch (UnsupportedEncodingException e) {

				throw new RuntimeException(e);
			}

			byte[] bytes = digest.digest();

			digest.update(bytes);

			bytes = digest.digest();

			return "*" + StringUtils.toHexString(bytes);

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException(e);
		}
	}
}
