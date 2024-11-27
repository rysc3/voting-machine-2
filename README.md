# CS 460: Software Engineering
## Project Overview

### Team Assignments
- **Group 6**
- **Group 7**
- **Group 8**
    - Jyrus Cadman
    - David Dominguez
    - Alex Hartel *(Manager)*
    - William Lopez
    - Matthew Macias
- **Group 9**
- **Group 10**

---

## Devices

### Devices Managed by **DeviceManager**
- **Tamper Sensor**
- **SD Card Driver**
- **Card Reader**
- **Latch**
- **Printer**

### Devices Managed by **ScreenManager**
- **Screen**

---

## How to Start

### Server Setup
1. Open the `Server` package.
2. Run `MonitoringServer`.

### Manager Setup
1. Open the `Managers` package.
2. Run `DeviceManager`.
3. Run `ScreenManager`.

---

## Example of Usage

1. Select the `MonitoringServer` terminal.

2. Type a command using the format:  
   `client:DeviceName:Choice:Message`  
   Example:  
   DeviceManager:Printer:1 -using choice 1 for any device will provide the information for it in their respective terminal. No message is needed for certain commands.

3. Check the `DeviceManager` terminal for information presented about the specific device.

4. Type the following command:  DeviceManager:Printer:3:newFilePathForPrinter.txt
- Observe that the monitoring window creates a box for the **Printer** device. Inside the box is text sent from `DeviceManager` to `MonitoringServer`.
- Note that after the choice (3), a message is included which will turn into the file path.

5. Refer to the `DeviceManager` terminal for more detailed information about the printer.

6. Try other commands, such as:  ScreenManager:Screen:1, DeviceManager:Tamper Sensor:1, etc. with the other devices in the second position. 

   to learn about additional device operations.

---
