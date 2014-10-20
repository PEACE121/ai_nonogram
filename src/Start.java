import gac.ENextVariable;


public class Start
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		NonoPuzzle nonoPuzzle = null;
		ENextVariable next = ENextVariable.COMPLEX2;
		EConstraintType constraintType = EConstraintType.STANDARD;
		switch (args.length)
		{
			case 3:
				constraintType = EConstraintType.valueOf(args[2]);
			case 2:
				next = ENextVariable.valueOf(args[1]);
			case 1:
				System.out.println(args[0]);
				nonoPuzzle = new NonoPuzzle(args[0], next, constraintType);
				break;
			default:
				System.out
						.println("Usage: Start <filename> <GAC heuristic> <constraint_type> \n Heuristics: COMPLEX1, COMPLEX2 for complex versions, SIMPLE for simple \n ConstraintTypes: MAX_4 or MAX_5");
				break;
		}
		if (nonoPuzzle != null)
		{
			nonoPuzzle.run();
		}
		
		// new FlowPuzzle("flowspec-0.txt");
		// new FlowPuzzle("flowspec-1.txt");
		// new FlowPuzzle("flowspec-2.txt");
		// new FlowPuzzle("flowspec-3.txt");
		// new FlowPuzzle("flowspec-4.txt");
		// new FlowPuzzle("flowspec-5.txt");
		// new FlowPuzzle("flowspec-6.txt");
	}
}
