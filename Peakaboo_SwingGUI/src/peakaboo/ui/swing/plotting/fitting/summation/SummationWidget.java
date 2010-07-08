package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import eventful.EventfulListener;
import fava.*;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;
import static fava.Fn.*;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;



class SummationWidget extends TSSelectorGroup
{


	public SummationWidget(FittingController controller)
	{

		super(controller, 2);
		resetSelectors();
		refreshGUI();
		
	}
	
	

	
	
	@Override
	public List<TransitionSeries> getTransitionSeries()
	{

		//get a list of all TransitionSeries to be summed
		List<TransitionSeries> tss = filter(map(selectors, new FunctionMap<TSSelector, TransitionSeries>() {

			public TransitionSeries f(TSSelector element)
			{
				return element.getTransitionSeries();
			}
		}), Functions.<TransitionSeries>notNull());
		
		
		return DataTypeFactory.<TransitionSeries>listInit(TransitionSeries.summation(tss));

	}
	


	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		Fn.each(selectors, new FunctionEach<TSSelector>() {

			public void f(TSSelector selector)
			{
				selector.setTransitionSeries(tss);
			}
		});
	}
	

	@Override
	protected void refreshGUI()
	{

		removeAll();

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		for (TSSelector selector : selectors)
		{
			c.gridy += 1;

			c.gridx = 0;
			c.weightx = 1.0;
			add(selector, c);

			c.gridx = 1;
			c.weightx = 0.0;
			add(createRemoveButton(selector), c);


		}

		c.gridy++;
		c.gridx = 1;
		add(addButton, c);

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		revalidate();

		TSSelectorUpdated();


	}


}
