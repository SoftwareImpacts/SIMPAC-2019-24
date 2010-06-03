package peakaboo.filters.filters;


import java.util.List;

import peakaboo.calculations.Noise;
import peakaboo.calculations.Noise.FFTStyle;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;

/**
 * 
 * This class is a filter exposing the Fourier Low Pass functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class FourierLowPass extends AbstractFilter
{

	private final int	ROLLOFF	= 0;
	private final int	START	= 1;
	private final int	END		= 2;


	public FourierLowPass()
	{
		super();
		parameters.add(ROLLOFF, new Parameter<FFTStyle>(ValueType.BOOLEAN, "Roll-Off Type", FFTStyle.LINEAR, FFTStyle.values()));
		parameters.add(START, new Parameter<Integer>(ValueType.INTEGER, "Starting Wavelength (keV)", 8));
		parameters.add(END, new Parameter<Integer>(ValueType.INTEGER, "Ending Wavelength (keV)", 6));

	}


	@Override
	public boolean validateParameters()
	{

		int start, end;

		start = this.<Integer>getParameterValue(START);
		if (start > 15 || start < 1) return false;

		end = this.<Integer>getParameterValue(END);
		if (end > 15 || end < 0) return false;

		if (start < end) return false;

		return true;

	}


	@Override
	public String getFilterName()
	{
		return "Fourier Low-Pass";
	}


	@Override
	public FilterType getFilterType()
	{
		return FilterType.NOISE;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " Filter transforms the spectral data with a Fourier Transformation into a frequency domain. Data from a high frequency range (noise) is filtered out, while lower frequencies (peaks, background) are passed through.";
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Double> filterApplyTo(List<Double> data, boolean cache)
	{
		
		data = Noise.FFTLowPassFilter(
			data,
			this.<FFTStyle>getParameterValue(ROLLOFF),
			this.<Integer>getParameterValue(START),
			this.<Integer>getParameterValue(END)
		);

		return data;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}


}
