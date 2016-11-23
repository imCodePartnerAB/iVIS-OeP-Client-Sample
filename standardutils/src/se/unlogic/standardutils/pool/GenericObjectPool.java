package se.unlogic.standardutils.pool;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.standardutils.factory.BeanFactory;


/**
 * A simple generic object pool that safely prevents memory leaks by not keeping references to borrowed objects.
 * 
 * @author Unlogic
 *
 * @param <T>
 */
public class GenericObjectPool<T> {

	protected int minSize;
	protected int maxSize;
	protected int objectsCreated;

	protected BeanFactory<T> factory;

	protected List<T> pool;

	protected PoolExhaustedBehaviour poolExhaustedBehaviour;

	public GenericObjectPool(int minSize, int maxSize, BeanFactory<T> factory, PoolExhaustedBehaviour poolExhaustedBehaviour) {

		super();

		if(minSize > maxSize){

			throw new RuntimeException("minSize cannot be bigger than maxSize");
		}

		this.minSize = minSize;
		this.maxSize = maxSize;
		this.factory = factory;
		this.poolExhaustedBehaviour = poolExhaustedBehaviour;

		this.pool = createList();

		initializePool();
	}

	public GenericObjectPool(BeanFactory<T> factory) {

		this(0, 10, factory, PoolExhaustedBehaviour.GROW);
	}

	protected List<T> createList() {

		return new ArrayList<T>(maxSize);
	}

	protected void initializePool() {

		if(minSize != 0){

			while(pool.size() < minSize){

				pool.add(factory.newInstance());
				objectsCreated++;
			}
		}
	}

	public T borrowObject() throws PoolExhaustedException{

		synchronized (pool){

			T object = null;

			if(pool.isEmpty()){

				if(poolExhaustedBehaviour == PoolExhaustedBehaviour.GROW){

					object = factory.newInstance();
					objectsCreated++;

				}else if(poolExhaustedBehaviour == PoolExhaustedBehaviour.THROW_EXCEPTION){

					throw new PoolExhaustedException("Pool has reached it's max size of " + maxSize + " objects");

				}else if(poolExhaustedBehaviour == PoolExhaustedBehaviour.WAIT){

					try {
						pool.wait();
						object = pool.remove(0);
					} catch (InterruptedException e) {
						throw new RuntimeException("Thread interrupted while waiting for free object in pool", e);
					}
				}

			}else{

				object = pool.remove(0);
			}

			return object;
		}
	}

	public void returnObject(T object) throws PoolFullException{

		synchronized (pool){

			validateObject(object);

			if(pool.size() == objectsCreated){

				throw new PoolFullException("More objects have been returned than have been created by the pool");
			}

			pool.add(object);

			pool.notify();

			return;
		}

	}

	protected void validateObject(T object) throws InvalidObjectException{}


	public int getObjectsCreated() {

		return objectsCreated;
	}

	public int getObjectsInPool(){

		return pool.size();
	}
}
