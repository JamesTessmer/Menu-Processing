package imUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import Model.Client;
import Model.Selections;

public final class CSVWriter {

	public static void writeToCSV(ArrayList<Client> clients)
	{
		//Init Date for time stamp:
		Date date = new Date();
		
		String fileName = "CLIENT_SELECTIONS_" + (date.getMonth() + 1) + "_" + date.getDate() + "_" + (date.getYear() - 100) + ".csv";
		
		try(PrintWriter pw = new PrintWriter(new File(fileName)))
		{
			StringBuilder sb = new StringBuilder();

			for(Client c : clients)
			{
				/**
				 * Temp format, until in hands of BNSF/MOW workers:
				 */
				sb.append("Client ID");
				sb.append("\n");
				sb.append(c.getClientID());
				sb.append("\n");
				sb.append("Selections");
				sb.append("\n");
				for(Selections s : c.getTableValues())
				{
					sb.append(s.getSelection());
					sb.append("\n");
				}
			}
			
			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
