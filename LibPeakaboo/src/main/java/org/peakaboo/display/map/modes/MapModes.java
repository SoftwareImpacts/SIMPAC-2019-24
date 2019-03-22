package org.peakaboo.display.map.modes;

import org.peakaboo.display.map.modes.composite.CompositeMapMode;
import org.peakaboo.display.map.modes.overlay.OverlayMapMode;
import org.peakaboo.display.map.modes.ratio.RatioMapMode;
import org.peakaboo.display.map.modes.scatter.ScatterMapMode;

public enum MapModes {
	COMPOSITE
	{
		@Override
		public String toString() { return "Composite"; }
		
		@Override 
		public MapMode getMapper() { return new CompositeMapMode(); }
		
	},
	OVERLAY
	{
		@Override
		public String toString() { return "Overlay"; }
		
		@Override 
		public MapMode getMapper() { return new OverlayMapMode(); }
		
	},
	RATIO
	{
		@Override
		public String toString() { return "Ratio"; }
		
		@Override 
		public MapMode getMapper() { return new RatioMapMode(); }
	},
	SCATTER
	{
		@Override
		public String toString() { return "Scatter"; }
		
		@Override 
		public MapMode getMapper() { return new ScatterMapMode(); }
	}
	
	;
	
	public MapMode getMapper() {
		return null;
	}
}