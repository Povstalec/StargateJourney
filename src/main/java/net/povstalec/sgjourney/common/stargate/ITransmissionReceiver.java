package net.povstalec.sgjourney.common.stargate;

public interface ITransmissionReceiver
{
	public void receiveTransmission(int transmissionJump, int frequency, String transmission);
}
