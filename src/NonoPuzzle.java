import gac.ENextVariable;
import gac.GACState;
import gac.IDomainAttribute;
import gac.IGACObersvers;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.VI;

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
		Map<String, Variable> vars = new HashMap<String, Variable>();
		
		
		int xsize = grid.getRulesX().size();
		int ysize = grid.getRulesY().size();
		// fill variables
		for (int x = 0; x < grid.getDimensionX(); x++)
		{
			String columnName = "column" + x;
			List<IDomainAttribute> domains = new ArrayList<IDomainAttribute>();
			List<Integer> columnRules = grid.getRulesX().get(x);
			int freeFileds = calculateFreeFields(columnRules, ysize);
			String constraintColumn = generateConstraintForRow("", columnRules, 0, x, 0, ysize, freeFileds, false);
			System.out.println(constraintColumn);
			for (Integer[] domainCombination : parseOldConstraint(constraintColumn))
			{
				domains.add(new DomainCombination(domainCombination, false, x));
			}
			vars.put(columnName, new Variable(columnName, domains));
		}
		for (int y = 0; y < grid.getDimensionY(); y++)
		{
			String rowName = "row" + y;
			List<IDomainAttribute> domains = new ArrayList<IDomainAttribute>();
			List<Integer> rowRules = grid.getRulesY().get(y);
			int freeFileds = calculateFreeFields(rowRules, xsize);
			String constraintRow = generateConstraintForRow("", rowRules, 0, 0, y, xsize, freeFileds, true);
			System.out.println(constraintRow);
			for (Integer[] domainCombination : parseOldConstraint(constraintRow))
			{
				domains.add(new DomainCombination(domainCombination, true, y));
			}
			vars.put(rowName, new Variable(rowName, domains));
		}
		
		// define constraints
		for (int x = 0; x < grid.getDimensionX(); x++)
		{
			for (int y = 0; y < grid.getDimensionY(); y++)
			{
				String hashKeyRow = "row" + y;
				String hashKeyColumn = "column" + x;
				Variable variableColumn = vars.get(hashKeyColumn);
				Variable variableRow = vars.get(hashKeyRow);
				Map<String, Variable> variables = new HashMap<String, Variable>();
				variables.put(hashKeyRow, variableRow);
				variables.put(hashKeyColumn, variableColumn);
				constraints.add(new Constraint(hashKeyColumn + "==" + hashKeyRow, variables));
			}
		}
		
		// init adapters,..
		// NashornScriptEngine.getInstance().setEvalType(EEvaluationType.NONO_HACK);
		List<Variable> variables = new ArrayList<Variable>(vars.values());
		NonoAStarAdapter aStarGAC = new NonoAStarAdapter(constraints, variables, heuristicGac);
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
	
	
	private String generateConstraintForRow(String currentConstraint, List<Integer> blocks, int blockIndex, int x,
			int y, int sizeRowColumn, int freeFields, boolean isRow)
	{
		if (blockIndex == blocks.size())
		{
			while ((isRow && x < (sizeRowColumn)) || (!isRow && y < (sizeRowColumn)))
			{
				currentConstraint += "0 && ";
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
					constraintForBlock += "0 && ";
				} else
				{
					constraintForBlock += "0 && ";
				}
			}
			for (int k = 0; k < block; k++)
			{
				if (isRow)
				{
					constraintForBlock += "1 && ";
				} else
				{
					constraintForBlock += "1 && ";
				}
				currenta++;
			}
			if (currenta < sizeRowColumn)
			{
				if (isRow)
				{
					constraintForBlock += "0 && ";
				} else
				{
					constraintForBlock += "0 && ";
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
	
	
	private List<Integer[]> parseOldConstraint(String input)
	{
		input = input.replaceAll(" ", "");
		String[] combis = input.split("\\|\\|");
		List<Integer[]> domainCombinations = new ArrayList<Integer[]>();
		for (String combi : combis)
		{
			String[] values = combi.split("&&");
			Integer[] domainCombination = new Integer[values.length];
			for (int i = 0; i < domainCombination.length; i++)
			{
				domainCombination[i] = Integer.parseInt(values[i]);
			}
			domainCombinations.add(domainCombination);
		}
		return domainCombinations;
	}
	
	
	public void run()
	{
		long start = System.currentTimeMillis();
		aStarInstance.run();
		long runtime = System.currentTimeMillis() - start;
		System.out.println("Runtime: " + runtime + " ms");
	}
	
	
	@Override
	public void update(Node app, boolean force)
	{
		GACState gacState = (GACState) app.getState();
		update(gacState, force);
	}
	
	
	@Override
	public void update(GACState gacState, boolean force)
	{
		Position[][] positions = new Position[grid.getDimensionX()][grid.getDimensionY()];
		for (VI vi : gacState.getVis().values())
		{
			if (vi.getDomain().size() == 1)
			{
				DomainCombination domain = (DomainCombination) vi.getDomain().get(0);
				for (int i = 0; i < domain.getCombination().length; i++)
				{
					int x;
					int y;
					if (domain.isRow())
					{
						x = i;
						y = domain.getRowColumn();
					} else
					{
						x = domain.getRowColumn();
						y = i;
					}
					int value = domain.getValue(i);
					if (positions[x][y] == null)
					{
						positions[x][y] = new Position(x, y, value);
					} else if (value != positions[x][y].getValue())
					{
						positions[x][y] = new Position(x, y, 99);
					}
					
				}
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
