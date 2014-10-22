import gac.IDomainAttribute;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.VI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NonoCI extends CI
{
	
	public NonoCI(CI oldCI, Map<Variable, VI> newVIs)
	{
		super(oldCI, newVIs);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @param consInCNET
	 * @param variables
	 */
	public NonoCI(Constraint consInCNET, List<VI> variables)
	{
		super(consInCNET, variables);
	}
	
	
	@Override
	public boolean revise(VI x)
	{
		List<IDomainAttribute> toDeleteFromX = new LinkedList<IDomainAttribute>();
		List<VI> neighbours = new ArrayList<VI>();
		for (VI vi : super.getVIs())
		{
			if (!vi.equals(x))
			{
				neighbours.add(vi);
			}
		}
		nextDom: for (IDomainAttribute dom : x.getDomain())
		{
			for (IDomainAttribute dom2 : neighbours.get(0).getDomain())
			{
				DomainCombination domCombination = (DomainCombination) dom;
				DomainCombination domCombination2 = (DomainCombination) dom2;
				Map<String, Integer> varAssign = new HashMap<String, Integer>();
				varAssign.put(x.getVarInCNET().getName(), domCombination.getValue(domCombination2.getRowColumn()));
				varAssign.put(neighbours.get(0).getVarInCNET().getName(),
						domCombination2.getValue(domCombination.getRowColumn()));
				if (super.getConsInCNET().eval(varAssign))
				{
					continue nextDom;
				}
			}
			toDeleteFromX.add(dom);
		}
		x.getDomain().removeAll(toDeleteFromX);
		// if (x.getDomain().size() == 0)
		// {
		// System.out.println("Stop");
		// }
		return !(toDeleteFromX.size() == 0);
	}
	
	
	@Override
	/**
	 * assumes that all VIs have only one domain left
	 * @return
	 */
	public boolean consistencyCheck()
	{
		Map<String, Integer> variableAssignments = new HashMap<String, Integer>();
		VI vi1 = super.getVIs().get(0);
		VI vi2 = super.getVIs().get(1);
		DomainCombination domainCombination1 = (DomainCombination) vi1.getDomain().get(0);
		DomainCombination domainCombination2 = (DomainCombination) vi2.getDomain().get(0);
		variableAssignments.put(vi1.getVarInCNET().getName(),
				domainCombination1.getValue(domainCombination2.getRowColumn()));
		variableAssignments.put(vi2.getVarInCNET().getName(),
				domainCombination2.getValue(domainCombination1.getRowColumn()));
		return super.getConsInCNET().eval(variableAssignments);
	}
}
