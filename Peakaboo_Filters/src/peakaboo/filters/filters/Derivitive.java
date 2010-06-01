package peakaboo.filters.filters;

import java.util.List;

import peakaboo.calculations.Noise;
import peakaboo.common.Version;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;


public class Derivitive extends AbstractFilter
{

	@Override
	public List<Double> filterApplyTo(List<Double> data, boolean cache)
	{
		return Noise.deriv(data);
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Derivitive (Rate of Change)";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.MATHEMATICAL;
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean validateParameters()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean showFilter()
	{
		return !Version.release;
	}
	
}
