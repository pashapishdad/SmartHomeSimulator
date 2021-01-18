package service;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConsoleService {

    private static PrintWriter printWriterGeneral = null;
    private static PrintWriter printWriterSHS = null;
    private static PrintWriter printWriterSHH = null;
    private static List<String> consoleList = new ArrayList<String>();

    static {
        try {
            printWriterGeneral = new PrintWriter(FileUtils.getFile("src", "main", "resources", "consoleLogs.txt"));
			printWriterSHS = new PrintWriter(FileUtils.getFile("src", "main", "resources", "consoleLogsSHS.txt"));
			printWriterSHH = new PrintWriter(FileUtils.getFile("src", "main", "resources", "consoleLogsSHH.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function responsible for updating the log file
     *
     * @param consoleLog The string to be appended onto the log file
     */
    public static void exportConsole(final String consoleLog) {
    	consoleList.add(consoleLog);
        printWriterGeneral.append(consoleLog);
        printWriterGeneral.flush();
    }

    /**
     * Function responsible for updating the log file for SHS
     *
     * @param consoleLog The string to be appended onto the log file
     */
    public static void exportConsoleSHS(final String consoleLog) {
    	exportConsole(consoleLog);
        printWriterSHS.append(consoleLog);
		printWriterSHS.flush();
    }

    /**
     * Function responsible for updating the log file for SHH
     *
     * @param consoleLog The string to be appended onto the log file
     */
    public static void exportConsoleSHH(final String consoleLog) {
        exportConsole(consoleLog);
        printWriterSHH.append(consoleLog);
		printWriterSHH.flush();
    }
    
    /**
     * Return the compiled console list as string
     * @return console log string list
     */
    public static String getConsole() {
    	return String.join("", consoleList);
    }
    

    /**
     * Initialize process, check potential IO problem
     */
	public static void initialize() {
		File file = FileUtils.getFile("src", "main", "resources", "consoleLogs.txt");
		if(!file.exists()) {
			try {
				FileUtils.touch(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		PrintWriter output = null;
		try {
			output = new java.io.PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		output.print("");
		output.close();
		
	}
}
