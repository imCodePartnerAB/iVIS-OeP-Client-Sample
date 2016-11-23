package se.unlogic.standardutils.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import se.unlogic.standardutils.streams.StreamUtils;

public class SerializationUtils {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T cloneSerializable(T obj) {

		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;

		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;

		try{
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(obj);

			byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

			objectInputStream = new ObjectInputStream(byteArrayInputStream);

			return (T)objectInputStream.readObject();

		}catch(IOException e){

			throw new RuntimeException(e);

		}catch(ClassNotFoundException e){

			throw new RuntimeException(e);

		}finally{
			StreamUtils.closeStream(byteArrayOutputStream);
			StreamUtils.closeStream(objectOutputStream);
			StreamUtils.closeStream(byteArrayInputStream);
			StreamUtils.closeStream(objectInputStream);
		}
	}

	public static byte[] serializeToArray(Serializable object) {

		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;

		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);

		} catch (IOException e) {

			throw new RuntimeException(e);

		}finally{

			StreamUtils.closeStream(byteArrayOutputStream);
			StreamUtils.closeStream(objectOutputStream);
		}

		return byteArrayOutputStream.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializeFromArray(Class<T> clazz, byte[] bytes) {

		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;

		try {
			byteArrayInputStream = new ByteArrayInputStream(bytes);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);

			return (T)objectInputStream.readObject();

		} catch (Exception e) {

			throw new RuntimeException(e);

		}finally{

			StreamUtils.closeStream(objectInputStream);
			StreamUtils.closeStream(byteArrayInputStream);
		}

	}
}
