package org.peakaboo.datasink.plugin.plugins;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.datasource.model.DataSource;

import cyclops.ReadOnlySpectrum;

public class CSV implements JavaDataSinkPlugin {

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginName() {
		return getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFormatDescription();
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "b0727d75-2c00-43df-9205-e83cd699be91";
	}
	
	@Override
	public void write(DataSource source, Path destination) throws IOException {
		Writer writer = new OutputStreamWriter(Files.newOutputStream(destination));
		for (ReadOnlySpectrum s : source.getScanData()) {
			String spectrum = s.stream().map(f -> Float.toString(f)).collect(Collectors.joining(", "));
			writer.write(spectrum);
			writer.write("\n");
		}
		writer.close();		
	}

	@Override
	public String getFormatExtension() {
		return "csv";
	}

	@Override
	public String getFormatName() {
		return "Comma Separated Values";
	}

	@Override
	public String getFormatDescription() {
		return "Comma Separated Value file with one spectrum per line";
	}

}
