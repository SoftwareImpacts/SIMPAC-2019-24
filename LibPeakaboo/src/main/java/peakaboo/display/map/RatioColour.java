package peakaboo.display.map;



import java.awt.Color;



public enum RatioColour
{

	RED {
		
		@Override
		public Color toColor()
		{
			return new Color(0.64f, 0.00f, 0.00f);
		}
	},
	BLUE {
		
		@Override
		public Color toColor()
		{
			return new Color(0.07f, 0.16f, 0.30f);
		}
	};


	public Color toColor()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
