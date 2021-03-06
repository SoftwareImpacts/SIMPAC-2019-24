package org.peakaboo.framework.stratus.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.theme.Theme;

//Fills the area of button style controls (no borders, etc)
public class ButtonPainter extends StatefulPainter {

	public static class ButtonPalette {
		
		public Color fillTop, fillBottom;
		public Color[] fillArray;
		public Color bevel, dash, gloss, border, shadow;
		public Color text;
		public float[] fillPoints = new float[] {0f, 1.0f};
		
		public ButtonPalette() {
			// TODO Auto-generated constructor stub
		}
		
		public ButtonPalette(ButtonPalette copy) {
			this.fillTop = copy.fillTop;
			this.fillBottom = copy.fillBottom;
			this.fillArray = Arrays.copyOf(copy.fillArray, copy.fillArray.length);
			this.fillPoints = Arrays.copyOf(copy.fillPoints, copy.fillPoints.length);
			this.bevel = copy.bevel;
			this.dash = copy.dash;
			this.gloss = copy.gloss;
			this.border = copy.border;
			this.shadow = copy.shadow;
			this.text = copy.text;
			
			
		}
		
	}
	
	private ButtonPalette palette = new ButtonPalette();
	    
    protected float radius = Stratus.borderRadius;
    protected float borderWidth = 1;
    protected int margin = 1;
    
    

    
    public ButtonPainter(Theme theme, ButtonState... buttonStates) {
    	this(theme, 1, buttonStates);
    }
    
    public ButtonPainter(Theme theme, int margin, ButtonState... buttonStates) {
    	super(theme, buttonStates);
    	setupPalette(palette, getTheme().getWidget());
    	this.margin = margin;
    }
    
    private void setupPalette(ButtonPalette palette, Color base) {
    	   	
    	
    	if (isMouseOver()) {
    		base = Stratus.lighten(base);
    	}
    	
    	//ENABLED is default
    	palette.fillTop = Stratus.lighten(base, 0.06f);
    	palette.fillBottom = Stratus.darken(base, 0.06f);
    	palette.bevel = Stratus.lighten(palette.fillTop, 0.1f);
    	palette.text = getTheme().getControlText();
    	palette.dash = getTheme().getWidgetDashAlpha();
    	palette.border = getTheme().getWidgetBorderAlpha();
    	palette.shadow = getTheme().getShadow();
    	
    	if (isPressed() || isSelected()) {
    		palette.fillTop = Stratus.darken(base, 0.03f);
    		palette.fillBottom = Stratus.darken(base, 0.03f);
    	}

    	
    	if (isDisabled()) {
    		palette.fillTop = getTheme().getControl();
    		palette.fillBottom = getTheme().getControl();
    		
    		//Disabled and selected, like toggle button
        	if (isSelected()) {
        		palette.fillTop = Stratus.darken(palette.fillTop, 0.06f);
        		palette.fillBottom = Stratus.darken(palette.fillBottom, 0.06f);
        	}
    		
    	}
    	
		palette.fillArray = new Color[] {palette.fillTop, palette.fillBottom};
		palette.fillPoints = new float[] {0, 1f};
    }
    
    
    /**
     * Makes a ButtonPalette object for this component. If there is nothing 
     * special about it, it will return the stock ButtonPalette instance.
     * The object may be null to retrieve the default ButtonPalette.
     */
    protected ButtonPalette makePalette(JComponent object) {
    	
    	if (object == null) {
    		return palette;
    	}
    	
    	boolean isCustomColour = object.getBackground().getClass().getName().equals("java.awt.Color"); 
    	
    	if (isCustomColour) {
    		Color base = object.getBackground();
    		ButtonPalette custom = new ButtonPalette(palette);
    		setupPalette(custom, base);
    		custom.border = Stratus.darken(object.getBackground(), 0.1f);
    		return custom;
    	}
    	
    	return palette;
    }

    @Override
    public final void paint(Graphics2D g, JComponent object, int width, int height) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		paint(g, object, width, height, makePalette(object));
		
    }
    
    


    
    protected void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
    	    	
    	float pad = margin;


    	
    	drawBorder(object, width, height, pad, g, palette);
    	drawMain(object, width, height, pad, g, palette);
    	drawShadow(object, width, height, pad, g, palette);
    	drawBevel(object, width, height, pad, g, palette);
    	drawDash(object, width, height, pad, g, palette);
	
    }

    

	protected Shape fillShape(JComponent object, float width, float height, float pad) {
    	float p = pad+1;
    	return new RoundRectangle2D.Float(p, p, width-p*2, height-p*2, radius, radius);
    }
    
    protected Shape borderShape(JComponent object, float width, float height, float pad) {
    	return new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2, radius, radius);
    }
    
    protected Shape shadowShape(JComponent object, float width, float height, float pad) {
    	GeneralPath path = new GeneralPath();
    	float y = (int)(height-(pad)*2);
    	float startx = pad+2;
    	float endx = width-(pad+1)*2;
    	path.moveTo(startx, y);
    	path.lineTo(endx, y);
    	return path;
    }
    
    protected Shape bevelShape(JComponent object, float width, float height, float pad) {
    	GeneralPath path = new GeneralPath();
    	path.moveTo(pad+2, pad+1);
    	path.lineTo(width-(pad+1)*2, pad+1);
    	return path;
    }
    
    protected Shape dashShape(JComponent object, float width, float height, float pad) {
    	return new RoundRectangle2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, radius, radius);
    }
    
    
    
    
    protected void drawBorder(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Border should be darker at the bottom when not pressed (bit of a shadow?)
    	g.setPaint(borderPaint(width, height, pad, palette));
    	g.fill(borderShape(object, width, height, pad));
    }
    
    protected void drawMain(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	g.setPaint(mainPaint(width, height, pad, palette));
    	g.fill(fillShape(object, width, height, pad));
	}

    protected void drawBevel(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Bevel at top of button unless pressed
    	if (!(isPressed() || isSelected()) && !(isDisabled())) {
	    	g.setPaint(bevelPaint(width, height, pad, palette));
	    	g.draw(bevelShape(object, width, height, pad));
    	}
    }
    
    protected void drawShadow(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Shadow at bottom of button unless pressed
    	if (!(isPressed() || isSelected()) && !(isDisabled())) {
	    	g.setPaint(shadowPaint(width, height, pad, palette));
	    	g.draw(shadowShape(object, width, height, pad));
    	}
    }
    
    protected void drawDash(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Focus dash if focused but not pressed
    	pad += 2;
    	if (isFocused() && !isPressed()) {
        	g.setPaint(dashPaint(width, height, pad, palette));
        	Stroke old = g.getStroke();
        	g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {2, 1}, 0f));
        	g.draw(dashShape(object, width, height, pad));
        	g.setStroke(old);
    	}
    }
    
    
    
    
    protected Paint shadowPaint(float width, float height, float pad, ButtonPalette palette) {
    	return palette.shadow;
    }

    protected Paint bevelPaint(float width, float height, float pad, ButtonPalette palette) {
    	return palette.bevel;
    }
    
    protected Paint mainPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, palette.fillPoints, palette.fillArray);
    }
    
    protected Paint borderPaint(float width, float height, float pad, ButtonPalette palette) {
    	return palette.border;
    }
    
    protected Paint dashPaint(float width, float height, float pad, ButtonPalette palette) {
    	return palette.dash;
    }
    
    
    
}



