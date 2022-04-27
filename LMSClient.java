import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//asks the teacher to check votes only if there are discussion forums in the curse she is in
// keep displaying the course name once a user enters the course, till one round, change if user enters another course in next round

public class LMSClient {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        JOptionPane.showMessageDialog(null, "Welcome to the Learning Management System!",
                "Learning Management System", JOptionPane.INFORMATION_MESSAGE);
        boolean a = false;
        Socket socket = null;

        do {
            String hostName;
            int portNumber = 0;
            int y = 6;
            do {
                hostName = JOptionPane.showInputDialog(null, "Please enter the Host Name.",
                        "Learning Management System", JOptionPane.QUESTION_MESSAGE);
                y = 6;

                if (hostName == null) { //when the user presses the cross button on top
                    y = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                            "Button Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (y == JOptionPane.YES_OPTION) {
                        displayFarewell();
                        return;
                    }
                    if (y == JOptionPane.CLOSED_OPTION) {
                        return;
                    }

                } else if (hostName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No input received.", "Learning Management System",
                            JOptionPane.ERROR_MESSAGE);
                }

            } while (hostName == null || hostName.isEmpty() || y == JOptionPane.NO_OPTION);
            boolean b = false;
            int z = 8;

            do {
                String s = JOptionPane.showInputDialog(null, "Please enter the Port Number",
                        "Learning Management System", JOptionPane.QUESTION_MESSAGE);
                b = false;
                if (s == null) {
                    z = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                            "Learning Management System", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (z == JOptionPane.YES_OPTION) {
                        displayFarewell();
                        return;
                    } else if (z == JOptionPane.NO_OPTION) {
                        b = true;
                    } else if (z == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                } else if (s != null) {

                    try {
                        portNumber = Integer.parseInt(s);
                        if (portNumber < 0) {
                            JOptionPane.showMessageDialog(null, "Port Number can not be negative!",
                                    "Learning Management System", JOptionPane.ERROR_MESSAGE);
                            b = true;
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Invalid Input!\n" +
                                        "Please enter a number",
                                "Learning Management System", JOptionPane.ERROR_MESSAGE);
                        b = true;
                    }

                }
            } while (b);

            try {
                socket = new Socket(hostName, portNumber);
                if (!socket.isConnected()) {
                    JOptionPane.showMessageDialog(null, "Sorry, couldn't establish a " +
                                    "connection with the Server.\nPlease try again",
                            "Learning Management System", JOptionPane.ERROR_MESSAGE);
                    a = true;

                }
                if (socket.isConnected()) {
                    JOptionPane.showMessageDialog(null, "Hurray, connection established!",
                            "Learning Management System", JOptionPane.INFORMATION_MESSAGE);
                    a = false;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Sorry, couldn't establish a" +
                                " connection with the Server.\nPlease try again",
                        "Learning Management System", JOptionPane.ERROR_MESSAGE);
                a = true;
            }
        } while (a);


        BufferedReader br = null;
        PrintWriter pw = null;
        try {

            pw = new PrintWriter(socket.getOutputStream());
            pw.flush();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("1. Log In\n2. Sign Up");
        String loginSignupInput = scanner.nextLine();
        if (loginSignupInput.equals("2")) {
            pw.write("signup"); //1st send
            pw.println();
            pw.flush();
        } else if (loginSignupInput.equals("1")) {
            pw.write("login"); //1st send
            pw.println();
            pw.flush();
        }

        if (loginSignupInput.equals("2")) {
            String name;
            boolean f = false;
            do {
                System.out.println("Please enter your full name:\n(Warning: You will not be able to " +
                        "change your name later)");
                name = scanner.nextLine();
                f = false;
                if (name == null || name.isEmpty()) {
                    System.out.println("Name can not be empty");
                    f = true;
                } else if (name.indexOf(" ") == -1) {
                    System.out.println("Please enter your firstname and lastname separated by a space.");
                    f = true;
                } else if (name.indexOf(',') != -1) {
                    System.out.println("Please DO NOT use any commas in your name!");
                    f = true;
                }
            } while (f);
            pw.write(name); //send 1a (name)
            pw.println();
            pw.flush();

            boolean c = false;
            int roleInt = 6;
            do {
                System.out.println("Are you a\n1. Teacher\n2. Student ?\n(Warning: You will not be able to " +
                            "change your role later!)");
                String roleString = scanner.nextLine();
                try {
                    roleInt = Integer.parseInt(roleString);
                    c = false;
                    if (roleInt != 2 && roleInt != 1) {
                        System.out.println("Please enter 1 or 2");
                        c = true;
                    }
                } catch (Exception e) {
                    System.out.println("Please enter 1 or 2\n");
                    c = true;
                }
            } while (c);
            String role = "";

            if (roleInt == 1) {
                role = "t";
            } else if (roleInt == 2) {
                role = "s";
            }
            pw.write(role);//send 1b (role)
            pw.println();
            pw.flush();

            String username;
            boolean d = false;
            boolean unExists = false;
            do {
                do {
                    System.out.println("Please enter a username:");

                    username = scanner.nextLine();
                    d = false;

                    if (username == null || username.isEmpty()) {
                        System.out.println("Username can not be blank!");
                        d = true;
                    } else if (username.indexOf(',') != -1) {
                        System.out.println("Please do not use any commas in your username!");
                        d = true;
                    }
                } while (d);
                pw.write(username); //send 1c (Username for checking if it's not already taken)
                pw.println();
                pw.flush();
                String unameExists = "";
                try {
                    unameExists = br.readLine(); //1ci/1cii read
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (unameExists.equals("unexists")) {
                    unExists = true;
                    System.out.println("Oops, this username is already taken! Try again.");
                } else if (unameExists.equals("undexist")) {
                    unExists = false;
                }

            } while (unExists);

            String password;
            do {
                System.out.println("Please enter a 6 characters password(DO NOT USE ANY COMMAS):");
                password = scanner.nextLine();
                if (password.length() != 6 || password.indexOf(',') != -1) {
                    System.out.println("Password should be 6 characters long and without a comma!");
                }
            } while (password.length() != 6 || password.indexOf(',') != -1);

            pw.write(password); // send 1d (password)
            pw.println();
            pw.flush();

            if (br.readLine().equals("usercreated")) { //1e read
                System.out.println("Hurray, account successfully created!");
            }

            String logInExitAns;
            System.out.println("Do you want to:\n1. Log In\n2. Exit");
            logInExitAns = scanner.nextLine();

            if (logInExitAns.equals("2")) {
                pw.write("exit"); //1fi write
                pw.println();
                pw.flush();
                displayFarewell();
                return;
            } else if (logInExitAns.equals("1")) {
                pw.write("login"); //1fii write
                pw.println();
                pw.flush();
                loginSignupInput = "1";
            }

        }

        if (loginSignupInput.equals("1")) {

            String isUsersEmpty = br.readLine();// login read 1i
            if (isUsersEmpty.equals("empty")) {
                System.out.println("You will first need to Sign Up");

                String name;
                boolean f = false;
                do {
                    System.out.println("Please enter your full name:\n(Warning: You will not be able to " +
                            "change your name later)");
                    name = scanner.nextLine();
                    f = false;
                    if (name == null || name.isEmpty()) {
                        System.out.println("Name can not be empty");
                        f = true;
                    } else if (name.indexOf(" ") == -1) {
                        System.out.println("Please enter your firstname and lastname separated by a space.");
                        f = true;
                    } else if (name.indexOf(',') != -1) {
                        System.out.println("Please DO NOT use any commas in your name!");
                        f = true;
                    }
                } while (f);
                pw.write(name); //send 1a (name)
                pw.println();
                pw.flush();

                boolean c = false;
                int roleInt = 6;
                do {
                    System.out.println("Are you a\n1. Teacher\n2. Student ?\n(Warning: You will not be able to " +
                            "change your role later!)");
                    String roleString = scanner.nextLine();
                    try {
                        roleInt = Integer.parseInt(roleString);
                        c = false;
                        if (roleInt != 2 && roleInt != 1) {
                            System.out.println("Please enter 1 or 2");
                            c = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Please enter 1 or 2\n");
                        c = true;
                    }
                } while (c);
                String role = "";

                if (roleInt == 1) {
                    role = "t";
                } else if (roleInt == 2) {
                    role = "s";
                }
                pw.write(role);//send 1b (role)
                pw.println();
                pw.flush();

                String username;
                boolean d = false;
                boolean unExists = false;
                do {
                    do {
                        System.out.println("Please enter a username:");

                        username = scanner.nextLine();
                        d = false;

                        if (username == null || username.isEmpty()) {
                            System.out.println("Username can not be blank!");
                            d = true;
                        } else if (username.indexOf(',') != -1) {
                            System.out.println("Please do not use any commas in your username!");
                            d = true;
                        }
                    } while (d);
                    pw.write(username); //send 1c (Username for checking if it's not already taken)
                    pw.println();
                    pw.flush();
                    String unameExists = "";
                    try {
                        unameExists = br.readLine(); //1ci/1cii read
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (unameExists.equals("unexists")) {
                        unExists = true;
                        System.out.println("Oops, this username is already taken! Try again.");
                    } else if (unameExists.equals("undexist")) {
                        unExists = false;
                    }

                } while (unExists);

                String password;
                do {
                    System.out.println("Please enter a 6 characters password(DO NOT USE ANY COMMAS):");
                    password = scanner.nextLine();
                    if (password.length() != 6 || password.indexOf(',') != -1) {
                        System.out.println("Password should be 6 characters long and without a comma!");
                    }
                } while (password.length() != 6 || password.indexOf(',') != -1);

                pw.write(password); // send 1d (password)
                pw.println();
                pw.flush();

                if (br.readLine().equals("usercreated")) { //1e read
                    System.out.println("Hurray, account successfully created!\nYou can now log in.");
                    isUsersEmpty = "notempty";
                }

            }

            String username;
            String password;
            boolean unExists = true;

            if (isUsersEmpty.equals("notempty")) {
                do {
                    do {
                        System.out.println("Username: ");
                        username = scanner.nextLine();
                        if (username.isEmpty()) {
                            System.out.println("Username can not be empty!");
                        }
                    } while (username.isEmpty());

                    pw.write(username); // 1f login username send
                    pw.println();
                    pw.flush();

                    String unameExists;

                    unameExists = br.readLine(); //1g read
                    if (unameExists.equals("undexist")) {
                        System.out.println("Oops! This username doesn't exist.\nPlease try again!");
                        unExists = false;
                    } else if (unameExists.equals("unexists")) {
                        unExists = true;
                    }
                } while (!unExists);

                boolean isPswdCorrect = true;
                do {
                    do {
                        System.out.println("Password:");
                        password = scanner.nextLine();
                        if (password.length() != 6) {
                            System.out.println("Please enter your 6 characters password!");
                        }
                    } while (password.length() != 6);
                    pw.write(password); //1h write password
                    pw.println();
                    pw.flush();
                    String correctPswd = br.readLine(); //1hi read (correct or incorrect pswd)
                    if (correctPswd.equals("correctpswd")) {
                        isPswdCorrect = true;
                    } else if (correctPswd.equals("incorrectpswd")) {
                        isPswdCorrect = false;
                    }

                } while (!isPswdCorrect);

                String loggedIn = br.readLine(); // 2nd read
                if (loggedIn.equals("loggedin")) {
                    System.out.println("Hurray, you've successfully logged in!");
                }

                String afterLogIn;
                System.out.println("What would you like to do?\n1. Edit your account\n2. Delete your account\n" +
                        "3. Go to the Main Page\n4. Exit");
                afterLogIn = scanner.nextLine();
                if (afterLogIn.equals("1")) {
                    pw.write("editaccount"); //3a write
                    pw.println();
                    pw.flush();
                } else if (afterLogIn.equals("2")) {
                    pw.write("deleteaccount");//3b write
                    pw.println();
                    pw.flush();
                } else if (afterLogIn.equals("3")) {
                    pw.write("gotomainpage"); //3c write
                    pw.println();
                    pw.flush();
                } else if (afterLogIn.equals("4")) {
                    pw.write("exit"); //3d write
                    pw.println();
                    pw.flush();
                }

                if (afterLogIn.equals("4")){
                    displayFarewell();
                    return;
                } else if (afterLogIn.equals("2")) {
                    String accDeleted = br.readLine(); //3e read
                    if (accDeleted.equals("accountdeleted")) {
                        System.out.println("You've successfully deleted your account.");
                        displayFarewell();
                        return;
                    }
                } else if (afterLogIn.equals("1")) {
                    String changeDetailsInput;
                    System.out.println("What would you like to change?\n1. Username\n2. Password\n3. Both");
                    changeDetailsInput = scanner.nextLine();
                    if (changeDetailsInput.equals("1")) {
                        pw.write("changeusername"); // 4a send
                        pw.println();
                        pw.flush();
                    } else if (changeDetailsInput.equals("2")) {
                        pw.write("changepassword"); // 4b send
                        pw.println();
                        pw.flush();
                    } else if (changeDetailsInput.equals("3")) {
                        pw.write("changeboth"); // 4c send
                        pw.println();
                        pw.flush();
                    }
                    if (changeDetailsInput.equals("1")) {
                        boolean b = false;
                        boolean c = false;
                        String newUsername;
                        do {
                            do {
                                System.out.println("Please enter the new username");
                                newUsername = scanner.nextLine();
                                b = false;
                                if (newUsername.isEmpty()) {
                                    System.out.println("Username can not be empty!");
                                    b = true;
                                }
                            } while (b);
                            pw.write(newUsername); // 5th send
                            pw.println();
                            pw.flush();

                            String promptAgain = br.readLine(); // 6 (a,b) read
                            if (promptAgain.equals("unexists")) {
                                System.out.println("Sorry, this username already exists.\nTry another one.");
                                c = true;
                            } else if (promptAgain.equals("undexist")) {
                                c = false;
                            }

                        } while (c);

                    } else if (changeDetailsInput.equals("2")) {
                        String newPassword;
                        do {
                            System.out.println("Please enter a new 6 characters password(DO NOT USE ANY COMMAS):");
                            newPassword = scanner.nextLine();
                            if (newPassword.length() != 6 || newPassword.indexOf(',') != -1) {
                                System.out.println("Password should be 6 characters long and without a comma!");
                            }
                        } while (newPassword.length() != 6 || newPassword.indexOf(',') != -1);
                        pw.write(newPassword); //7th send
                        pw.println();
                        pw.flush();
                    } else if (changeDetailsInput.equals("3")) {
                        boolean b = false;
                        boolean c = false;
                        String newUsername;
                        do {
                            do {
                                System.out.println("Please enter the new username");
                                newUsername = scanner.nextLine();
                                b = false;
                                if (newUsername.isEmpty()) {
                                    System.out.println("Username can not be empty!");
                                    b = true;
                                }
                            } while (b);
                            pw.write(newUsername); // 5th send
                            pw.println();
                            pw.flush();

                            String promptAgain = br.readLine(); // 6 (a,b) read
                            if (promptAgain.equals("unexists")) {
                                System.out.println("Sorry, this username already exists.\nTry another one.");
                                c = true;
                            } else if (promptAgain.equals("undexist")) {
                                c = false;
                            }

                        } while (c);

                        String newPassword;
                        do {
                            System.out.println("Please enter a new 6 characters password(DO NOT USE ANY COMMAS):");
                            newPassword = scanner.nextLine();
                            if (newPassword.length() != 6 || newPassword.indexOf(',') != -1) {
                                System.out.println("Password should be 6 characters long and without a comma!");
                            }
                        } while (newPassword.length() != 6 || newPassword.indexOf(',') != -1);
                        pw.write(newPassword); //7th send
                        pw.println();
                        pw.flush();

                    }
                    String successfullyEdited = br.readLine(); //8th read
                    if (successfullyEdited.equals("editedaccount")) {
                        System.out.println("You've successfully edited your account!");
                    }

                    String goingBack;
                    System.out.println("Would you like to:\n1. Go to the Main Page\n2. Exit");
                    goingBack = scanner.nextLine();
                    if (goingBack.equals("1")) {
                        pw.write("gotomp"); //9a send
                        pw.println();
                        pw.flush();
                        afterLogIn = "3";
                    } else if (goingBack.equals("2")) {
                        pw.write("exit"); //9b send
                        pw.println();
                        pw.flush();
                        displayFarewell();
                        return;
                    }

                }

                if (afterLogIn.equals("3")) {
                    String loopingBackOrExit;
                    do {
                        String roleOfUser = br.readLine(); //10(a,b) read
                        if (roleOfUser.equals("teacher")) {

                            String goToCourseExit = "";
                            String addGoExit = "";
                            String coursesEmptyOrNot = br.readLine(); //11 (a,b) read
                            if (coursesEmptyOrNot.equals("coursesempty")) {

                                String courseName;
                                System.out.println("No courses have been added yet!");
                                do {
                                    System.out.println("Please enter the name of the course you'd like to add\n" +
                                            "(Press 0 to exit)");

                                    courseName = scanner.nextLine();
                                    if (courseName.isEmpty()) {
                                        System.out.println("Course Name can not be empty!");
                                    }
                                } while(courseName.isEmpty());
                                if (courseName.equals("0")) {
                                    pw.write("exit"); //12 a send
                                    pw.println();
                                    pw.flush();
                                    displayFarewell();
                                    return;
                                } else {
                                    pw.write(courseName); //12b send
                                    pw.println();
                                    pw.flush();

                                    String courseAdded = br.readLine();//13 read
                                    if (courseAdded.equals("courseadded")) {
                                        System.out.println("Hurray, course added!");
                                    }
                                    System.out.println("Would you like to:\n1. Go to a course\n2. Exit");
                                    goToCourseExit = scanner.nextLine();
                                    if (goToCourseExit.equals("1")){
                                        pw.write("gotocourse");// 14a send
                                        pw.println();
                                        pw.flush();
                                    } else if (goToCourseExit.equals("2")) {

                                        pw.write("exit");//14b send
                                        pw.println();
                                        pw.flush();
                                    }

                                }
                            } else if (coursesEmptyOrNot.equals("coursesnotempty")) {
                                System.out.println("Would you like to:\n1. Add a course\n2. Go to a course\n3. Exit");
                                addGoExit = scanner.nextLine();
                                if (addGoExit.equals("1")) {
                                    pw.write("addcourse");//15a send
                                    pw.println();
                                    pw.flush();

                                    String courseName;
                                    do {
                                        System.out.println("Please enter the name of the course you'd like to add\n" +
                                                "(Press 0 to exit)");

                                        courseName = scanner.nextLine();
                                        if (courseName.isEmpty()) {
                                            System.out.println("Course Name can not be empty!");
                                        }
                                    } while(courseName.isEmpty());
                                    if (courseName.equals("0")) {
                                        pw.write("exit"); //12 a send repeat
                                        pw.println();
                                        pw.flush();
                                        displayFarewell();
                                        return;
                                    } else {
                                        pw.write(courseName); //12b send repeat
                                        pw.println();
                                        pw.flush();

                                        String courseAdded = br.readLine();//13 read repeat
                                        if (courseAdded.equals("courseadded")) {
                                            System.out.println("Hurray, course added!");
                                        }
                                        System.out.println("Would you like to:\n1. Go to a course\n2. Exit");
                                        goToCourseExit = scanner.nextLine();
                                        if (goToCourseExit.equals("1")){
                                            pw.write("gotocourse");// 14a send repeat
                                            pw.println();
                                            pw.flush();
                                        } else if (goToCourseExit.equals("2")) {

                                            pw.write("exit");//14b send repeat
                                            pw.println();
                                            pw.flush();
                                        }

                                    }

                                } else if (addGoExit.equals("2")) {
                                    pw.write("gotocourse"); //15b send
                                    pw.println();
                                    pw.flush();
                                } else  {   //if (addGoExit.equals("3")) {
                                    pw.write("exit"); //15c send
                                    pw.println();
                                    pw.flush();
                                    displayFarewell();
                                    return;
                                }
                            }
                            if (goToCourseExit.equals("1") || addGoExit.equals("2")) {

                                ArrayList<String> courses = new ArrayList<>();
                                String activeCourse;
                                String size = br.readLine(); //16 read
                                int sizeCoursesArray = Integer.parseInt(size);
                                for (int i = 0; i < sizeCoursesArray; i++) {
                                    String course = br.readLine(); //17 read (loop)
                                    courses.add(course);
                                }
                                System.out.println("0. exit"); //won't require number coz there will be button
                                for (int i = 0; i < courses.size(); i++) {
                                    System.out.printf("%d %s\n", i+1, courses.get(i));
                                }
                                String courseMenu = scanner.nextLine();
                                int courseOption = Integer.parseInt(courseMenu);
                                activeCourse = courses.get(courseOption - 1);
                                pw.write(activeCourse); //18th send (active course)
                                pw.println();
                                pw.flush();

                                String dTopicsEmptyOrNot = br.readLine(); //19 (a,b) read
                                String addDFAns;
                                String goToDFexit = "";
                                String discussionForumMenu = "";
                                if (dTopicsEmptyOrNot.equals("nodtopics")) {

                                    System.out.println("No Discussion Forums have been added to this course yet.");

                                    System.out.println("Would you like to:\n1. Add a Discussion Forum\n2. Exit");
                                    addDFAns = scanner.nextLine();
                                    if (addDFAns.equals("1")) {
                                        pw.write("adddf"); //20a write
                                        pw.println();
                                        pw.flush();
                                    } else if (addDFAns.equals("2")) {
                                        pw.write("exit"); //20b write
                                        pw.println();
                                        pw.flush();
                                        displayFarewell();
                                        return;
                                    }
                                    if (addDFAns.equals("1")) {
                                        System.out.println("[Please note- The format of the Discussion forum is " +
                                                "as follows:\na) " +
                                                "Please write a brief Topic Title in the first line.\n" +
                                                "b) Write a short topic description in the next line.]\n");
                                        String addOptions;
                                        System.out.println("Would you like to write the Discussion Forum Title" +
                                                " and description:\n1. In the terminal\n2. " +
                                                "Import a file for it?\n(Enter 1 or 2)");
                                        addOptions = scanner.nextLine();
                                        String topicTitle = null;
                                        String topicDescription = null;
                                        if (addOptions.equals("1")) {
                                            System.out.println("Please enter the Forum Title and Description " +
                                                    "in the aforementioned format.");
                                            topicTitle = scanner.nextLine();
                                            topicDescription = scanner.nextLine();


                                        } else if (addOptions.equals("2")) {
                                            System.out.println("Please enter the file pathname.");
                                            boolean errorInFileImport = false;
                                            do {
                                                String f = scanner.nextLine();
                                                try {
                                                    File file = new File(f);

                                                    FileReader fr = new FileReader(file);
                                                    BufferedReader brr = new BufferedReader(fr);
                                                    String s = brr.readLine();
                                                    if (s == null || s.isEmpty()) {
                                                        System.out.println("This first line of this file is empty.\n" +
                                                                "Please enter the file path of a non-empty file.");
                                                        errorInFileImport = true;
                                                    } else if (!s.isEmpty() && s != null) {
                                                        topicTitle = s;
                                                        errorInFileImport = false;
                                                        s = brr.readLine();
                                                        if (!s.isEmpty() && s != null) {
                                                            topicDescription = s;
                                                            errorInFileImport = false;
                                                        }
                                                    }

                                                    brr.close();
                                                    fr.close();
                                                } catch (FileNotFoundException fnfe) {
                                                    System.out.println("No file with this name was found.\n" +
                                                            "Please enter the correct file path.");
                                                    errorInFileImport = true;
                                                } catch (IOException ioe) {
                                                    ioe.printStackTrace();
                                                    System.out.println("\nThere was an error reading your file, " +
                                                            "please enter the path name of a valid file again");
                                                    errorInFileImport = true;
                                                }

                                            } while (errorInFileImport);

                                        }
                                        DiscussionTopic newDT = new DiscussionTopic(activeCourse,
                                                topicTitle, topicDescription);
                                        pw.write(newDT.toString()); //21 send
                                        pw.println();
                                        pw.flush();
                                        if (br.readLine().equals("dfadded")) { //22 read
                                            System.out.println("Hurray, discussion forum successfully added!");
                                            dTopicsEmptyOrNot = "yesdtopics";
                                        }

                                    }
                                }
                                if (dTopicsEmptyOrNot.equals("yesdtopics")) {
                                    ArrayList<DiscussionTopic> activeCourseDtopics = new ArrayList<>();
                                    int activeCourseDtopicsSize = Integer.parseInt(br.readLine()); //23 read
                                    for (int i = 0; i < activeCourseDtopicsSize; i++) {
                                        String dtopic = br.readLine(); //24 read (loop)
                                        DiscussionTopic dt = readDiscussionTopicString(dtopic);
                                        activeCourseDtopics.add(dt);
                                    }

                                    for (int i = 0; i < activeCourseDtopics.size(); i++) {
                                        System.out.printf("%d. Topic: %s (%s)\nDescription: %s\n\n", i + 1,
                                                activeCourseDtopics.get(i).getTopicTitle(),
                                                activeCourseDtopics.get(i).getTimestamp(),
                                                activeCourseDtopics.get(i).getTopicDescription());
                                    }
                                    //just display these topics (don't create buttons of them), create buttons of the
                                    // below mentioned options of what they can do
                                    System.out.printf("1. Add a Discussion Forum\n2. " +
                                            "Edit a Discussion Forum\n" +
                                            "3. Delete a Discussion Forum\n4. Go to a Discussion Forum\n5. Exit");
                                    discussionForumMenu = scanner.nextLine();
                                    if (discussionForumMenu.equals("1")) {
                                        pw.write("addforum"); //25 a write
                                        pw.println();
                                        pw.flush();
                                    } else if (discussionForumMenu.equals("2")) {
                                        pw.write("editforum"); //25 b write
                                        pw.println();
                                        pw.flush();
                                    } else if (discussionForumMenu.equals("3")){
                                        pw.write("deleteforum"); //25c write
                                        pw.println();
                                        pw.flush();
                                    } else if (discussionForumMenu.equals("4")){
                                        pw.write("gotoforum"); //25d write
                                        pw.println();
                                        pw.flush();
                                    } else if (discussionForumMenu.equals("5")){
                                        pw.write("exit"); //25e write
                                        pw.println();
                                        pw.flush();
                                    }

                                    if (discussionForumMenu.equals("5")) {
                                        displayFarewell();
                                        return;
                                    } else if (discussionForumMenu.equals("1")) {
                                        System.out.println("[Please note- The format of the Discussion forum is " +
                                                "as follows:\na) " +
                                                "Please write a brief Topic Title in the first line.\n" +
                                                "b) Write a short topic description in the next line.]\n");
                                        String addOptions;
                                        System.out.println("Would you like to write the Discussion Forum Title" +
                                                " and description:\n1. In the input box\n2. " +
                                                "Import a file for it?\n(Enter 1 or 2)");
                                        addOptions = scanner.nextLine();
                                        String topicTitle = null;
                                        String topicDescription = null;
                                        if (addOptions.equals("1")) {
                                            System.out.println("Please enter the Forum Title and Description " +
                                                    "in the aforementioned format.");
                                            topicTitle = scanner.nextLine();
                                            topicDescription = scanner.nextLine();


                                        } else if (addOptions.equals("2")) {
                                            System.out.println("Please enter the file pathname.");
                                            boolean errorInFileImport = false;
                                            do {
                                                String f = scanner.nextLine();
                                                try {
                                                    File file = new File(f);

                                                    FileReader fr = new FileReader(file);
                                                    BufferedReader brr = new BufferedReader(fr);
                                                    String s = brr.readLine();
                                                    if (s == null || s.isEmpty()) {
                                                        System.out.println("This first line of this file is empty.\n" +
                                                                "Please enter the file path of a non-empty file.");
                                                        errorInFileImport = true;
                                                    } else if (!s.isEmpty() && s != null) {
                                                        topicTitle = s;
                                                        errorInFileImport = false;
                                                        s = brr.readLine();
                                                        if (!s.isEmpty() && s != null) {
                                                            topicDescription = s;
                                                            errorInFileImport = false;
                                                        }
                                                    }

                                                    brr.close();
                                                    fr.close();
                                                } catch (FileNotFoundException fnfe) {
                                                    System.out.println("No file with this name was found.\n" +
                                                            "Please enter the correct file path.");
                                                    errorInFileImport = true;
                                                } catch (IOException ioe) {
                                                    ioe.printStackTrace();
                                                    System.out.println("\nThere was an error reading your file, " +
                                                            "please enter the path name of a valid file again");
                                                    errorInFileImport = true;
                                                }

                                            } while (errorInFileImport);

                                        }
                                        DiscussionTopic newDT = new DiscussionTopic(activeCourse,
                                                topicTitle, topicDescription);
                                        pw.write(newDT.toString()); //21 send
                                        pw.println();
                                        pw.flush();
                                        if (br.readLine().equals("dfadded")) { //22 read
                                            System.out.println("Hurray, discussion forum successfully added!");
                                            //dTopicsEmptyOrNot = "yesdtopics";
                                        }
                                        System.out.println("Would you like to:\n1. Go to a Discussion Forum\n2. Exit");
                                        goToDFexit = scanner.nextLine();

                                        if (goToDFexit.equals("1")) {
                                            pw.write("gotodf"); //26a write
                                            pw.println();
                                            pw.flush();
                                        } else if (goToDFexit.equals("2")) {
                                            pw.write("exit"); //26b write
                                            pw.println();
                                            pw.flush();
                                            displayFarewell();
                                            return;
                                        }

                                    } else if (discussionForumMenu.equals("2")) {
                                        ArrayList<DiscussionTopic> activeCourseDtopicss = new ArrayList<>();
                                        int activeCourseDtopicsSizee = Integer.parseInt(br.readLine()); //23 read
                                        for (int i = 0; i < activeCourseDtopicsSizee; i++) {
                                            String dtopic = br.readLine(); //24 read (loop)
                                            DiscussionTopic dt = readDiscussionTopicString(dtopic);
                                            activeCourseDtopicss.add(dt);
                                        }

                                        String editForumNum;
                                        int w = 0;
                                        System.out.println("Click on the discussion forum you'd like to edit");
                                        //create buttons of these
                                        for (int i = 0; i < activeCourseDtopicss.size(); i++) {
                                            System.out.printf("%d. Topic: %s (%s)\nDescription: %s\n\n", i + 1,
                                                    activeCourseDtopicss.get(i).getTopicTitle(),
                                                    activeCourseDtopicss.get(i).getTimestamp(),
                                                    activeCourseDtopicss.get(i).getTopicDescription());
                                        }
                                        editForumNum = scanner.nextLine();
                                        w = Integer.parseInt(editForumNum);
                                        pw.write(activeCourseDtopicss.get(w-1).getTopicTitle()); //27 send (original topictitle)
                                        pw.println();
                                        pw.flush();
                                        pw.write(activeCourseDtopicss.get(w-1).getTopicDescription()); //28 send (original topicdescription)
                                        pw.println();
                                        pw.flush();

                                        String newTopicTitle;
                                        String newTopicDescription;
                                        do {
                                            System.out.println("Please enter the edited Topic Title");
                                            newTopicTitle = scanner.nextLine();
                                            if (newTopicTitle.isEmpty()) {
                                                System.out.println("Topic title can not be empty");
                                            }
                                        } while (newTopicTitle.isEmpty());
                                        do {
                                            System.out.println("Please enter the edited Topic Description");
                                            newTopicDescription = scanner.nextLine();
                                            if (newTopicDescription.isEmpty()) {
                                                System.out.println("Topic description can not be empty");
                                            }
                                        } while (newTopicDescription.isEmpty());

                                        pw.write(newTopicTitle); //29th write (edited topic title)
                                        pw.println();
                                        pw.flush();
                                        pw.write(newTopicDescription); //30th write (edited topic desc)
                                        pw.println();
                                        pw.flush();
                                        if (br.readLine().equals("succesfullyedited")) {
                                            System.out.println("Discussion Forum successfully edited!");
                                        }

                                        System.out.println("Would you like to:\n1. Go to a Discussion Forum\n2. Exit");
                                        goToDFexit = scanner.nextLine();

                                        if (goToDFexit.equals("1")) {
                                            pw.write("gotodf"); //26a write
                                            pw.println();
                                            pw.flush();
                                        } else if (goToDFexit.equals("2")) {
                                            pw.write("exit"); //26b write
                                            pw.println();
                                            pw.flush();
                                            displayFarewell();
                                            return;
                                        }
                                    } else if (discussionForumMenu.equals("3")) {

                                        ArrayList<DiscussionTopic> activeCourseDtopicss = new ArrayList<>();
                                        int activeCourseDtopicsSizee = Integer.parseInt(br.readLine()); //23 read
                                        for (int i = 0; i < activeCourseDtopicsSizee; i++) {
                                            String dtopic = br.readLine(); //24 read (loop)
                                            DiscussionTopic dt = readDiscussionTopicString(dtopic);
                                            activeCourseDtopicss.add(dt);
                                        }

                                        String deleteForumNum;
                                        int w = 0;

                                        System.out.println("Click on the discussion forum you'd like to delete");
                                        //create buttons of these
                                        for (int i = 0; i < activeCourseDtopicss.size(); i++) {
                                            System.out.printf("%d. Topic: %s (%s)\nDescription: %s\n\n", i + 1,
                                                    activeCourseDtopicss.get(i).getTopicTitle(),
                                                    activeCourseDtopicss.get(i).getTimestamp(),
                                                    activeCourseDtopicss.get(i).getTopicDescription());
                                        }
                                        deleteForumNum = scanner.nextLine();
                                        w = Integer.parseInt(deleteForumNum);
                                        pw.write(activeCourseDtopicss.get(w-1).getTopicTitle()); //27 send (original topictitle) repeat
                                        pw.println();
                                        pw.flush();
                                        pw.write(activeCourseDtopicss.get(w-1).getTopicDescription()); //28 send (original topicdescription) repeat
                                        pw.println();
                                        pw.flush();

                                        if (br.readLine().equals("successfullydeleted")) {
                                            System.out.println("Discussion Forum successfully deleted!");
                                        }
                                        System.out.println("Would you like to:\n1. Go to a Discussion Forum\n2. Exit");
                                        goToDFexit = scanner.nextLine();

                                        if (goToDFexit.equals("1")) {
                                            pw.write("gotodf"); //26a write
                                            pw.println();
                                            pw.flush();
                                        } else if (goToDFexit.equals("2")) {
                                            pw.write("exit"); //26b write
                                            pw.println();
                                            pw.flush();
                                            displayFarewell();
                                            return;
                                        }
                                    }
                                    if (goToDFexit.equals("1") || discussionForumMenu.equals("4")) {
                                        String dteon = br.readLine(); //33(a,b) read
                                        if (dteon.equals("nodf")) {
                                            System.out.println("Sorry there are no discussion forums in this course anymore.");
                                            //we want to ask the end question of looping back after this
                                        } else if (dteon.equals("yesdf")) {
                                            int activeCoursedtsize = br.read(); //34 read
                                            activeCourseDtopics = new ArrayList<>();
                                            for (int i = 0; i < activeCoursedtsize; i++) {
                                                String dtopic = br.readLine(); //24 read (loop)
                                                DiscussionTopic dt = readDiscussionTopicString(dtopic);
                                                activeCourseDtopics.add(dt);
                                            }
                                            //create buttons for these
                                            for (int i = 0; i < activeCourseDtopics.size(); i++) {
                                                System.out.printf("%d. Topic: %s (%s)\nDescription: %s\n\n", i + 1,
                                                        activeCourseDtopics.get(i).getTopicTitle(),
                                                        activeCourseDtopics.get(i).getTimestamp(),
                                                        activeCourseDtopics.get(i).getTopicDescription());
                                            }
                                            System.out.println("Click on the discussion forum you'd like to go to");
                                            String dfNumString = scanner.nextLine();
                                            int dfNum = Integer.parseInt(dfNumString);
                                            String topicTitle = activeCourseDtopics.get(dfNum - 1).getTopicTitle();
                                            String topicDescription = activeCourseDtopics.get(dfNum - 1).getTopicDescription();
                                            String topicTimestamp = activeCourseDtopics.get(dfNum - 1).getTimestamp();
                                            pw.write(topicTitle);//35 send
                                            pw.println();
                                            pw.flush();
                                            pw.write(topicDescription); //36 send
                                            pw.println();
                                            pw.flush();

                                            String repliesExistOrNot = br.readLine(); //37 (a,b) read
                                            if (repliesExistOrNot.equals("noreplies")) {
                                                System.out.println("Sorry, there are no replies to this Discussion Forum yet!");
                                                //we want to ask the end question of looping back after this
                                            } else if (repliesExistOrNot.equals("yesreplies")) {
                                                ArrayList<Replies> activeDTReplies = new ArrayList<>();
                                                ArrayList<Comments> activeDTComments = new ArrayList<>();
                                                int activeDTRepliesSize = Integer.parseInt(br.readLine());  //38 read

                                                for (int i = 0; i < activeDTRepliesSize; i++) {
                                                    String replyObjString = br.readLine(); //39 read (loop)
                                                    Replies replyObject = readRepliesString(replyObjString);
                                                    activeDTReplies.add(replyObject);
                                                }
                                                String commentsExistOrNot = br.readLine(); //40 (a,b) read
                                                if (commentsExistOrNot.equals("yescomments")) {
                                                    int commentsALSize = Integer.parseInt(br.readLine()); //41 read
                                                    for (int i = 0; i < commentsALSize; i++) {
                                                        Comments commentObject = readCommentsString(br.readLine()); //42 read (loop)
                                                        activeDTComments.add(commentObject);
                                                    }
                                                }

                                                System.out.printf("Discussion Forum\nTopic: %s (%s)\nDescription: %s\n\n",
                                                        topicTitle, topicTimestamp, topicDescription);
                                                int k = 1;
                                                for (int i = activeDTReplies.size() - 1; i >= 0; i--) {
                                                    String replierUsername = activeDTReplies.get(i).getUsername();
                                                    String replyTimestamp = activeDTReplies.get(i).getTimestamp();
                                                    String reply = activeDTReplies.get(i).getReply();

                                                    ArrayList<Comments> activeReplyComments = new ArrayList<>();
                                                    if (!activeDTComments.isEmpty()) {
                                                        for (int j = 0; j < activeDTComments.size(); j++) {
                                                            if (activeDTComments.get(j).getPost().getUsername().equals(replierUsername)) {
                                                                if (activeDTComments.get(j).getPost().getReply().equals(reply)) {
                                                                    activeReplyComments.add(activeDTComments.get(j));
                                                                }
                                                            }
                                                        }
                                                    }

                                                    System.out.printf("%d. %s (%s)\n%s\n\n", k, replierUsername,
                                                            replyTimestamp, reply);
                                                    k++;
                                                    if (!activeReplyComments.isEmpty()) {
                                                        for (int l = activeReplyComments.size() - 1; l >= 0; l--) {
                                                            String commenterUsername = activeReplyComments.get(l).getUsername();
                                                            String commentTimestamp = activeReplyComments.get(l).getTimestamp();
                                                            String comment = activeReplyComments.get(l).getComment();
                                                            System.out.printf(" %s (%s)\n%s\n\n", commenterUsername,
                                                                    commentTimestamp, comment);
                                                        }
                                                    }
                                                }
                                                System.out.println("1. Comment on a student's post\n2. Move ahead\n3. Exit");
                                                String commentorExit = scanner.nextLine();
                                                if (commentorExit.equals("3")) {
                                                    pw.write("exit"); //43b write
                                                    pw.println();
                                                    pw.flush();
                                                    displayFarewell();
                                                    return;
                                                } else if (commentorExit.equals("2")) {
                                                    pw.write("moveahead"); //43 c write
                                                    pw.println();
                                                    pw.flush();
                                                } else if (commentorExit.equals("1")) {
                                                    pw.write("teacherwantstocomment"); //43a write
                                                    pw.println();
                                                    pw.flush();
                                                    String teacherUsername = br.readLine(); //44 read

                                                    //only create buttons of the replies (not of comments or topic title or topic description)

                                                    System.out.printf("Discussion Forum\nTopic: %s (%s)\nDescription: %s\n\n",
                                                            topicTitle, topicTimestamp, topicDescription);
                                                    int m = 1;
                                                    for (int i = activeDTReplies.size() - 1; i >= 0; i--) {
                                                        String replierUsername = activeDTReplies.get(i).getUsername();
                                                        String replyTimestamp = activeDTReplies.get(i).getTimestamp();
                                                        String reply = activeDTReplies.get(i).getReply();

                                                        ArrayList<Comments> activeReplyComments = new ArrayList<>();
                                                        if (!activeDTComments.isEmpty()) {
                                                            for (int j = 0; j < activeDTComments.size(); j++) {
                                                                if (activeDTComments.get(j).getPost().getUsername().equals(replierUsername)) {
                                                                    if (activeDTComments.get(j).getPost().getReply().equals(reply)) {
                                                                        activeReplyComments.add(activeDTComments.get(j));
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        System.out.printf("%d. %s (%s)\n%s\n\n", m, replierUsername,
                                                                replyTimestamp, reply);
                                                        m++;
                                                        if (!activeReplyComments.isEmpty()) {
                                                            for (int l = activeReplyComments.size() - 1; l >= 0; l--) {
                                                                String commenterUsername = activeReplyComments.get(l).getUsername();
                                                                String commentTimestamp = activeReplyComments.get(l).getTimestamp();
                                                                String comment = activeReplyComments.get(l).getComment();
                                                                System.out.printf(" %s (%s)\n%s\n\n", commenterUsername,
                                                                        commentTimestamp, comment);
                                                            }
                                                        }
                                                    }
                                                    System.out.println("Click on the reply you'd like to comment on."); // this shouldn't be a button
                                                    int postNum = Integer.parseInt(scanner.nextLine());
                                                    String teacherComment;
                                                    do {
                                                        System.out.println("Please enter your comment.");
                                                        teacherComment = scanner.nextLine();
                                                        if (teacherComment.isEmpty() || teacherComment == null) {
                                                            System.out.println("Comment can not be empty.");
                                                        }
                                                    } while (teacherComment.isEmpty() || teacherComment == null);
                                                    Replies studentReply = activeDTReplies.get(activeDTReplies.size() -
                                                            postNum);
                                                    Comments teacherCommentObj = new Comments(studentReply,
                                                            teacherUsername, teacherComment);
                                                    pw.write(teacherCommentObj.toString()); // 45 write
                                                    pw.println();
                                                    pw.flush();

                                                    Replies replyObj = readRepliesString(br.readLine()); //46 read
                                                    String postUsername = replyObj.getUsername();
                                                    String postTimestamp = replyObj.getTimestamp();
                                                    String postContent = replyObj.getReply();

                                                    ArrayList<Comments> editedComments = new ArrayList<>();
                                                    int numOfCommentsTeacherComment = Integer.parseInt(br.readLine()); //47 read
                                                    for (int i = 0; i < numOfCommentsTeacherComment; i++) {
                                                        Comments commentObj = readCommentsString(br.readLine()); //48 read (loop)
                                                        editedComments.add(commentObj);
                                                    }

                                                    System.out.printf("%s (%s)\n%s\n\n", postUsername,
                                                            postTimestamp, postContent);
                                                    if (!editedComments.isEmpty()) {
                                                        for (int l = editedComments.size() - 1; l >= 0; l--) {
                                                            String commenterUsername = editedComments.get(l).
                                                                    getUsername();
                                                            String commentTimestamp = editedComments.get(l).
                                                                    getTimestamp();
                                                            String comment = editedComments.get(l).getComment();
                                                            System.out.printf(" %s (%s)\n%s\n\n", commenterUsername,
                                                                    commentTimestamp, comment);
                                                        }
                                                    }
                                                    System.out.println("Your comment was successfully added!");

                                                }

                                            }

                                        }
                                    }
                                    //votes
                                    String askForVoteChecking = br.readLine(); //49 (a,b) read
                                    if (askForVoteChecking.equals("askforvotechecking")) {
                                        System.out.println("Would you like to view the highest votes for each forum?\n" +
                                                "1. Yes\n2. No\n3. Exit");
                                        String checkVotesAns = scanner.nextLine();
                                        if (checkVotesAns.equals("3")) {
                                            pw.write("exit"); //50a write
                                            pw.println();
                                            pw.flush();
                                            displayFarewell();
                                            return;
                                        } else if (checkVotesAns.equals("2")) {
                                            pw.write("teacherdoesntwanttocheckvotes"); //50b write
                                            pw.println();
                                            pw.flush();
                                        } else if (checkVotesAns.equals("1")) {
                                            pw.write("teacherwantstocheckvotes"); //50c write
                                            pw.println();
                                            pw.flush();

                                            int numOfDf = Integer.parseInt(br.readLine()); //51 read

                                            for (int i = 0; i < numOfDf; i++) {


                                                String topicTitle = br.readLine(); //52 read
                                                String topicTimestamp = br.readLine(); //53 read
                                                String topicDescription = br.readLine(); //54 read

                                                System.out.printf("Discussion Forum Topic: %s (%s)\nDescription: %s\n\n",
                                                        topicTitle, topicTimestamp, topicDescription);

                                                String repliesEmptyOrNot = br.readLine(); //55 (a,b,c) read
                                                if (repliesEmptyOrNot.equals("noreplies")) {
                                                    System.out.println("This Discussion Forum has no replies.");
                                                } else if (repliesEmptyOrNot.equals("yesrepliesbutallzero")) {
                                                    System.out.println("All the replies to this Discussion Forum" +
                                                            " currently have 0 votes.");
                                                } else if (repliesEmptyOrNot.equals("yesrepliesbutnotallzero")) {
                                                    ArrayList<Replies> highestVotedRepliesArrayList = new ArrayList<>();
                                                    ArrayList<String> highestVotedRepliesNameArrayList = new ArrayList<>();

                                                    int loopRounds = Integer.parseInt(br.readLine());// 56 read
                                                    for (int m = 0; m < loopRounds; m++) {
                                                        highestVotedRepliesArrayList.add(readRepliesString(br.readLine())); //58 read (loop)
                                                        highestVotedRepliesNameArrayList.add(br.readLine()); //58 read (loop)
                                                    }

                                                    for (int m = 0; m < highestVotedRepliesArrayList.size(); m++) {
                                                        String highestVotesUsername = highestVotedRepliesArrayList.get(m).getUsername();
                                                        String highestVotesTimestamp = highestVotedRepliesArrayList.get(m).getTimestamp();
                                                        String highestVotes = String.valueOf(highestVotedRepliesArrayList.get(m).
                                                                getVotes());
                                                        String highestVotesPost = highestVotedRepliesArrayList.get(m).getReply();
                                                        String highestVotesName = highestVotedRepliesNameArrayList.get(m);

                                                        System.out.printf("Name: %s Votes: %s\n%s %s\n%s\n\n", highestVotesName,
                                                                highestVotes,
                                                                highestVotesUsername, highestVotesTimestamp, highestVotesPost);
                                                    }


                                                }

                                            }
                                        }
                                    }
                                }

                            }
                            //grading
                            System.out.println("1. Grade student's work\n2. Move ahead\n3. Exit");

                            String gradingAns = scanner.nextLine();
                            if (gradingAns.equals("1")) {
                                pw.write("teacherwantstograde"); //59 a write
                                pw.println();
                                pw.flush();

                                String studentsEmptyOrNot = br.readLine(); //60 (a,b) read
                                if (studentsEmptyOrNot.equals("nostudents")) {
                                    System.out.println("Sorry, no students havesigned up yet");
                                } else if (studentsEmptyOrNot.equals("studentsnotempty")) {

                                    ArrayList<User> students = new ArrayList<>();
                                    int studentSize = Integer.parseInt(br.readLine()); //61 read
                                    for (int i = 0; i < studentSize; i++) {
                                        User student = readUserString(br.readLine()); //62 read (loop)
                                        students.add(student);
                                    }

                                    for (int i = 0; i < students.size(); i++) {
                                        System.out.printf("%d. %s (%s)\n", i + 1, students.get(i).getName(),
                                                students.get(i).getUsername());
                                    }

                                    System.out.println("Which student would you like to grade?");
                                    int studentChoice = Integer.parseInt(scanner.nextLine());

                                    String studentUsername = students.get(studentChoice - 1).getUsername();
                                    pw.write(studentUsername); //63 write
                                    pw.println();
                                    pw.flush();

                                    String postsByStudentOrNot = br.readLine(); //64 (a,b) read
                                    if (postsByStudentOrNot.equals("nopostsbythisstudent")) {
                                        System.out.println("There are no posts from this student yet!");
                                    } else if (postsByStudentOrNot.equals("yespostsbythisstudent")) {

                                        int selectedStudentRepliesSize = Integer.parseInt(br.readLine()); //65 read
                                        ArrayList<Replies> selectedStudentReplies = new ArrayList<>();
                                        ArrayList<Grades> gradesList = new ArrayList<>();

                                        for (int i = 0; i < selectedStudentRepliesSize; i++) {
                                            Replies selectedstudentReply = readRepliesString(br.readLine()); //66 read (loop)
                                            selectedStudentReplies.add(selectedstudentReply);
                                        }
                                        for (int i = 0; i < selectedStudentReplies.size(); i++) {
                                            System.out.printf("%d. Topic: %s\n", i + 1,
                                                    selectedStudentReplies.get(i).getDt().getTopicTitle());
                                            System.out.printf("Reply: (%s)\n%s",
                                                    selectedStudentReplies.get(i).getTimestamp(),
                                                    selectedStudentReplies.get(i).getReply());
                                        }
                                        System.out.printf("\n");
                                        System.out.println("Please assign a point value grade out of 10 to each post\n" +
                                                "(Please write it in decimal format up to one decimal point).\n");
                                        for (int i = 0; i < selectedStudentReplies.size(); i++) {
                                            System.out.printf("What grade will you assign to post %d.?", i + 1);
                                            double grade = 0.0;
                                            boolean why = false;
                                            do {
                                                String t = scanner.nextLine();
                                                try {
                                                    grade = Double.parseDouble(t);
                                                    why = false;
                                                    if (grade < 0.0 || grade > 10.0) {
                                                        System.out.println("Please enter a valid input.");
                                                        why = true;
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Please enter a valid input.");
                                                    why = true;
                                                }
                                            } while (why);

                                            Grades gradesObj = new Grades(selectedStudentReplies.get(i).getDt().
                                                    getCourseName(), selectedStudentReplies.get(i).getDt().getTopicTitle(),
                                                    selectedStudentReplies.get(i).getReply(),
                                                    selectedStudentReplies.get(i).getUsername(), grade);
                                            gradesList.add(gradesObj);
                                            System.out.println("You've successfully assigned a grade to this post!");
                                        }
                                        pw.write(String.valueOf(gradesList.size())); // 67 send
                                        pw.println();
                                        pw.flush();
                                        for (int i = 0; i < gradesList.size(); i++) {
                                            pw.write(gradesList.get(i).toString()); //68 send (loop)
                                            pw.println();
                                            pw.flush();
                                        }

                                    }

                                }

                            } else if (gradingAns.equals("2")) {
                                pw.write("teacherdoesntwanttograde");//59b write
                                pw.println();
                                pw.flush();
                            } else if (gradingAns.equals("3")) {
                                pw.write("exit"); //59c write
                                pw.println();
                                pw.flush();
                                displayFarewell();
                                return;
                            }

                        }// teacher part ends here

                        System.out.println("You've reached the end of the menu!");
                        System.out.println("1. Go back to the main page\n2. Exit");
                        loopingBackOrExit = scanner.nextLine();

                        if (loopingBackOrExit.equals("2")) {
                            pw.write("exit"); //69a send
                            pw.println();
                            pw.flush();
                            displayFarewell();
                            return;
                        } else if (loopingBackOrExit.equals("1")) {
                            pw.write("loopback"); //69b send
                            pw.println();
                            pw.flush();
                        }

                    } while (loopingBackOrExit.equals("1")); //looping back entre menu from after login = 3
                }
            }

        }

    }



    public static void displayFarewell() {
        System.out.println("Thank you for using the Learning Management System!\nBye! Have a nice day!");
    }

    //make sure to check the string isn't empty
    public static User readUserString(String s) {
        int commaIndex;
        User user = null;
        if (s != null && !s.isEmpty()) {
            commaIndex = s.indexOf(',');
            String name = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf(',');
            String role = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf(',');
            String username = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf(',');
            String password = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            String grade = s;

            user = new User(name, role, username, password, grade);
        }
        return user;
    }

    public static DiscussionTopic readDiscussionTopicString(String s) {
        int commaIndex;
        DiscussionTopic dtObject = null;
        if (s != null && !s.isEmpty()) {
            commaIndex = s.indexOf('`');
            String courseName = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String topicTitle = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String topicDescription = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            String timestamp = s;

            dtObject = new DiscussionTopic(courseName,
                    topicTitle, topicDescription, timestamp);
        }
        return dtObject;

    }


    public static Replies readRepliesString (String s) {
        int index;
        Replies replyObject = null;
        if (s != null && !s.isEmpty()) {
            index = s.indexOf('`');
            String courseName = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String topicTitle = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String topicDescription = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String dtTimestamp = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String username = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String replyTimestamp = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String reply = s.substring(0, index);
            s = s.substring(index + 1);
            int votes = Integer.parseInt(s);


            DiscussionTopic dtObject = new DiscussionTopic(courseName,
                    topicTitle, topicDescription, dtTimestamp);
            replyObject = new Replies(dtObject, username,
                    replyTimestamp, reply, votes);
        }
        return replyObject;
    }

    public static Comments readCommentsString (String s) {
        int index;
        Comments commentObject = null;
        if (s != null && !s.isEmpty()) {
            index = s.indexOf('`');
            String courseName = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String topicTitle = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String topicDescription = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String dtTimestamp = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String replierUsername = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String replyTimestamp = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String reply = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String vote = s.substring(0, index);
            int replyVotes = Integer.parseInt(vote);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String commenterUsername = s.substring(0, index);
            s = s.substring(index + 1);
            index = s.indexOf('`');
            String commenterTimestamp = s.substring(0, index);
            s = s.substring(index + 1);
            String comment = s;


            DiscussionTopic dtObject = new DiscussionTopic(courseName,
                    topicTitle, topicDescription, dtTimestamp);
            Replies replyObject = new Replies(dtObject, replierUsername,
                    replyTimestamp, reply, replyVotes);
            commentObject = new Comments(replyObject,
                    commenterUsername, commenterTimestamp, comment);
        }
        return commentObject;
    }

    public static VotersList readVotersListString (String s) {
        int commaIndex;
        VotersList vlObject = null;
        if (s != null && !s.isEmpty()) {
            commaIndex = s.indexOf('`');
            String courseName = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String topicTitle = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String reply = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            String username = s;

            vlObject = new VotersList(courseName, topicTitle,
                    reply, username);
        }
        return vlObject;
    }

    public static Grades readGradesString (String s) {
        int commaIndex;
        Grades gradeObject = null;
        if (s != null && !s.isEmpty()) {
            commaIndex = s.indexOf('`');
            String courseName = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String topicTitle = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String reply = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            commaIndex = s.indexOf('`');
            String username = s.substring(0, commaIndex);
            s = s.substring(commaIndex + 1);
            double grade = Double.valueOf(s);

            gradeObject = new Grades(courseName, topicTitle,
                    reply, username, grade);
        }
        return gradeObject;
    }



}


//for all the read write errors of this class, we need to print stack trace?
//everytime they choose to exit, do we need to close pw and br from client side?