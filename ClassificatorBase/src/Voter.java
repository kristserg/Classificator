
public class Voter 
{
	public String[] attr;
	
	public Voter(String[] a)
	{
		this.attr = a;
	}
	
	public void setParty(String p)
	{
		this.attr[0] = p;
	}	
	
	public String getParty()
	{
		return this.attr[0];
	}
}
