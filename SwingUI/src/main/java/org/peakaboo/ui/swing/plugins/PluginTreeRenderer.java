package org.peakaboo.ui.swing.plugins;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.calibration.CalibrationReference;
import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.datasource.plugin.JavaDataSourcePlugin;
import org.peakaboo.filter.plugins.JavaFilterPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;

public class PluginTreeRenderer extends DefaultTreeCellRenderer {
	
	private boolean init = false;
	
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
    	
    	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    	
    	if (init == false) {
    		init = true;
    		setBorder(Spacing.bSmall());
    	}
    	
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    	Object object = node.getUserObject();
    	if (object instanceof BoltPluginManager) {
    		BoltPluginManager<?> manager = (BoltPluginManager<?>) object;
        	setText(manager.getInterfaceName());
        	setIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
    	} else if (object instanceof BoltPluginPrototype) {
        	BoltPluginPrototype<? extends BoltPlugin> plugin = (BoltPluginPrototype<? extends BoltPlugin>)object;
        	setText(plugin.getName());
        	setIcon(getIcon(plugin));	
    	} else if (object instanceof BoltIssue) {
    		BoltIssue issue = (BoltIssue) object;
    		setText(issue.shortSource());
    		setIcon(StockIcon.BADGE_ERROR.toImageIcon(IconSize.BUTTON));
    	}
    	

    	return this;
    	
    }
    
	private ImageIcon getIcon(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == JavaDataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaDataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaFilterPlugin.class) {
			return StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON);
		}

		if (pluginBaseClass == JavaMapFilterPlugin.class) {
			return StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == CalibrationReference.class) {
			return IconFactory.getImageIcon("calibration", IconSize.BUTTON);
		}
		
		return null;
	}

}
