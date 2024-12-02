package MainProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SDCardDriver {

    private File file;
    private boolean initialized = false ;
    private boolean corrupted = false ;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Mode mode;
    private boolean hasFailed;

    public enum Mode {
        R, W
    }
    
    public SDCardDriver(String filePath, char mode) {
        this.file = new File(filePath);
        if (mode == 'R') {
            this.mode = Mode.R;
        }
        if (mode == 'W') {
            this.mode = Mode.W;
        }
        try {
            openFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile() throws IOException {
        if (mode == Mode.R) {
            if (!file.exists()) {
                throw new IOException("File not found for reading: " + file.getAbsolutePath());
            }
            reader = new BufferedReader(new FileReader(file));
            reader.mark(0);  // Mark the current position
        } else {
            // Check if file exists for write mode, if not create it
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (isCreated) {
                    //System.out.println("File created: " + file.getAbsolutePath());
                } else {
                    throw new IOException("Failed to create file: " + file.getAbsolutePath());
                }
            }
            writer = new BufferedWriter(new FileWriter(file, true));  // 'true' for appending
        }
    }

    public void closeFile() throws IOException {

        if(reader != null){
            reader.close();
        }
        {if (writer != null){
            writer.close();
        }
        }
    }

    public String[] read() throws IOException {
        if (mode != Mode.R) {
            throw new IllegalStateException("SDCardDriver is not in read mode.");
        }

        // List to store lines read from the file
        List<String> lines = new ArrayList<>();

        try {
            // Reinitialize the reader to start from the beginning of the file
            reader.close(); // Close the existing reader
            reader = new BufferedReader(new FileReader(file)); // Reinitialize reader

            // Read lines into the list
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }

        // Convert the list to an array and return
        return lines.toArray(new String[0]);
    }


    public void write(String line) {
        if (mode == Mode.R) {
            throw new IllegalStateException("SDCardDriver is not in write mode. Trying to access Write Mode.");
        }
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // checks for failure if file exist, or SDCard has been initialized, and is not corrupted
    public boolean checkForFailure(){
        return !initialized && !file.exists() && corrupted;
    }

    public void setFailureStatus(boolean fail) {
        hasFailed = fail;
    }

    public boolean getFailureStatus() {
        //hasFailed = checkForFailure();
        return hasFailed;
    }

    public static void main(String[] args) {
        // Path for the SDCard simulation file
        String filePath = "sdCardDriver1.txt";

        // Create an SDCardDriver instance in write mode (Mode.W)
        SDCardDriver writeDriver = new SDCardDriver(filePath, 'W');

        try {
            // Write data to the file (simulating writing to the SD card)
            writeDriver.write("Hello, this is the first line.");
            writeDriver.write("This is the second line.");
            writeDriver.write("This is the third line.");

            // Close the writer after writing
            writeDriver.closeFile();

            // Now, create an SDCardDriver instance in read mode (Mode.R)
            SDCardDriver readDriver = new SDCardDriver(filePath, 'R');

            try {
                // Read the data back from the SD card (file)
                String[] data = readDriver.read();

                // Print the data that was read from the SD card
                System.out.println("Data read from sdCardDriver1.txt:");
                for (String line : data) {
                    System.out.println(line);
                }

                // Close the reader after reading
                readDriver.closeFile();

            } catch (IOException e) {
                System.err.println("Error reading the file: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }

        // Check for failure (if the SD card is not initialized or corrupted)
        SDCardDriver failureCheckDriver = new SDCardDriver(filePath, 'W');
        if (failureCheckDriver.getFailureStatus()) {
            System.out.println("SDCardDriver is in failure state.");
        } else {
            System.out.println("SDCardDriver is working properly.");
        }

        // Check if the file exists
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("The file exists: " + filePath);
        } else {
            System.out.println("The file does not exist: " + filePath);
        }
    }
}
