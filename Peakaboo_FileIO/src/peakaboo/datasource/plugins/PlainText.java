package peakaboo.datasource.plugins;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.interaction.CallbackDataSourceInteraction;
import peakaboo.datasource.components.interaction.DataSourceInteraction;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


public class PlainText extends AbstractDataSource
{

	String	datasetName;
	int 	size = 0;
	int		scanSize = -1;

	List<Spectrum> scans;
		
	
	public PlainText()
	{
		scans = SpectrumList.create(getFileFormat().getFormatName());
	}
	
	public String datasetName()
	{
		return datasetName;
	}

	public float maxEnergy()
	{
		return 0;
	}

	public Spectrum get(int index)
	{
		return scans.get(index);
	}

	public int scanCount()
	{
		return size;
	}

	public List<String> scanNames()
	{
		return new Range(0, size-1).stream().map(e -> "Scan #" + (e)).collect(toList());
	}

	

	
	
	
	
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================

	@Override
	public void read(String filename) throws Exception
	{

		datasetName = new File(filename).getName();
		
	
		//Split the input up by line
		FStringInput lines = FStringInput.lines(new File(filename));
		

		while (lines.hasNext())
		{
			String line = lines.next();
			
			if (line == null || getInteraction().checkReadAborted()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			Spectrum scan = new Spectrum(new FList<String>(line.trim().split("[, \\t]+")).stream().map(s -> {
				try { return Float.parseFloat(s); } 
				catch (Exception e) { return 0f; }
			}).collect(toList()));
			
			
			if (size > 0 && scan.size() != scanSize) 
			{
				throw new Exception("Spectra sizes are not equal");
			}
			else if (size == 0)
			{
				scanSize = scan.size();
			}
			
			
			scans.add(scan);
			size++;
			
			getInteraction().notifyScanRead(1);
			
		}
	}

	@Override
	public void read(List<String> filenames) throws Exception
	{
		
		if (filenames == null) throw new UnsupportedOperationException();
		if (filenames.size() == 0) throw new UnsupportedOperationException();
		if (filenames.size() > 1) throw new UnsupportedOperationException();
		
		read(filenames.get(0));
	}


	@Override
	public DataSourceFileFormat getFileFormat() {
		return new SimpleFileFormat(
				true, 
				"Peakaboo Plain Text", 
				"Peakaboo Plain Text format is a simple XRF format comprised of rows of space-separated numbers.", 
				Arrays.asList("txt", "dat", "csv", "tsv"));
	}


	
	
	
	
	//==============================================
	// UNSUPPORTED FEATURES
	//==============================================
	
	@Override
	public DataSourceDimensions getDimensions() {
		return null;
	}


	@Override
	public DataSourceMetadata getMetadata() {
		return null;
	}

	

}
