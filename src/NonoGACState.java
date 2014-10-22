import gac.GACState;
import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.VI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NonoGACState extends GACState
{
	
	public NonoGACState(GACState old)
	{
		super(new HashMap<Variable, VI>(), new LinkedList<CI>());
		for (VI vi : old.getVis().values())
		{
			super.getVis().put(vi.getVarInCNET(), new VI(vi));
		}
		for (CI ci : old.getCis())
		{
			super.getCis().add(new NonoCI(ci, super.getVis()));
		}
	}
	
	
	public NonoGACState(Map<Variable, VI> vis, List<CI> cis)
	{
		super(vis, cis);
	}
}
