package stratus.theme;

import java.awt.Color;

import stratus.Stratus;

public class LightTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color control = new Color(0xe9e9e9);
	private Color controlText = new Color(0x202020);
	private Color controlTextDisabled = new Color(0x999999);
	private Color border = new Color(0xBABABA);
	
	private Color widget = Stratus.darken(getControl(), 0.02f);
	private Color widgetBevel = Stratus.lighten(getWidget(), 0.2f);
	
	private Color menuControl = new Color(0xffffff);
	private Color menuControlText = controlText;
	
	private Color tableHeaderText = new Color(0x666666);
	
	private Color scrollHandle = Stratus.darken(getBorder(), 0.1f);
	
	@Override
	public Color getHighlight() {
		return highlight;
	}
	
	@Override
	public Color getControl() {
		return control;
	}
	
	@Override
	public Color getControlText() {
		return controlText;
	}
	
	@Override
	public Color getControlTextDisabled() {
		return controlTextDisabled;
	}
	
	@Override
	public Color getBorder() {
		return border;
	}

	@Override
	public Color getMenuControl() {
		return menuControl;
	}

	@Override
	public Color getMenuControlText() {
		return menuControlText;
	}

	@Override
	public Color getHighlightText() {
		return Color.WHITE;
	}

	
	@Override
	public Color getWidget() {
		return widget;
	}

	@Override
	public Color getWidgetBevel() {
		return widgetBevel;
	}

	@Override
	public Color getTextControl() {
		return Color.WHITE;
	}

	@Override
	public Color getTextText() {
		return controlText;
	}

	@Override
	public Color getTableHeader() {
		return Color.WHITE;
	}

	@Override
	public Color getTableHeaderText() {
		return tableHeaderText;
	}

	@Override
	public Color getScrollHandle() {
		return scrollHandle;
	}

	
	
}
