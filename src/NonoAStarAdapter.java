import gac.AStarAdapter;
import gac.ENextVariable;
import gac.GACAlgorithm;
import gac.GACState;
import gac.IDomainAttribute;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.VI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astarframework.IState;


public class NonoAStarAdapter extends AStarAdapter
{
	
	public NonoAStarAdapter(List<Constraint> constraints, List<Variable> vars, ENextVariable next)
	{
		super();
		this.chooseNextComplex = next;
		
		// Create cis and vis
		Map<Variable, VI> vis = new HashMap<Variable, VI>();
		for (Variable var : vars)
		{
			vis.put(var, new VI(var, var.getFullDomainCopy()));
		}
		List<CI> cis = new LinkedList<CI>();
		for (Constraint constraint : constraints)
		{
			NonoCI ci = new NonoCI(constraint, super.filterVariables(vis, constraint.getVariables().values()));
			cis.add(ci);
		}
		GACState state = new NonoGACState(vis, cis);
		super.setGacAlgorithm(new GACAlgorithm(state));
		super.setGacStartState(super.getGacAlgorithm().getState());
	}
	
	
	@Override
	protected List<IState> generateSuccesorsOfVI(VI vi, GACState gacState)
	{
		List<IState> successors = new LinkedList<IState>();
		for (int j = 0; j < vi.getDomain().size(); j++)
		{
			GACState newState = new NonoGACState(gacState);
			IDomainAttribute newDom = newState.getVis().get(vi.getVarInCNET()).getDomain().get(j);
			List<IDomainAttribute> newDoms = new LinkedList<IDomainAttribute>();
			newDoms.add(newDom);
			newState.getVis().get(vi.getVarInCNET()).setDomain(newDoms);
			newState.setLastGuessed(newDom);
			newState.setLastGuessedVar(vi.getVarInCNET());
			super.getGacAlgorithm().rerun(newState, newState.getVis().get(vi.getVarInCNET()));
			// only add the state if it is solvable! ignore states with contradictions, that are dead ends
			if (newState.isStillSolvable() && !isApplicationDeadEnd(newState))
			{
				successors.add(newState);
			}
			// inform(newState);
		}
		return successors;
	}
	
}
