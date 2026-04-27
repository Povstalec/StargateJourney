package net.povstalec.sgjourney.common.sgjourney.transporter;

public abstract class SGJourneyTransportRings extends SGJourneyTransporter
{
	public static final double INNER_RADIUS = 2;
	
	public SGJourneyTransportRings(TransporterType<?> type)
	{
		super(type);
	}
	
	@Override
	public double getInnerRadius()
	{
		return INNER_RADIUS;
	}
}
