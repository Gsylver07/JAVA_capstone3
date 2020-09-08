import java.sql.*;
import java.util.*;


/**
 * Executes Main menu choice and returns choice to method.
 
 * @param userMenuChoice - int for user input on menu choice.
 */
public class PoisedTaskManager {
    public static void main(String[] args) {

        welcomeScreen();

        //Declare project variables
        String proName, proType, proPhysAdd, proNumber, proERF, date, projDate, proStatus, status, numProjectToFinalize, clientProjectNum, completionDate;
        double totalfee, amountPaid, cost;
        int currentDate;
        String customerName, customerTelNum, customerPhyAdd, customerEmail, proNum;
        String buildName, buildEmail, buildAdress, buildTel;
        String archName, archEmail, archAdress, archTel;
        int answer, choice, choice1, option1, select;

        //menu options
        Scanner userMenuChoice = new Scanner(System.in);
        System.out.println("Please Choose an option below:\n1) Projects \n2) New Contractor \n3) New Architect \n4) Edit \n5) Exit");
        answer = userMenuChoice.nextInt();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/poised_pms?useSSL=false", "otheruser", "swordfish");
            Statement statement = connection.createStatement();
            ResultSet arch;
            ResultSet cli;
            ResultSet contr;
            ResultSet proj;
            int rowsAffected;
            arch = statement.executeQuery("SELECT Name FROM architect");
            cli = statement.executeQuery("SELECT Name FROM client");
            contr = statement.executeQuery("SELECT Name FROM contractor");
            proj = statement.executeQuery("SELECT Project_Number, Project_Name FROM project");
            
            //sub-menu for option 1
            if (answer == 1) {
                System.out.println("Please choose an option below from the submenu:\n1) New Project \n2) Finalize Project \n3) View All Projects \n4) View all Incompleted Projects \n5) View all Overdue Projects \n6) View Any Project");
                select = userMenuChoice.nextInt();

                //new project module for capturing a new project
                if (select == 1) {
                    userMenuChoice.nextLine();
                    System.out.print("Enter the project number: ");
                    proNumber = userMenuChoice.nextLine();
                    System.out.print("Enter the project name:");
                    proName = userMenuChoice.nextLine();
                    System.out.print("Enter in the building type (eg. Apartment, House): ");
                    proType = userMenuChoice.nextLine();
                    System.out.print("Enter in the physical address of the building: ");
                    proPhysAdd = userMenuChoice.nextLine();
                    System.out.print("Enter in the ERF number: ");
                    proERF = userMenuChoice.nextLine();
                    System.out.print("Enter in the initial date (YYYYMMDD): ");
                    projDate = userMenuChoice.nextLine();
                    System.out.print("Enter in the deadline date(YYYYMMDD): ");
                    date = userMenuChoice.nextLine();
                    System.out.print("Enter in the total amount of the project: ");
                    totalfee = userMenuChoice.nextDouble();
                    System.out.print("Enter in the amount paid in to date: ");
                    amountPaid = userMenuChoice.nextDouble();
                    System.out.print("Please enter today's date(YYYYMMDD): ");
                    currentDate = userMenuChoice.nextInt();

                    //calculation for the total cost owing by the client 
                    cost = totalfee - amountPaid;
                    if (cost == 0) {
                        proStatus = "Complete";
                    } else {
                        proStatus = "Incomplete";
                    }

                    //checks if project is overdue or not
                    int d = Integer.parseInt(date);
                    if (currentDate > d) {
                        status = "Overdue";
                    } else {
                        status = "Within due date";
                    }

                    //Insert to the table 'project'
                    rowsAffected = statement.executeUpdate("INSERT INTO projects VALUES('" + proNumber + "','" + proName + "','" + proType + "','" + proPhysAdd + "','" + proERF + "','" + projDate + "','" + date + "','" + totalfee + "','" + amountPaid + "','" + cost + "','" + proStatus + "','" + status + "')");
                    System.out.println("Query complete," + rowsAffected + " row added.");
                    printAllFromTablepro(statement);

                    //captures all the details of the client
                    userMenuChoice.nextLine();
                    System.out.print("Enter the clients name: ");
                    customerName = userMenuChoice.nextLine().toUpperCase();
                    System.out.print("Enter the clients email address: ");
                    customerEmail = userMenuChoice.nextLine();
                    System.out.print("Enter in the physical address of the client: ");
                    customerPhyAdd = userMenuChoice.nextLine();
                    System.out.print("Enter in the clients telephone number: ");
                    customerTelNum = userMenuChoice.nextLine();
                    System.out.print("Enter in the project number for this client: ");
                    proNum = userMenuChoice.nextLine();
                    rowsAffected = statement.executeUpdate("INSERT INTO client VALUES('" + customerName + "','" + customerEmail + "','" + customerPhyAdd + "','" + customerTelNum + "','" + proNum + "')");
                    System.out.println("Query complete," + rowsAffected + " row added.");
                    printAllFromTablecli(statement);
                }

                // module for finalizing the project and creates an invoice
                // also if the balance is zero, displays no invoice needed.
                if (select == 2) {
                    userMenuChoice.nextLine();
                    System.out.println("Do you wish to finalize the project?\n1 - Yes\n2 - No");
                    option1 = userMenuChoice.nextInt();
                    if (option1 == 1) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("Please enter the project number of the project you wish to finalize: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        proj = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Number = '" + numProjectToFinalize + "'");
                        printAllFromTablecli(statement);
                        System.out.println("Please enter the name of the client of the project selected above: ");
                        clientProjectNum = userMenuChoice.nextLine().toUpperCase();
                        while (proj.next()) {
                            System.out.println("Please enter the completion date(YYYYMMDD): ");
                            completionDate = userMenuChoice.nextLine();
                            String PROJECTNUMBER = proj.getString("Project_Number");
                            String PROJECTNAME = proj.getString("Project_Name");
                            Double COST = proj.getDouble("Amount_Outstanding");
                            if (COST > 0) {
                                System.out.println("Please see Invoice below:\nINVOICE\nProject Number: " + PROJECTNUMBER + "\nProject Name: " + PROJECTNAME + "\nAmount Outstanding: " + COST + "\nProject Finalized: Yes\nCompletion Date: " + completionDate);
                                rowsAffected = statement.executeUpdate("UPDATE project SET Project_Status = 'Complete' WHERE Project_Number='" + numProjectToFinalize + "'");
                                cli = statement.executeQuery("SELECT Name, Email_Address, Physical_Address,Telephone_Number FROM client WHERE Name = '" + clientProjectNum + "'");
                                if (cli.next()) {
                                    String NAME = cli.getString("Name");
                                    String EMAIL = cli.getString("Email_Address");
                                    String ADDRESS = cli.getString("Physical_Address");
                                    String TELEPHONE = cli.getString("Telephone_Number");
                                    System.out.println("Name: " + NAME + "\nEmail Address: " + EMAIL + "\nPhysical Address: " + ADDRESS + "\nTelephone Number: " + TELEPHONE);
                                }
                                break;
                            }
                            if (COST == 0) {
                                rowsAffected = statement.executeUpdate("UPDATE project SET Project_Status = 'Complete' WHERE Project_Number='" + numProjectToFinalize + "'");
                                System.out.println("Balance is 0 - No Invoice required!!");
                            }
                        }
                    }

                    if (option1 == 2) {
                        System.out.println("Thank you the project is marked: completed.");
                    }
                }

                //view all projects 
                if (select == 3) {
                    userMenuChoice.nextLine();
                    System.out.println("Do you wish to view all projects?\n1 - Yes\n2 - No");
                    choice = userMenuChoice.nextInt();
                    switch (choice) {
                        case 1:
                            proj = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project");
                            printAllFromTablepro(statement);
                            break;
                        case 2:
                            System.out.println("You Have Exited.");
                            break;
                    }
                }
                
                /**
                 * views all incomplete projects.
                 * @param userMenuChoice - Select option 4 to view all Incomplete Projects .
                 */
                if (select == 4) {
                    userMenuChoice.nextLine();
                    System.out.println("Do you wish to view all incomplete projects?\n1 - Yes\n2 - No");
                    choice = userMenuChoice.nextInt();
                    if (choice == 1) {
                        proj = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Status = 'Incomplete'");
                        while (proj.next()) {
                            String PROJECTNUMBER = proj.getString("Project_Number");
                            String PROJECTNAME = proj.getString("Project_Name");
                            String TYPE = proj.getString("Building_Type");
                            String ADDRESS = proj.getString("Physical_Address");
                            String ERF = proj.getString("ERF_Number");
                            String IDATE = proj.getString("Project_Initial_Date");
                            String DATE = proj.getString("Deadline_Date");
                            Double TOTALFEE = proj.getDouble("Total_Fee");
                            Double ATD = proj.getDouble("Total_Amount_Paid");
                            Double AOUT = proj.getDouble("Amount_Outstanding");
                            String PSTATUS = proj.getString("Project_Status");
                            String DSTATUS = proj.getString("Deadline_Status");
                            System.out.println("Please see all Incomplete Projects Below:\nProject Number: " + PROJECTNUMBER + " Project Name: " + PROJECTNAME + " Building Type: " + TYPE + " Physical Address: " + ADDRESS + " ERF Number: " + ERF + " Project Initial Date: " + IDATE + " Deadline Date: " + DATE + " Total Fee: " + TOTALFEE + " Amount Paid to Date: " + ATD + " Amount Outstanding: " + AOUT + " Project Status: " + PSTATUS + " Deadline Status: " + DSTATUS);
                        }
                    }
                }
              
                /**
                 * views all overdue projects.
                 * @param userMenuChoice - Select option 5 view all Overdue Projects .
                 */
                if (select == 5) {
                    userMenuChoice.nextLine();
                    System.out.println("Do you wish to view all overdue projects?\n1 - Yes\n2 - No");
                    choice = userMenuChoice.nextInt();
                    if (choice == 1) {
                        proj = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Deadline_Status = 'Overdue'");
                        while (proj.next()) {
                            String PROJECTNUMBER = proj.getString("Project_Number");
                            String PROJECTNAME = proj.getString("Project_Name");
                            String TYPE = proj.getString("Building_Type");
                            String ADDRESS = proj.getString("Physical_Address");
                            String ERF = proj.getString("ERF_Number");
                            String IDATE = proj.getString("Project_Initial_Date");
                            String DATE = proj.getString("Deadline_Date");
                            Double TOTALFEE = proj.getDouble("Total_Fee");
                            Double ATD = proj.getDouble("Total_Amount_Paid");
                            Double AOUT = proj.getDouble("Amount_Outstanding");
                            String PSTATUS = proj.getString("Project_Status");
                            String DSTATUS = proj.getString("Deadline_Status");
                            System.out.println("Please see all Overdue Projects Below:\nProject Number: " + PROJECTNUMBER + " Project Name: " + PROJECTNAME + " Building Type: " + TYPE + " Physical Address: " + ADDRESS + " ERF Number: " + ERF + " Project Initial Date: " + IDATE + " Deadline Date: " + DATE + " Total Fee: " + TOTALFEE + " Amount Paid to Date: " + ATD + " Amount Outstanding: " + AOUT + " Project Status: " + PSTATUS + " Deadline Status: " + DSTATUS);
                        }
                    }
                }

            
                /**
                 * view any project.
                 * @param userMenuChoice - Select option 6 to view any Projects .
                 */
                if (select == 6) {
                    userMenuChoice.nextLine();
                    System.out.println("Do you wish to view any project?\n1 - Yes\n2 - No");
                    choice = userMenuChoice.nextInt();
                    if (choice == 1) {
                        userMenuChoice.nextLine();
                        System.out.println("Please enter in the project number you wish to view: ");
                        numProjectToFinalize = userMenuChoice.nextLine();
                        proj = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Number = '" + numProjectToFinalize + "'");
                        while (proj.next()) {
                            String PROJECTNUMBER = proj.getString("Project_Number");
                            String PROJECTNAME = proj.getString("Project_Name");
                            String TYPE = proj.getString("Building_Type");
                            String ADDRESS = proj.getString("Physical_Address");
                            String ERF = proj.getString("ERF_Number");
                            String IDATE = proj.getString("Project_Initial_Date");
                            String DATE = proj.getString("Deadline_Date");
                            Double TOTALFEE = proj.getDouble("Total_Fee");
                            Double ATD = proj.getDouble("Total_Amount_Paid");
                            Double AOUT = proj.getDouble("Amount_Outstanding");
                            String PSTATUS = proj.getString("Project_Status");
                            String DSTATUS = proj.getString("Deadline_Status");
                            System.out.println("Please see Project selected Below:\nProject Number: " + PROJECTNUMBER + " Project Name: " + PROJECTNAME + " Building Type: " + TYPE + " Physical Address: " + ADDRESS + " ERF Number: " + ERF + " Project Initial Date: " + IDATE + " Deadline Date: " + DATE + " Total Fee: " + TOTALFEE + " Amount Paid to Date: " + ATD + " Amount Outstanding: " + AOUT + " Project Status: " + PSTATUS + " Deadline Status: " + DSTATUS);
                        }
                    }
                }
            }

          
            /**
             * captures the contractors details
             * @param userMenuChoice - Select option 2 to capture Contractors from the main menu .
             */
            if (answer == 2) {
                userMenuChoice.nextLine();
                System.out.print("Enter the contractors name: ");
                buildName = userMenuChoice.nextLine();
                System.out.print("Enter the email address of the contractor: ");
                buildEmail = userMenuChoice.nextLine();
                System.out.print("Enter the physical address of the contractor: ");
                buildAdress = userMenuChoice.nextLine();
                System.out.print("Enter the contractors telephone number: ");
                buildTel = userMenuChoice.nextLine();

                rowsAffected = statement.executeUpdate("INSERT INTO contractor VALUES('" + buildName + "','" + buildEmail + "','" + buildAdress + "','" + buildTel + "')");
                System.out.println("Query complete," + rowsAffected + " row added.");
                printAllFromTablecon(statement);
            }

           
            /**
             * captures the architects details
             * @param userMenuChoice - Select option 3 to capture architects from the main menu .
             */
            if (answer == 3) {
                userMenuChoice.nextLine();
                System.out.print("Enter the architects name: ");
                archName = userMenuChoice.nextLine();
                System.out.print("Enter the email address of the architect: ");
                archEmail = userMenuChoice.nextLine();
                System.out.print("Enter the physical address of the architect: ");
                archAdress = userMenuChoice.nextLine();
                System.out.print("Enter the architects telephone number: ");
                archTel = userMenuChoice.nextLine();

                rowsAffected = statement.executeUpdate("INSERT INTO architect VALUES('" + archName + "','" + archEmail + "','" + archAdress + "','" + archTel + "')");
                System.out.println("Query complete," + rowsAffected + " row added.");
                printAllFromTablearc(statement);
            }

            //
            
            /**
             * edit option for updating the project,client,architect and contractors details
             * @param userMenuChoice - Select option 4 to edit  .
             */
            if (answer == 4) {
                userMenuChoice.nextLine();
                System.out.println("Please select what you would like to edit from the submenu below:\n1) Project Details\n2) Client Details\n3) Architect Details\n4) Contractors Details");
                choice = userMenuChoice.nextInt();

                if (choice == 1) {
                    userMenuChoice.nextLine();
                    System.out.println("What would you like to update?\n1) Project number\n2) Project Name\n3) Building Type\n4) Project Physical Address\n5) ERF Number\n6) Project Initial Date\n7) Project Deadline Date\n8) Total Amount Paid");
                    choice1 = userMenuChoice.nextInt();
                    if (choice1 == 1) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the number of the project you wish to update: ");
                        String oldNum = userMenuChoice.nextLine();
                        System.out.print("Please enter the new number of the project: ");
                        String newNum = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET Project_Number= '" + newNum + "' WHERE Project_Number='" + oldNum + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 2) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new name of the project: ");
                        String newName = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET Project_Name= '" + newName + "'WHERE Project_Number='" + numProjectToFinalize + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 3) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new buidling type of the project: ");
                        String newType = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET Building_Type= '" + newType + "' WHERE Project_Number='" + numProjectToFinalize + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 4) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the physical address of the project you wish to update: ");
                        String oldAdd = userMenuChoice.nextLine();
                        System.out.print("Please enter the new physical address of the project: ");
                        String newAdd = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET Physical_Address= '" + newAdd + "' WHERE Physical_Address='" + oldAdd + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 5) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new ERF number of the project: ");
                        String newERF = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET ERF_Number= '" + newERF + "' WHERE Project_Number='" + numProjectToFinalize + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 6) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new initial date of the project: ");
                        String newIDATE = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE project SET Project_Initial_Date= '" + newIDATE + "' WHERE Project_Number='" + numProjectToFinalize + "'");
                        System.out.print("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 7) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new deadline date of the project: ");
                        String newDDATE = userMenuChoice.nextLine();
                        System.out.print("Please enter the current date(YYYYMMDD): ");
                        int currdate = userMenuChoice.nextInt();
                        int dt = Integer.parseInt(newDDATE);
                        if (currdate > dt) {
                            status = "Overdue";
                        } else {
                            status = "Within due date";
                        }
                        rowsAffected = statement.executeUpdate("UPDATE project SET Deadline_Date= '" + newDDATE + "',Deadline_Status='" + status + "' WHERE Project_Number='" + numProjectToFinalize + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                    if (choice1 == 8) {
                        printAllFromTablepro(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number you wish to update: ");
                        numProjectToFinalize = userMenuChoice.nextLine().toUpperCase();
                        System.out.print("Please enter the new amount to be paid of the project: ");
                        Double newATD = userMenuChoice.nextDouble();
                        System.out.print("Please enter the total fee of the selected project: ");
                        Double totfee = userMenuChoice.nextDouble();
                        Double Cost = totfee - newATD;
                        if (Cost == 0) {
                            proStatus = "Complete";
                        } else {
                            proStatus = "Incomplete";
                        }
                        rowsAffected = statement.executeUpdate("UPDATE project SET Amount_Outstanding= '" + Cost + "',Total_Amount_Paid= '" + newATD + "',Project_Status = '" + proStatus + "'WHERE Project_Number='" + numProjectToFinalize + "'");

                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablepro(statement);
                    }
                }
                if (choice == 2) {
                    userMenuChoice.nextLine();
                    System.out.println("What would you like to update?\n1) Client name\n2) Client Email Address\n3) Client Physical Address\n4) Client Telephone Number\n5) Client Project Number");
                    choice1 = userMenuChoice.nextInt();
                    if (choice1 == 1) {
                        printAllFromTablecli(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the name of the client you wish to update: ");
                        String oldName = userMenuChoice.nextLine();
                        System.out.print("Please enter the new name of the client: ");
                        String newName = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE client SET Name= '" + newName + "' WHERE Name='" + oldName + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecli(statement);
                    }
                    if (choice1 == 2) {
                        printAllFromTablecli(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the email address of the client you wish to update: ");
                        String oldEmail = userMenuChoice.nextLine();
                        System.out.print("Please enter the new email address of the client: ");
                        String newEmail = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE client SET Email_Address= '" + newEmail + "' WHERE Email_Address='" + oldEmail + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecli(statement);
                    }
                    if (choice1 == 3) {
                        printAllFromTablecli(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the physical address of the client you wish to update: ");
                        String oldAdd = userMenuChoice.nextLine();
                        System.out.print("Please enter the new physical address of the client: ");
                        String newAdd = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE client SET Physical_Address= '" + newAdd + "' WHERE Physical_Address='" + oldAdd + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecli(statement);
                    }
                    if (choice1 == 4) {
                        printAllFromTablecli(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the telephone number of the client you wish to update: ");
                        String oldTel = userMenuChoice.nextLine();
                        System.out.print("Please enter the new telephone number of the client: ");
                        String newTel = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE client SET Telephone_Number= '" + newTel + "' WHERE Telephone_Number='" + oldTel + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecli(statement);
                    }
                    if (choice1 == 5) {
                        printAllFromTablecli(statement);
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the project number of the client you wish to update: ");
                        String oldPro = userMenuChoice.nextLine();
                        System.out.print("Please enter the new project number of the client: ");
                        String newPro = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE client SET Project_Number= '" + newPro + "' WHERE Project_Number='" + oldPro + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecli(statement);
                    }
                }
                if (choice == 3) {
                    userMenuChoice.nextLine();
                    System.out.println("What would you like to update?\n1) Architect name\n2) Architect Email Address\n3) Architect Physical Address\n4) Architect Telephone Number");
                    choice1 = userMenuChoice.nextInt();
                    if (choice1 == 1) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the name of the architect you wish to update: ");
                        String oldName = userMenuChoice.nextLine();
                        System.out.print("Please enter the new name of the architect: ");
                        String newName = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE architect SET Name= '" + newName + "' WHERE Name='" + oldName + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablearc(statement);
                    }
                    if (choice1 == 2) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the email address of the architect you wish to update: ");
                        String oldEmail = userMenuChoice.nextLine();
                        System.out.print("Please enter the new email address of the architect: ");
                        String newEmail = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE architect SET Email_Address= '" + newEmail + "' WHERE Email_Address='" + oldEmail + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablearc(statement);
                    }
                    if (choice1 == 3) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the physical address of the architect you wish to update: ");
                        String oldAdd = userMenuChoice.nextLine();
                        System.out.print("Please enter the new physical address of the architect: ");
                        String newAdd = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE architect SET Physical_Address= '" + newAdd + "' WHERE Physical_Address='" + oldAdd + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablearc(statement);
                    }
                    if (choice1 == 4) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the telephone number of the architect you wish to update: ");
                        String oldTel = userMenuChoice.nextLine();
                        System.out.print("Please enter the new telephone number of the architect: ");
                        String newTel = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE architect SET Telephone_Number= '" + newTel + "' WHERE Telephone_Number='" + oldTel + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablearc(statement);
                    }
                }

                if (choice == 4) {
                    userMenuChoice.nextLine();
                    System.out.println("What would you like to update?\n1) Contractors name\n2) Contractors Email Address\n3) Contractors Physical Address\n4) Contractors Telephone Number");
                    choice1 = userMenuChoice.nextInt();
                    if (choice1 == 1) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the name of the contractors you wish to update: ");
                        String coName = userMenuChoice.nextLine();
                        System.out.print("Please enter the new name of the contractor: ");
                        String newName = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE contractor SET Name= '" + newName + "' WHERE Name='" + coName + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecon(statement);
                    }
                    if (choice1 == 2) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the email address of the contractors you wish to update: ");
                        String coEmail = userMenuChoice.nextLine();
                        System.out.print("Please enter in the new email address of the contractor: ");
                        String newEmail = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE contractor SET Email_Address= '" + newEmail + "' WHERE Email_Address='" + coEmail + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecon(statement);
                    }
                    if (choice1 == 3) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the physical address of the contractors you wish to update: ");
                        String coAdd = userMenuChoice.nextLine();
                        System.out.print("Please enter the new physical address of the contractor: ");
                        String newAdd = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE contractor SET Physical_Address= '" + newAdd + "' WHERE Physical_Address='" + coAdd + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecon(statement);
                    }
                    if (choice1 == 4) {
                        userMenuChoice.nextLine();
                        System.out.print("\nPlease enter the telephone number of the contractor you wish to update: ");
                        String coTel = userMenuChoice.nextLine();
                        System.out.print("Please enter in the new telephone number of the contractor: ");
                        String newTel = userMenuChoice.nextLine();
                        rowsAffected = statement.executeUpdate("UPDATE contractor SET Telephone_Number= '" + newTel + "' WHERE Telephone_Number='" + coTel + "'");
                        System.out.println("Query complete," + rowsAffected + " row updated.");
                        printAllFromTablecon(statement);
                    }
                }
            }

            //exit option from program

            if (answer == 5) {
                System.out.println("You have exited.\nThank You!!");
            }
            arch.close();
            cli.close();
            contr.close();
            proj.close();
            statement.close();
            connection.close();
            userMenuChoice.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Method printing all values in all rows for each table. */

    public static void printAllFromTablearc(Statement statement) throws
    SQLException {
        ResultSet arc = statement.executeQuery("SELECT Name, Email_Address, Physical_Address, Telephone_Number FROM architect");

        while (arc.next()) {
            System.out.println(
                arc.getString("Name") + ", " +
                arc.getString("Email_Address") + ", " +
                arc.getString("Physical_Address") + ", " +
                arc.getString("Telephone_Number")
            );
        }
    }
    public static void printAllFromTablecli(Statement statement) throws
    SQLException {
        ResultSet cli = statement.executeQuery("SELECT Name, Email_Address, Physical_Address, Telephone_Number, Project_Number FROM client");

        while (cli.next()) {
            System.out.println(
                cli.getString("Name") + ", " +
                cli.getString("Email_Address") + ", " +
                cli.getString("Physical_Address") + ", " +
                cli.getString("Telephone_Number") + ", " +
                cli.getString("Project_Number")
            );
        }
    }
    public static void printAllFromTablecon(Statement statement) throws
    SQLException {
        ResultSet con = statement.executeQuery("SELECT Name, Email_Address, Physical_Address, Telephone_Number FROM contractor");

        while (con.next()) {
            System.out.println(
                con.getString("Name") + ", " +
                con.getString("Email_Address") + ", " +
                con.getString("Physical_Address") + ", " +
                con.getString("Telephone_Number")
            );
        }
    }
    public static void printAllFromTablepro(Statement statement) throws
    SQLException {
        ResultSet pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project");

        while (pro.next()) {
            System.out.println(
                pro.getString("Project_Number") + ", " +
                pro.getString("Project_Name") + ", " +
                pro.getString("Building_Type") + ", " +
                pro.getString("Physical_Address") + ", " +
                pro.getString("ERF_Number") + ", " +
                pro.getString("Project_Initial_Date") + ", " +
                pro.getString("Deadline_Date") + ", " +
                pro.getDouble("Total_Fee") + ", " +
                pro.getDouble("Total_Amount_Paid") + ", " +
                pro.getDouble("Amount_Outstanding") + ", " +
                pro.getString("Project_Status") + ", " +
                pro.getString("Deadline_Status")
            );
        }
    }

    public static void welcomeScreen() {
        // Welcome Screen:
        System.out.println("... POISED TASK MANAGER - SQL ...");
        System.out.println("");
    }
}