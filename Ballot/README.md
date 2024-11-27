# CS 460 Project 2: Voting Machine

## Team Members
- Jyrus Cadman
- David Dominguez
- Alex Hartel
- William Lopez
- Matthew Lloyd Macias


# Voting Machine

## Project Description
The Voting Machine project is designed to automate and manage the election process, ensuring a secure, efficient, and user-friendly experience for both voters and administrators. It provides functionalities for creating ballots, managing propositions, and collecting votes.

## Features
- **Ballot Management**: Create and manage election ballots with propositions and options.
- **Voting**: Allow users to cast their votes securely and anonymously.
- **Data Management**: Store and manage ballot

### EDITOR
The editor for the ballot is fairly simple to understand when you run it, The first thing you will see is all the fields you need to enter like the name of the file, election name, start date, end date, start time, end time in HH:MM format (use military time). You will also see a button where you can add a proposition. Then once you do press that button you start adding the title for the proposition you are voting, another one for description of it, and finally a place where you choose to add how many options you are voting for which them, then add finally add the options. There is also a button to remove a propostion in case you accidently clicked. The last step you will need to do is press the confirm and it will start generating an XML. You are now done with the editor for the ballot.


## EXTRACTINFOXML 
**OVERVIEW**: THE `EXTRACTINFOXML` FUNCTIONALITY ALLOWS YOU TO UTILIZE AN XML FILE-STYLE STRING AND FEED IT TO THE FUNCTION `EXTRACTINFOXML`. 
- **DATA EXTRACTION**: THE FUNCTION WILL EXTRACT THE FOLLOWING INFORMATION: 
  - **ELECTION INFORMATION**: 
    - `ELECTIONNAME` 
    - `STARTDATE` 
    - `ENDDATE` 
    - `STARTFORDAY` 
    - `ENDFORDAY` 
  - **PROPOSITIONS**: 
    - `PROPOSITION` 
    - `PROPNAME` 
    - `PROPDESC` 
    - `OPTION(S)` 
    - `NUMCHOICES`

### EXAMPLE XML DATA
HERE IS AN EXAMPLE OF AN XML FILE USED TO CREATE A BALLOT:
```XML
<?XML VERSION="1.0" ENCODING="UTF-8" STANDALONE="NO"?>
<VOTING-MACHINE>
    <ELECTIONNAME>PRESIDENTIAL ELECTION</ELECTIONNAME>
    <STARTDATE>2020-08-08</STARTDATE>
    <ENDDATE>2020-08-08</ENDDATE>
    <STARTFORDAY>05:00</STARTFORDAY>
    <ENDFORDAY>17:00</ENDFORDAY>
    <PROPOSITION>
        <PROPNAME>PROPOSITION 1</PROPNAME>
        <PROPDESC>DESCRIPTION OF PROPOSITION</PROPDESC>
        <OPTION>OPTION 1</OPTION>
        <OPTION>OPTION 2</OPTION>
        <OPTION>OPTION 3</OPTION>
        <OPTION>OPTION 3</OPTION>
        <OPTION>OPTION 5</OPTION>
        <NUMCHOICES>2</NUMCHOICES>
    </PROPOSITION>
</Voting-Machine>
