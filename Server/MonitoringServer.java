package Server;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class MonitoringServer {


    private static final Map<String, PrintWriter> clients = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, JPanel> clientPanels = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, JTextArea> deviceLogs = Collections.synchronizedMap(new HashMap<>());
    private static JPanel mainPanel;


    public static void main(String[] args) {
        // Launch the GUI in a separate thread
        SwingUtilities.invokeLater(MonitoringServer::createAndShowGUI);

        // Set up the server socket
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for clients...");

            // Start a thread to accept client connections
            new Thread(() -> {
                while (true) {
                    try {
                        final Socket clientSocket = serverSocket.accept();
                        System.out.println("New client connected: " + clientSocket.getInetAddress());
                        new Thread(() -> handleClientConnection(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Start reading user input to send messages to clients
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Format: Client:Device:Int:Message (e.g., DeviceManager:Screen:1:Hello)");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                String[] parts = input.split(":", 4);
                if (parts.length == 3){
                    String clientName = parts[0].trim();
                    String deviceName = parts[1].trim();
                    int choice = Integer.parseInt(parts[2].trim());
                    String message = "";
                    sendMessageToClient(clientName, deviceName, choice,message);
                }else if (parts.length == 4) {
                    String clientName = parts[0].trim();
                    String deviceName = parts[1].trim();
                    int choice = Integer.parseInt(parts[2].trim());
                    String message = parts[3].trim();

                    sendMessageToClient(clientName, deviceName, choice, message);
                } else {
                    System.out.println("Invalid input format. Use: Client:Device:Int:Message");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Central Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Main panel to hold client rows
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Scroll pane for the main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    private static void addClientRow(String clientName) {
        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        clientPanel.setBorder(BorderFactory.createTitledBorder(clientName));

        SwingUtilities.invokeLater(() -> {
            mainPanel.add(clientPanel);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        clientPanels.put(clientName, clientPanel);
    }

    private static void addDeviceToClient(String clientName, String deviceName) {
        JPanel clientPanel = clientPanels.get(clientName);
        if (clientPanel == null) {
            System.out.println("Client panel not found for: " + clientName + ". Adding new row.");
            addClientRow(clientName);
            clientPanel = clientPanels.get(clientName);
        }

        String compositeKey = clientName + ":" + deviceName;
        if (deviceLogs.containsKey(compositeKey)) {
            return;
        }

        JPanel devicePanel = createDevicePanel(deviceName);

        JPanel finalClientPanel = clientPanel;
        SwingUtilities.invokeLater(() -> {
            finalClientPanel.add(devicePanel);
            finalClientPanel.revalidate();
            finalClientPanel.repaint();
        });

        JTextArea logArea = (JTextArea) ((JScrollPane) devicePanel.getComponent(1)).getViewport().getView();
        deviceLogs.put(compositeKey, logArea);
    }

    private static JPanel createDevicePanel(String deviceName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(deviceName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea(5, 15);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static void handleDeviceInput(String clientName, String input) {
        System.out.println("input: " + input);
        if (input.startsWith("SD Card Driver:SDCardData:")) {
            String sdCardData = input.substring("SD Card Driver:SDCardData:".length());
            sdCardData = sdCardData.replace("[", "").replace("]", "");
            //System.out.println("Received SD Card data from DeviceManager: " + sdCardData);
            forwardToScreenManager(sdCardData);
        }
        else if (input.contains(":")) {
            String[] parts = input.split(":", 2);
            String deviceName = parts[0].trim();
            String message = parts[1].trim();

            // Do not create a device panel for SD Card data
            if (!"SD Card Driver".equalsIgnoreCase(deviceName)) {
                addDeviceToClient(clientName, deviceName);
            }else{

            addDeviceToClient(clientName, deviceName);
            }


            String compositeKey = clientName + ":" + deviceName;
            JTextArea logArea = deviceLogs.get(compositeKey);

            if (logArea != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDate = formatter.format(new Date());
                SwingUtilities.invokeLater(() -> logArea.append("[" + formattedDate + "] " + message + "\n"));
            } else {
                System.out.println("Log area not found for: " + compositeKey);
            }
        } else {
            System.out.println("Invalid input format: " + input);
        }
    }

    private static void forwardToScreenManager(String sdCardData) {
        String screenManagerClientName = "ScreenManager";
        PrintWriter clientOut = clients.get(screenManagerClientName);
        if (clientOut != null) {
            clientOut.println("SDCardData:" + sdCardData);
            System.out.println("Forwarded SD Card data to ScreenManager.");
        } else {
            System.out.println("ScreenManager is not connected.");
        }
    }

    private static void handleClientConnection(Socket clientSocket) {
        String clientName = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Enter your name (e.g., 'DeviceManager'):");
            clientName = in.readLine();
            System.out.println("Welcome " + clientName);

            if (clientName != null && !clientName.trim().isEmpty()) {
                synchronized (clients) {
                    if (clients.containsKey(clientName)) {
                        out.println("Name already in use. Disconnecting.");
                        return;
                    }
                    clients.put(clientName, out);
                }
                addClientRow(clientName);
            } else {
                out.println("Invalid name. Disconnecting.");
                return;
            }

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from " + clientName + ": " + message);
                handleDeviceInput(clientName, message);
            }

        } catch (IOException e) {
            System.out.println("Client " + clientName + " disconnected.");
        } finally {
            if (clientName != null) {
                removeClientRow(clientName);
                synchronized (clients) {
                    clients.remove(clientName);
                }
                System.out.println("Cleaned up resources for client: " + clientName);
            }
        }
    }

    private static void sendMessageToClient(String clientName, String deviceName, int choice,String message) {
        PrintWriter clientOut = clients.get(clientName);
        if (clientOut != null) {
            clientOut.println(clientName + ":" + deviceName + ":" + choice + ":" + message);
            //System.out.println("Sent message to client " + clientName + " (device: " + deviceName + "): " + message);
        } else {
            System.out.println("Client " + clientName + " not found.");
        }
    }

    private static void removeClientRow(String clientName) {
        JPanel clientPanel = clientPanels.remove(clientName);
        if (clientPanel != null) {
            SwingUtilities.invokeLater(() -> {
                mainPanel.remove(clientPanel);
                mainPanel.revalidate();
                mainPanel.repaint();
            });

            deviceLogs.keySet().removeIf(key -> key.startsWith(clientName + ":"));
        }
    }
}
