package se.unlogic.standardutils.io;

import java.io.Closeable;
import java.io.IOException;


public class CloseUtil {

	public static final void close(Closeable closeable){
		
		if(closeable != null){
			
			try {
				closeable.close();
			} catch (IOException e) {}
		}
	}
}
