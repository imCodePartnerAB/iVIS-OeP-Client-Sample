package se.unlogic.standardutils.collections;

import java.util.Map.Entry;


public class MapUtils {

	public static <T> T getEntryKey(Entry<T,?> entry){

		if(entry != null){

			return entry.getKey();
		}

		return null;
	}

	public static <T> T getEntryValue(Entry<?,T> entry){

		if(entry != null){

			return entry.getValue();
		}

		return null;
	}
}
