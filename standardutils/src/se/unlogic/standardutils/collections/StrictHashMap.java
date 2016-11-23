package se.unlogic.standardutils.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


@SuppressWarnings("unused")
public class StrictHashMap<Key, Value> extends HashMap<Key, Value> implements StrictMap<Key, Value> {

	private static final long serialVersionUID = 6901384924202372770L;

	@Override
	public Value put(Key key, Value value) throws KeyAlreadyCachedException{

		if (containsKey(key)) {
			throw new KeyAlreadyCachedException(key);
		} else {
			return super.put(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends Key, ? extends Value> map) throws KeyAlreadyCachedException {

		for (Entry<? extends Key, ? extends Value> entry : map.entrySet()) {

			if (containsKey(entry.getKey())) {
				throw new KeyAlreadyCachedException(entry.getKey());
			}
		}

		super.putAll(map);
	}

	@Override
	public Value remove(Object key) throws KeyNotCachedException{

		if (containsKey(key)) {
			return super.remove(key);
		} else {
			throw new KeyNotCachedException(key);
		}
	}
	
	public Value update(Key key, Value value) throws KeyNotCachedException {

		if (containsKey(key)) {
			super.remove(key);
			return put(key, value);
		} else {
			throw new KeyNotCachedException(key);
		}
	}
}
