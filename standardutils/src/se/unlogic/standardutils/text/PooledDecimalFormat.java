package se.unlogic.standardutils.text;

import java.text.DecimalFormat;
import java.text.ParseException;

import se.unlogic.standardutils.pool.GenericObjectPool;


public class PooledDecimalFormat{

	protected GenericObjectPool<DecimalFormat> pool;

	DecimalFormat decimalFormat;

	public PooledDecimalFormat(String format){

		this.pool = new GenericObjectPool<DecimalFormat>(new DecimalFormatFactory(format));
	}

	public final String format(long number) {

		DecimalFormat decimalFormat = null;

		try{
			decimalFormat = pool.borrowObject();

			return decimalFormat.format(number);

		}finally{

			pool.returnObject(decimalFormat);
		}
	}

	public final String format(double number) {

		DecimalFormat decimalFormat = null;

		try{
			decimalFormat = pool.borrowObject();

			return decimalFormat.format(number);

		}finally{

			pool.returnObject(decimalFormat);
		}
	}

	public final String format(Object number) {

		DecimalFormat decimalFormat = null;

		try{
			decimalFormat = pool.borrowObject();

			return decimalFormat.format(number);

		}finally{

			pool.returnObject(decimalFormat);
		}
	}

	public Number parse(String source) throws ParseException {

		DecimalFormat decimalFormat = null;

		try{
			decimalFormat = pool.borrowObject();

			return decimalFormat.parse(source);

		}finally{

			pool.returnObject(decimalFormat);
		}
	}

	public int getObjectsCreated() {

		return pool.getObjectsCreated();
	}

	public int getObjectsInPool() {

		return pool.getObjectsInPool();
	}
}
