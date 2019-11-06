package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Selections {

	private final SimpleStringProperty day;
	private final SimpleStringProperty selection;
	
	public Selections(String day, String selection)
	{
		this.day = new SimpleStringProperty(day);
		this.selection = new SimpleStringProperty(selection);
	}

	public String getDay() {
		return this.day.get();
	}

	public void setDay(String day) {
		this.day.set(day);
	}

	public StringProperty day()
	{
		return this.day;
	}
	
	public String getSelection() {
		return selection.get();
	}

	public void setSelection(String selection) {
		this.selection.set(selection);
	}
	
	public StringProperty dayProperty()
	{
		return day;
	}
	
	public StringProperty selectionProperty()
	{
		return selection;
	}
}
