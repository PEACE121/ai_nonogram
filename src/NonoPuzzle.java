import gac.AStarAdapter;
import gac.ENextVariable;
import gac.GACState;
import gac.IDomainAttribute;
import gac.IGACObersvers;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.VI;

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
	private AStar				aStarInstance	= null;
	
	
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
		Map<String, NonoVariable> vars = new HashMap<String, NonoVariable>();
		
		List<IDomainAttribute> domains = new ArrayList<IDomainAttribute>();
		domains.add(new DomainColor(Color.black, 1));
		domains.add(new DomainColor(Color.white, 0));
		
		// fill variables
		for (int x = 0; x < grid.getDimensionX(); x++)
		{
			for (int y = 0; y < grid.getDimensionY(); y++)
			{
				String gridCell = createVariableName(x, y);
				vars.put(gridCell, new NonoVariable(gridCell, domains, x, y));
			}
		}
		
		// define constraints
		int xsize = grid.getRulesX().size();
		int ysize = grid.getRulesY().size();
		for (int y = 0; y < ysize; y++)
		{
			List<Integer> rowRules = grid.getRulesY().get(y);
			int freeFileds = calculateFreeFields(rowRules, xsize);
			String constraintRow = generateConstraintForRow("", rowRules, 0, 0, y, xsize, freeFileds, true);
			constraintRow = constraintRow.substring(0, constraintRow.length() - 4);
			System.out.println(constraintRow);
			constraints.add(new Constraint(constraintRow, createListOfVariables(y, true, vars)));
		}
		for (int x = 0; x < xsize; x++)
		{
			List<Integer> columnRules = grid.getRulesX().get(x);
			int freeFileds = calculateFreeFields(columnRules, ysize);
			String constraintColumn = generateConstraintForRow("", columnRules, 0, x, 0, ysize, freeFileds, false);
			constraintColumn = constraintColumn.substring(0, constraintColumn.length() - 4);
			System.out.println(constraintColumn);
			constraints.add(new Constraint(constraintColumn, createListOfVariables(x, false, vars)));
		}
		
		// init adapters,..
		List<Variable> variables = new ArrayList<Variable>(vars.values());
		AStarAdapter aStarGAC = new AStarAdapter(constraints, variables, heuristicGac);
		aStarGAC.register(this);
		aStarGAC.domainFilteringLoop();
		aStarInstance = new AStar(aStarGAC);
		aStarInstance.register(this);
	}
	
	
	private int calculateFreeFields(List<Integer> rules, int size)
	{
		int occupiedFields = -1;
		for (Integer block : rules)
		{
			occupiedFields += block + 1;
		}
		return size - occupiedFields;
	}
	
	
	private Map<String, Variable> createListOfVariables(int a, boolean isRow, Map<String, NonoVariable> variables)
	{
		Map<String, Variable> filteredVariables = new HashMap<String, Variable>();
		for (NonoVariable var : variables.values())
		{
			if ((isRow && var.getY() == a) || (!isRow && var.getX() == a))
			{
				filteredVariables.put(var.getName(), var);
			}
		}
		return filteredVariables;
	}
	
	
	private String generateConstraintForRow(String currentConstraint, List<Integer> blocks, int blockIndex, int x,
			int y, int sizeRowColumn, int freeFields, boolean isRow)
	{
		if (blockIndex == blocks.size())
		{
			while ((isRow && x < (sizeRowColumn)) || (!isRow && y < (sizeRowColumn)))
			{
				currentConstraint += createVariableName(x, y) + " == 0 && ";
				if (isRow)
				{
					x++;
				} else
				{
					y++;
				}
			}
			return currentConstraint.substring(0, currentConstraint.length() - 4) + " || ";
		}
		int block = blocks.get(blockIndex);
		
		String finalConstraint = "";
		for (int i = 0; i <= freeFields; i++)
		{
			int a;
			int currenta;
			if (isRow)
			{
				a = x;
				currenta = x + i;
			} else
			{
				a = y;
				currenta = y + i;
			}
			String constraintForBlock = "";
			for (int j = a; j < currenta; j++)
			{
				if (isRow)
				{
					constraintForBlock += createVariableName(j, y) + " == 0 && ";
				} else
				{
					constraintForBlock += createVariableName(x, j) + " == 0 && ";
				}
			}
			for (int k = 0; k < block; k++)
			{
				if (isRow)
				{
					constraintForBlock += createVariableName(currenta, y) + " == 1 && ";
				} else
				{
					constraintForBlock += createVariableName(x, currenta) + " == 1 && ";
				}
				currenta++;
			}
			if (currenta < sizeRowColumn)
			{
				if (isRow)
				{
					constraintForBlock += createVariableName(currenta, y) + " == 0 && ";
				} else
				{
					constraintForBlock += createVariableName(x, currenta) + " == 0 && ";
				}
				currenta++;
			}
			if (currenta <= sizeRowColumn)
			{
				if (isRow)
				{
					finalConstraint += generateConstraintForRow(currentConstraint + constraintForBlock, blocks,
							blockIndex + 1, currenta, y, sizeRowColumn, freeFields, isRow);
				} else
				{
					finalConstraint += generateConstraintForRow(currentConstraint + constraintForBlock, blocks,
							blockIndex + 1, x, currenta, sizeRowColumn, freeFields, isRow);
				}
			}
		}
		return finalConstraint;
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
		System.out.println("Assumption");
		GACState gacState = (GACState) app.getState();
		update(gacState, force);
	}
	
	
	@Override
	public void update(GACState x, boolean force)
	{
		System.out.println("Update");
		Position[][] positions = new Position[grid.getDimensionX()][grid.getDimensionY()];
		for (VI vi : x.getVis().values())
		{
			NonoVariable variable = (NonoVariable) vi.getVarInCNET();
			if (vi.getDomain().size() == 1)
			{
				positions[variable.getX()][variable.getY()] = new Position(variable.getX(), variable.getY(), vi.getDomain()
						.get(0).getNumericalRepresentation());
			} else if (vi.getDomain().size() == 0)
			{
				positions[variable.getX()][variable.getY()] = new Position(variable.getX(), variable.getY(), 99);
			}
		}
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
