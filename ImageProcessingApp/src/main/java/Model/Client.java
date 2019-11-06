package Model;


import java.util.ArrayList;

public class Client {

	private ArrayList<Character> selections;
	private ArrayList<Integer> daysList;
	private ArrayList<Selections> tableValues; //**NOTE** this is the list to be processed, and finalized into a .csv file
	private int clientID;
	private boolean needsDecision;
	private String imPath;
	
	public Client(int clientID, boolean needsDecision, String absPath)
	{

		this.selections = new ArrayList<Character>();
		this.daysList = new ArrayList<Integer>();
		this.tableValues = new ArrayList<Selections>();
		this.clientID = clientID;
		this.needsDecision = needsDecision;
		this.imPath = absPath;
	}
	
	public void fillTableValues()
	{
		
		//Add Drink and Sugar Choice:
		tableValues.add(new Selections("Desert", selections.get(0) + ""));
		tableValues.add(new Selections("Drink", selections.get(1) + ""));
		
		//Start index at '2' for initial selections
		for(int i = 2; i < 32; i++)
		{
			daysList.add(new Integer(i - 1));
		}
		
		for(int i = 2; i < selections.size(); i++)
		{
			String tmp_0 = daysList.get(i - 2) + "";
			String tmp_1 = selections.get(i) + "";

			tableValues.add(new Selections(tmp_0, tmp_1));
		}
	}
	
	public void debugSelections()
	{
		System.out.println("CLIENT ID: " + clientID);
		for(Character c : selections)
		{

			System.out.println(c + " SELECTION");
		}
	}

	public ArrayList<Character> getSelections() {
		return selections;
	}

	public void setSelections(ArrayList<Character> selections) {
		this.selections = selections;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public String getImPath() {
		return imPath;
	}

	public void setImPath(String imPath) {
		this.imPath = imPath;
	}

	public ArrayList<Selections> getTableValues() {
		return tableValues;
	}

	public void setTableValues(ArrayList<Selections> tableValues) {
		this.tableValues = tableValues;
	}

	public boolean isNeedsDecision() {
		return needsDecision;
	}

	public void setNeedsDecision(boolean needsDecision) {
		this.needsDecision = needsDecision;
	}
	
	
	
	
	
}
