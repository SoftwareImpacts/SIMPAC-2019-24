package org.peakaboo.controller.mapper.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.Range;
import org.peakaboo.framework.eventful.EventfulType;

/**
 * Represents a box-style selection over an area
 * @author NAS
 *
 */
public class AreaSelection extends EventfulType<MapUpdateType> {

	private Coord<Integer> start, end;
	private boolean hasSelection = false;
	
	private MappingController map;
	
	public AreaSelection(MappingController map) {
		this.map = map;
	}
		
	public Coord<Integer> getStart()
	{
		return start;
	}
	
	public void setStart(Coord<Integer> dragStart)
	{
		if (dragStart != null) 
		{
			if (dragStart.x < 0) dragStart.x = 0;
			if (dragStart.y < 0) dragStart.y = 0;
			if (dragStart.x >= map.getUserDimensions().getUserDataWidth()) dragStart.x = map.getUserDimensions().getUserDataWidth()-1;
			if (dragStart.y >= map.getUserDimensions().getUserDataHeight()) dragStart.y = map.getUserDimensions().getUserDataHeight()-1;
		}
		
		this.start = dragStart;
		
		updateListeners(MapUpdateType.AREA_SELECTION);
		
		map.addListener(type -> {
			if (type == MapUpdateType.DATA_SIZE) {
				trimSelectionToBounds();
			}
		});
	}

	
	
	public Coord<Integer> getEnd()
	{
		return end;
	}

	public void setEnd(Coord<Integer> dragEnd)
	{
		if (dragEnd != null)
		{
			if (dragEnd.x < 0) dragEnd.x = 0;
			if (dragEnd.y < 0) dragEnd.y = 0;
			if (dragEnd.x >= map.getUserDimensions().getUserDataWidth()) dragEnd.x = map.getUserDimensions().getUserDataWidth()-1;
			if (dragEnd.y >= map.getUserDimensions().getUserDataHeight()) dragEnd.y = map.getUserDimensions().getUserDataHeight()-1;
		}
		
		this.end = dragEnd;
		
		updateListeners(MapUpdateType.AREA_SELECTION);
	}

	
	/**
	 * generate a list of indexes in the map which are selected
	 */
	public List<Integer> getPoints() {
		trimSelectionToBounds();
		List<Integer> indexes = new ArrayList<>();
		
		if (getStart() == null || getEnd() == null) {
			return Collections.emptyList();
		}
		
		final int xstart = getStart().x;
		final int ystart = getStart().y;
		
		final int xend = getEnd().x;
		final int yend = getEnd().y;

		final GridPerspective<Float> grid = new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(), 
				map.getUserDimensions().getUserDataHeight(), 
				0f);
		
		for (int x : new Range(xstart, xend)) {
			for (int y : new Range(ystart, yend)){
				indexes.add( grid.getIndexFromXY(x, y) );
			}
		}
		
		return indexes;
	}
	
	
	public boolean hasSelection()
	{
		return hasSelection && map.getFiltering().isReplottable() && map.getFitting().getActiveMode().isSelectable();
	}

	public boolean isReplottable() {
		return hasSelection() && map.getFitting().getActiveMode().isReplottable();
	}

	

	public void setHasBoundingRegion(boolean hasBoundingRegion)
	{
		this.hasSelection = hasBoundingRegion;
		updateListeners(MapUpdateType.AREA_SELECTION);
	}



	public void clearSelection() {
		setHasBoundingRegion(false);
	}
	
	
	public void trimSelectionToBounds() {
		
		Coord<Integer> size = map.getUserDimensions().getDimensions();
		int x = size.x;
		int y = size.y;
		
		if (start != null) {
			if (start.x >= x) start.x = x-1;
			if (start.y >= y) start.y = y-1;
		}
		if (end != null) {
			if (end.x >= x) end.x = x-1;
			if (end.y >= y) end.y = y-1;
		}
		
	}
}
