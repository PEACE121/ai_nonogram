import java.awt.Color;

import astarframework.IState;


public class Position implements IState
{
	private int				x;
	private int				y;
	
	private DomainColor	color	= new DomainColor(Color.black, 0);
	
	
	/**
	 * @param x
	 * @param y
	 */
	public Position(int x, int y)
	{
		super();
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * @return the color
	 */
	public DomainColor getColor()
	{
		return color;
	}
	
	
	/**
	 * @param color the color to set
	 */
	public void setColor(DomainColor color)
	{
		this.color = color;
	}
	
	
	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}
	
	
	/**
	 * @param x the x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	
	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}
	
	
	/**
	 * @param y the y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	
	@Override
	public boolean isTheSame(IState object)
	{
		if (object instanceof Position)
		{
			Position p = (Position) object;
			return this.x == p.x && this.y == p.y;
		}
		System.out.println("Warning: Comparing Position with something else");
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Position [x=" + x + ", y=" + y + "]";
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
