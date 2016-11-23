package se.unlogic.standardutils.io;

import se.unlogic.standardutils.text.PooledDecimalFormat;


public class BinarySizeFormater {

	private static final PooledDecimalFormat DECIMAL_FORMAT = new PooledDecimalFormat("#.##");

	public static String getFormatedSize(long bytes){

		if(bytes >= BinarySizes.GigaByte){

			return DECIMAL_FORMAT.format(bytes/(double)BinarySizes.GigaByte) + " GB";

		}else if(bytes >= BinarySizes.MegaByte){

			return DECIMAL_FORMAT.format((double)bytes/(double)BinarySizes.MegaByte) + " MB";

		}else if(bytes >= BinarySizes.KiloByte){

			return bytes/BinarySizes.KiloByte + " KB";

		}else{

			return bytes + " B";
		}
	}
}
