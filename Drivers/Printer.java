package Drivers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Printer {


    private Boolean hasFailure;
    private final File outputFile;
    private FileWriter fileWriter;


    public Printer(String fileName) {
        this.hasFailure = false;
        this.outputFile = new File(fileName);
        try {
            // Create a new empty file or clear the file if it already exists
            if (outputFile.exists()) {
                // outputFile.delete(); // Delete existing file
            }
            outputFile.createNewFile();
            //this.fileWriter = new FileWriter(outputFile, true); // Append mode
        } catch (IOException e) {
            //toggleHasFailure();
            System.out.println("Error creating the file: " + e.getMessage());
        }
    }


    // open file in append mode
    public void on() {
        try {
            fileWriter = new FileWriter(outputFile, true); //apend mode
            System.out.println("File opened for writing: " + outputFile.getName());
        } catch (IOException e) {
            System.out.println("Error opening the file: " + e.getMessage());
            hasFailure = true;
        }
    }


    // Close file
    public void off() {
        try {
            if (fileWriter != null) {
                fileWriter.flush(); // Ensure all data is written to the file
                fileWriter.close();
                System.out.println("File Closed: " + outputFile.getName());
            }
        } catch (IOException e) {
            System.out.println("Error closing the file: " + e.getMessage());
            hasFailure = true;
        }
    }


    public void printLine(String line) {
        if (!hasFailure) {
            try {
                fileWriter.write(line + "\n");
                fileWriter.flush(); // Ensure the content is written to the file
            } catch (IOException e) {
                //toggleHasFailure();
                System.out.println("Error writing to the file: " + e.getMessage());
                hasFailure = true;
            }
        }
    }


    public void printEmptyLine() {
        if (!hasFailure) {
            try {
                fileWriter.write("\n");
                fileWriter.flush(); // Ensure the content is written to the file
            } catch (IOException e) {
                //toggleHasFailure();
                System.out.println("Error writing to the file: " + e.getMessage());
                hasFailure = true;
            }
        }
    }


    public boolean getHasFailure() { return hasFailure; }
}
