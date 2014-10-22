import gac.IDomainAttribute;


public class DomainCombination implements IDomainAttribute
{
	
	private final Integer[]	combination;
	
	private final boolean	isRow;
	
	private final int			rowColumn;
	
	
	/**
	 * @param combination
	 * @param isRow
	 * @param rowColumn
	 */
	public DomainCombination(Integer[] combination, boolean isRow, int rowColumn)
	{
		super();
		this.combination = combination;
		this.isRow = isRow;
		this.rowColumn = rowColumn;
	}
	
	
	@Override
	public int getNumericalRepresentation()
	{
		return -1;
	}
	
	
	public int getValue(int a)
	{
		return combination[a];
	}
	
	
	/**
	 * @return the rowColumn
	 */
	public int getRowColumn()
	{
		return rowColumn;
	}
	
	
	/**
	 * @return the isRow
	 */
	public boolean isRow()
	{
		return isRow;
	}
	
	
	/**
	 * @return the combination
	 */
	public Integer[] getCombination()
	{
		return combination;
	}
	
	
}
