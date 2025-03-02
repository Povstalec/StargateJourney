package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;

public class TransceiverFunctions
{
	public static int getFrequency(TransceiverEntity transceiver)
	{
		return transceiver.getFrequency();
	}
	
	public static void setFrequency(TransceiverEntity transceiver, int frequency)
	{
		transceiver.setFrequency(frequency);
	}
	
	public static String getCurrentCode(TransceiverEntity transceiver)
	{
		return transceiver.getCurrentCode();
	}
	
	public static void setCurrentCode(TransceiverEntity transceiver, String idc)
	{
		transceiver.setCurrentCode(idc);
	}
	
	public static void sendTransmission(TransceiverEntity transceiver)
	{
		transceiver.sendTransmission();
	}
	
	public static Integer checkConnectedShielding(TransceiverEntity transceiver)
	{
		int state = transceiver.checkShieldingState();
		
		if(state < 0)
			return null;
		
		return state;
	}
}
