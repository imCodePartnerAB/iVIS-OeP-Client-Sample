package se.unlogic.standardutils.io;

import java.io.IOException;
import java.nio.channels.Channel;


public class ChannelUtils {

	public static void closeChannel(Channel channel){

		try{
			if(channel != null){
				channel.close();
			}

		}catch(IOException e){}
	}
}
