package imUtils;

import Model.Client;
import Model.Selections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

/**
 * Created this class to organize some of the functions that go into
 * creating/updating JavaFX's table. 
 * @author Jacob
 *
 */
public final class FXTableUI {

	public static void setTable(Client currClient, TableView tv, TableColumn colChoice, TableColumn colDay)
	{
		tv.getItems().clear();
		final ObservableList<Selections> data = FXCollections.observableArrayList();
		data.clear();
		
		for(Selections s : currClient.getTableValues())
		{
			data.add(s);
		}
		
		colDay.setCellValueFactory(new PropertyValueFactory("day"));
		colChoice.setCellValueFactory(new PropertyValueFactory("selection"));
		tv.setItems(data);
		tv.setEditable(true);
		tv.refresh();
	}
	

	


}
