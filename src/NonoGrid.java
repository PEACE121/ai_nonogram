import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class NonoGrid extends JPanel
{
	
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 2838947983271757774L;
	
	static final int					MAX_SIZE				= 600;
	private int							rectSize;
	
	private Position[][]				positions;
	
	private List<List<Integer>>	rulesY;
	private List<List<Integer>>	rulesX;
	
	int									dimensionX			= 0;
	int									dimensionY			= 0;
	
	
	public NonoGrid()
	{
	}
	
	
	// -------------------------------------------------------------------------
	// ------------------------------------ GUI --------------------------------
	// -------------------------------------------------------------------------
	
	public void refreshField()
	{
		this.repaint();
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}
	
	
	public void draw(Graphics g)
	{
		if (positions != null)
		{
			for (int i = 0; i < positions.length; i++)
			{
				for (int j = 0; j < positions[0].length; j++)
				{
					if (positions[i][j] != null)
					{
						g.setColor((positions[i][j].getColor().getColor()));
						g.fillRect(rectSize * i, (rectSize * (positions[0].length - 1)) - rectSize * j, rectSize, rectSize);
					}
				}
			}
		}
		
		// Display the window.
		setVisible(true);
	}
	
	
	// -------------------------------------------------------------------------
	// --------------------------- Inpupt Processing ---------------------------
	// -------------------------------------------------------------------------
	private static float getFloatFromPair(String pair, int index)
	{
		return Float.parseFloat(getStringFromPair(pair, index));
	}
	
	
	private static int getIntFromPair(String pair, int index)
	{
		return Integer.parseInt(getStringFromPair(pair, index));
	}
	
	
	private static String getStringFromPair(String pair, int index)
	{
		pair = pair.replaceAll("\\(", "");
		pair = pair.replaceAll("\\)", "");
		String[] array = pair.split(" ");
		return array[index];
	}
	
	
	@SuppressWarnings("resource")
	public Position[][] readInput(String fileName)
	{
		rulesY = new ArrayList<List<Integer>>();
		rulesX = new ArrayList<List<Integer>>();
		try
		{
			File file = new File(fileName);
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			String line;
			int lineNr = 0;
			while ((line = br.readLine()) != null)
			{
				switch (lineNr)
				{
					case 0:
						dimensionX = getIntFromPair(line, 0);
						dimensionY = getIntFromPair(line, 1);
						positions = new Position[dimensionX][dimensionY];
						break;
					default:
						List<List<Integer>> listToAdd;
						if (lineNr < (dimensionY + 1))
						{
							listToAdd = rulesY;
						} else
						{
							listToAdd = rulesX;
						}
						List<Integer> rule = new ArrayList<Integer>();
						for (int i = 0; i < line.split(" ").length; i++)
						{
							rule.add(getIntFromPair(line, i));
						}
						listToAdd.add(rule);
				}
				lineNr++;
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		rectSize = MAX_SIZE / positions.length;
		return positions;
	}
	
	
	// -------------------------------------------------------------------------
	// --------------------------- Getter and Setter ---------------------------
	// -------------------------------------------------------------------------
	
	public int getGridSize()
	{
		return positions.length;
	}
	
	
	/**
	 * @return the positions
	 */
	public Position[][] getPositions()
	{
		return positions;
	}
	
	
	/**
	 * @param positions the positions to set
	 */
	public void setPositions(Position[][] positions)
	{
		this.positions = positions;
	}
	
	
	/**
	 * @return the rulesY
	 */
	public List<List<Integer>> getRulesY()
	{
		return rulesY;
	}
	
	
	/**
	 * @param rulesY the rulesY to set
	 */
	public void setRulesY(List<List<Integer>> rulesY)
	{
		this.rulesY = rulesY;
	}
	
	
	/**
	 * @return the rulesX
	 */
	public List<List<Integer>> getRulesX()
	{
		return rulesX;
	}
	
	
	/**
	 * @param rulesX the rulesX to set
	 */
	public void setRulesX(List<List<Integer>> rulesX)
	{
		this.rulesX = rulesX;
	}
	
	
}