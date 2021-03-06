package org.peakaboo.mapping.filter.plugin.plugins.transforming;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class Rotate270MapFilter extends AbstractMapFilter {

	@Override
	public String getFilterName() {
		return "Rotate 270 Degrees";
	}

	@Override
	public String getFilterDescription() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.TRANSFORMING;
	}

	@Override
	public void initialize() {}

	@Override
	public AreaMap filter(AreaMap map) {
		
		ReadOnlySpectrum source = map.getData();
		GridPerspective<Float> sourceGrid = new GridPerspective<Float>(map.getSize().x, map.getSize().y, 0f);
		
		Spectrum target = new ISpectrum(source.size());
		GridPerspective<Float> targetGrid = new GridPerspective<Float>(map.getSize().y, map.getSize().x, 0f);
		
		int maxy = map.getSize().y-1;
		int maxx = map.getSize().x-1;
		
		for (int y = 0; y < sourceGrid.height; y++) {
			for (int x = 0; x < sourceGrid.width; x++) {
				float value = sourceGrid.get(source, x, y);
				targetGrid.set(target, maxy-y, x, value);
			}
		}
		
		Coord<Bounds<Number>> origDim = map.getRealDimensions();
		Coord<Bounds<Number>> newDim = null;
		if (origDim != null) {
			newDim = new Coord<>(new Bounds<>(origDim.y.end, origDim.y.start), new Bounds<>(origDim.x.start, origDim.x.end));
		}
		
		Coord<Integer> oldsize = map.getSize();
		return new AreaMap(target, new Coord<>(oldsize.y, oldsize.x), newDim);
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "05c41d99-bfa7-45ce-82b5-97e4a6ffd8f7";
	}

	@Override
	public boolean isReplottable() {
		return false;
	}

}
