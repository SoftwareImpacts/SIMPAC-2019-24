package peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.model.FittingResultSet;
import peakaboo.curvefit.model.FittingSet;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;



public class FittingModel
{

	/**
	 * Existing TransitionSeries and their Fitting against raw data.
	 */
	public FittingSet			selections;
	
	/**
	 * Results of fitting existing selections
	 */
	public FittingResultSet		selectionResults;
	
	/**
	 * Proposed TransitionSeries and their Fitting against data after already being fit against current selections
	 */
	public FittingSet			proposals;
	
	/**
	 * Results of fitting proposed selections.
	 */
	public FittingResultSet		proposalResults;
	
	
	List<TransitionSeries> highlighted;
	
	
	public FittingModel()
	{
		selections = new FittingSet();
		proposals = new FittingSet();
		selections.setEscapeType(EscapePeakType.getDefault());
		proposals.setEscapeType(EscapePeakType.getDefault());
		selectionResults = null;
		proposalResults = null;
		highlighted = new ArrayList<>();
	}
	
}