package peakaboo.filters.filters;


import peakaboo.calculations.Noise;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class SpringSmoothing extends AbstractFilter
{

	private final int	MULTIPLIER		= 0;
	private final int	ITERATIONS		= 1;
	private final int	FALLOFF			= 2;


	public SpringSmoothing()
	{
		super();
		parameters.put(MULTIPLIER, new Parameter<Double>(ValueType.REAL, "Force Multiplier", 1.0d));
		parameters.put(FALLOFF, new Parameter<Double>(ValueType.REAL, "Force Falloff Power", 1.0d));
		parameters.put(ITERATIONS, new Parameter<Integer>(ValueType.INTEGER, "Iterations", 1));

	}


	@Override
	public String getFilterName()
	{
		return "Spring Smoothing";
	}



	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}


	@Override
	public boolean validateParameters()
	{

		double mult, power;
		int iterations;

		
		mult = this.<Double>getParameterValue(MULTIPLIER);
		if (mult > 40 || mult < 0.1) return false;
		
		power = this.<Double>getParameterValue(FALLOFF);
		if (power > 10 || power < 0.0) return false;
		
		iterations = this.<Integer>getParameterValue(ITERATIONS);
		if (iterations > 50 || iterations < 1) return false;
		

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " Filter treats each pair of points as if they were connected by a spring. With each iteration, a tension force draws neighbouring points closer together. The Force Multiplier controls how strongly the two elements are pulled together, and the Force Falloff Power controls how aggressively stronger signal becomes fixed in place, unmoved by spring forces.";
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		data = Noise.testFilter(
				data, 
				this.<Double>getParameterValue(MULTIPLIER).floatValue(), 
				this.<Double>getParameterValue(FALLOFF).floatValue(), 
				this.<Integer>getParameterValue(ITERATIONS)
			);
		return data;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}

}
