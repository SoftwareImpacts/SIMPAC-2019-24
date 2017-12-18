package peakaboo.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bolt.plugin.core.BoltPluginSet;
import bolt.plugin.core.IBoltPluginSet;
import bolt.plugin.java.BoltPluginLoader;
import bolt.plugin.java.ClassInheritanceException;
import bolt.plugin.java.ClassInstantiationException;
import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.filter.model.FilterPlugin;
import peakaboo.filter.plugins.advanced.DataToWavelet;
import peakaboo.filter.plugins.advanced.SubFilter;
import peakaboo.filter.plugins.advanced.Identity;
import peakaboo.filter.plugins.advanced.Interpolation;
import peakaboo.filter.plugins.advanced.SpectrumNormalization;
import peakaboo.filter.plugins.advanced.WaveletToData;
import peakaboo.filter.plugins.background.BruknerRemoval;
import peakaboo.filter.plugins.background.LinearTrimRemoval;
import peakaboo.filter.plugins.background.PolynomialRemoval;
import peakaboo.filter.plugins.mathematical.Addition;
import peakaboo.filter.plugins.mathematical.Derivative;
import peakaboo.filter.plugins.mathematical.Integrate;
import peakaboo.filter.plugins.mathematical.Multiply;
import peakaboo.filter.plugins.mathematical.Subtraction;
import peakaboo.filter.plugins.noise.AggressiveWaveletNoiseFilter;
import peakaboo.filter.plugins.noise.FourierLowPass;
import peakaboo.filter.plugins.noise.MovingAverage;
import peakaboo.filter.plugins.noise.SavitskyGolaySmoothing;
import peakaboo.filter.plugins.noise.SpringSmoothing;
import peakaboo.filter.plugins.noise.WaveletNoiseFilter;


public class FilterLoader
{

	private static BoltPluginLoader<FilterPlugin> pluginLoader;
	private static BoltPluginSet<FilterPlugin> plugins = new IBoltPluginSet<>();
	
	public static void load() {
		try {
			initLoader();
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			e.printStackTrace();
		}
	}
	
	private static void initLoader() throws ClassInheritanceException, ClassInstantiationException {
		BoltPluginLoader<FilterPlugin> newPluginLoader = new BoltPluginLoader<>(plugins, FilterPlugin.class);
		

		//register built-in plugins
		newPluginLoader.registerPlugin(DataToWavelet.class);
		newPluginLoader.registerPlugin(Identity.class);
		newPluginLoader.registerPlugin(SubFilter.class);
		newPluginLoader.registerPlugin(SpectrumNormalization.class);
		newPluginLoader.registerPlugin(WaveletToData.class);
		
		newPluginLoader.registerPlugin(BruknerRemoval.class);
		newPluginLoader.registerPlugin(LinearTrimRemoval.class);
		newPluginLoader.registerPlugin(PolynomialRemoval.class);
		
		newPluginLoader.registerPlugin(Addition.class);
		newPluginLoader.registerPlugin(Derivative.class);
		newPluginLoader.registerPlugin(Integrate.class);
		newPluginLoader.registerPlugin(Multiply.class);
		newPluginLoader.registerPlugin(Subtraction.class);
		
		newPluginLoader.registerPlugin(AggressiveWaveletNoiseFilter.class);
		newPluginLoader.registerPlugin(FourierLowPass.class);
		newPluginLoader.registerPlugin(MovingAverage.class);
		newPluginLoader.registerPlugin(SavitskyGolaySmoothing.class);
		newPluginLoader.registerPlugin(SpringSmoothing.class);
		newPluginLoader.registerPlugin(WaveletNoiseFilter.class);
		newPluginLoader.registerPlugin(Interpolation.class);
		
		
		
		//load plugins from local
		newPluginLoader.register();
		
		//load plugins from the application data directory
		File appDataDir = Env.appDataDirectory(Version.program_name, "Plugins/Filter");
		appDataDir.mkdirs();
		newPluginLoader.register(appDataDir);
		pluginLoader = newPluginLoader;
	}
	
	public static BoltPluginSet<FilterPlugin> getPluginSet() {
		if (pluginLoader == null) {
			try {
				initLoader();
			} catch (ClassInheritanceException | ClassInstantiationException e) {
				e.printStackTrace();
			}
		}
		return plugins;
	}
	
	public static synchronized List<FilterPlugin> getAvailableFilters()
	{

		try {
			
			List<FilterPlugin> filters = new ArrayList<>(); 
			
			if (pluginLoader == null)
			{
				initLoader();
			}
			
			filters.addAll(plugins.getNewInstancesForAllPlugins());
			
			return filters;
			
		} catch (ClassInheritanceException e) {
			e.printStackTrace();
		} catch (ClassInstantiationException e) {
			e.printStackTrace();
		}
				
		return null;
		
	}
	
}
