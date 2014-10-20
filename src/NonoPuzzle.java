import gac.ENextVariable;
import gac.GACState;
import gac.IDomainAttribute;
import gac.IGACObersvers;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import algorithms.AStar;
import astarframework.IAStarObersvers;
import astarframework.Node;


public class NonoPuzzle implements IGACObersvers, IAStarObersvers
{
	private final NonoGrid	grid;
	private final AStar		aStarInstance	= null;
	
	
	public NonoPuzzle(String fileName)
	{
		this(fileName, ENextVariable.COMPLEX2, EConstraintType.STANDARD);
	}
	
	
	/**
	 * 
	 */
	public NonoPuzzle(String fileName, ENextVariable heuristicGac, EConstraintType constraintType)
	{
		super();
		
		grid = new NonoGrid();
		grid.readInput(fileName);
		
		JFrame f = new JFrame();
		f.getContentPane().add(grid);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(NonoGrid.MAX_SIZE, NonoGrid.MAX_SIZE);
		f.setVisible(true);
		
		List<Constraint> constraints = new LinkedList<Constraint>();
		Map<String, Variable> vars = new HashMap<String, Variable>();
		
		List<IDomainAttribute> domains = new ArrayList<IDomainAttribute>();
		domains.add(new DomainColor(Color.black, 0));
		domains.add(new DomainColor(Color.white, 1));
		
		// fill variables
		for (int i = 0; i < grid.getGridSize(); i++)
		{
			for (int j = 0; j < grid.getGridSize(); j++)
			{
				String gridCell = createVariableName(i, j);
				vars.put(gridCell, new NonoVariable(gridCell, domains, i, j));
			}
		}
		
		// define constraints
		int xsize = grid.getRulesX().size();
		int ysize = grid.getRulesY().size();
		for (int y = 0; y < ysize; y++)
		{
			List<Integer> rowRules = grid.getRulesY().get(y);
			String constraint = "";
			int occupiedFields = -1;
			for (Integer block : rowRules)
			{
				occupiedFields += block + 1;
			}
			int freeFields = xsize - occupiedFields;
			System.out.println(generateConstraintForRow("", rowRules, 0, 0, y, xsize, freeFields));
		}
		
		// init adapters,..
		// List<Variable> variables = new ArrayList<Variable>(vars.values());
		// AStarAdapter aStarGAC = new AStarAdapter(constraints, variables, heuristicGac, grid);
		// aStarGAC.register(this);
		// aStarGAC.domainFilteringLoop();
		// aStarInstance = new AStar(aStarGAC);
		// aStarInstance.register(this);
	}
	
	
	private String generateConstraintForRow(String currentConstraint, List<Integer> blocks, int blockIndex, int x,
			int y, int sizex, int freeFields)
	{
		if (blockIndex == blocks.size())
		{
			while (x < (sizex))
			{
				currentConstraint += createVariableName(x, y) + " == 0 && ";
				x++;
			}
			return currentConstraint.substring(0, currentConstraint.length() - 4) + " || ";
		}
		int block = blocks.get(blockIndex);
		
		for (int i = 0; i <= freeFields; i++)
		{
			int currentx = x + i;
			String constraintForBlock = "";
			if (blockIndex == 0)
			{
				for (int j = 0; j < currentx; j++)
				{
					constraintForBlock += createVariableName(j, y) + " == 0 && ";
				}
			}
			for (int k = 0; k < block; k++)
			{
				constraintForBlock += createVariableName(currentx, y) + " == 1 && ";
				currentx++;
			}
			if (currentx < sizex)
			{
				constraintForBlock += createVariableName(currentx, y) + " == 0 && ";
				currentx++;
			}
			if (currentx > sizex)
			{
				return "";
			}
			currentConstraint += generateConstraintForRow(constraintForBlock, blocks, blockIndex + 1, currentx, y, sizex,
					freeFields);
		}
		return currentConstraint;
	}
	
	
	public void run()
	{
		long start = System.currentTimeMillis();
		aStarInstance.run();
		long runtime = System.currentTimeMillis() - start;
		System.out.println("Runtime: " + runtime + " ms");
	}
	
	
	private String createVariableName(int x, int y)
	{
		return "pos" + x + "a" + y;
	}
	
	
	@Override
	public void update(Node app, boolean force)
	{
		GACState gacState = (GACState) app.getState();
		update(gacState, force);
	}
	
	
	@Override
	public void update(GACState x, boolean force)
	{
		Position[][] positions = new Position[grid.getGridSize()][grid.getGridSize()];
		// for (VI vi : x.getVis().values())
		// {
		// NonoVariable variable = (NonoVariable) vi.getVarInCNET();
		// if (vi.getDomain().size() == 1)
		// {
		// positions[variable.getX()][variable.getY()] = new Position(variable.getX(), variable.getY(), vi.getDomain()
		// .get(0).getNumericalRepresentation());
		// } else if (vi.getDomain().size() == 0)
		// {
		// positions[variable.getX()][variable.getY()] = new Position(variable.getX(), variable.getY(), 99);
		// }
		// }
		grid.setPositions(positions);
		grid.refreshField();
		try
		{
			Thread.sleep(0);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}
}
