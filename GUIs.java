import javax.swing.*;

public class GUIs {
    public static void main(String[] args) {
        //all Return default String
        showWelcomeDialog(); //Display Welcome Message

        showRegisterDialog(); //Display login options menu and Return "1" or "2"
                              //1 is log in
                              //2 is sign up

        showSignUpDialog(); //Display and Return user's first and last name

        showIdentityDialog(); //Display role menu and Return "1" or "2"
                              //1 is Teacher
                              //2 is Student

        //sign up
        showCreateUserNameDialog(); //Display and Return user created username
        showUserNameTakenDialog(); //Display error message for username is taken
        showCreateUserPasswordDialog(); //Display and Return user created 6 characters password
        showAccountCreatedDialog(); //Display Account created message
        showAfterSignUpDialog(); //Display role menu and Return "1" or "2"
                              //1 is Log in
                              //2 is Exit

        //log in
        showEmptyUserDialog(); //Display error message if no account in the system
        showInputUserNameDialog(); //Display and Return user input username for login
        showUserNameErrorDialog(); //Display "username is invalid" message
        showInputUserPasswordDialog(); //Display and Return user input password for login
        showUserPasswordErrorDialog(); //Display "password is invalid" message
        showLoginSuccessDialog(); //Display login in successful message

        //After log in
        showAccountDialog(); //Display account menu and Return "1" or "2" or "3" or "4"
                                 //1 is Edit Account
                                 //2 is Delete Account
                                 //3 is Go to the Main Page
                                 //4 is Exit
        showAccountEditDialog(); //Display account edit menu and Return "1" or "2" or "3"
                                 //1 is username
                                 //2 is password
                                 //3 is both
        showNewUserNameDialog(); //Display and Return new username
        showNewUserPasswordDialog(); //Display and Return new password
        showEditSuccessDialog(); //Display edit successful message
        showDeleteAccountDialog(); //Display account deleted message

        //LMS
        showGoToForumDialog(); //Display and Return "1" or "2"
                               //1 is Go to the Discussion Forum
                               //2 is Exit
        showEmptyCourseDialog(); //Display error message if no course in the Forum
        //TODO if courses exist
        showEmptyForumDialog(); //Display error message if no Forum in the Forum
        //TODO if forums exist
        showEmptyPostDialog(); //Display error message if no post in the Forum
        showAskAddPostDialog(); //Display Return "1" or "2" or "3"
                                //1 is user would add post
                                //2 is user would not add post
                                //3 is Exit
        showAddPostDialog(); //Display and Return user added posts
        //TODO if posts exist

        showGoodbyeDialog(); //Display Goodbye Message
    }

    public static void showWelcomeDialog() {
        JOptionPane.showMessageDialog(null, "Welcome to Learning Management System", "Learning Management System", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String showRegisterDialog() {
        String result;
        String[] options = {"Log In", "Sign Up"};
        result = (String) JOptionPane.showInputDialog(null,"Would you like to...", "Learning Management System", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result.equals("Log In")) {
            return "1";
        } else {
            return "2";
        }
    }

    public static String showSignUpDialog() {
        String name;
        String checkLastname;
        int space;
        boolean checkSpace = false;
        boolean checkName = false;
        do {
            name = JOptionPane.showInputDialog(null,"Please enter your first name and last name separated by a space", "User Name", JOptionPane.PLAIN_MESSAGE);
            space = name.indexOf(" ");
            checkLastname = name.substring(space + 1);
            if (name.contains(" ")) {
                checkSpace = true;
            } else {
                JOptionPane.showMessageDialog(null, "There is no space between your first and last name", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (checkLastname.equals("") || space == 0) {
                JOptionPane.showMessageDialog(null, "Invalid Name Format", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkName = true;
            }
        } while (!checkSpace || !checkName);
        return name;
    }

    public static String showIdentityDialog() {
        String result;
        String[] options = {"Teacher", "Student"};
        result = (String) JOptionPane.showInputDialog(null, "Please select your role", "Role", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result.equals("Teacher")) {
            return "1";
        } else {
            return "2";
        }
    }

    public static String showCreateUserNameDialog() {
        String username;
        boolean checkUsername = false;
        do {
            username = (String) JOptionPane.showInputDialog(null, "Please enter a username without comma", "Account", JOptionPane.PLAIN_MESSAGE);
            if(username.equals("")) {
                JOptionPane.showMessageDialog(null, "User name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(username.contains(",")) {
                JOptionPane.showMessageDialog(null, "Username cannot contain any comma!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkUsername = true;
            }
        } while (!checkUsername);
        return username;
    }

    public static void showUserNameTakenDialog() {
        JOptionPane.showMessageDialog(null, "Sorry, this username is already taken :(", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showCreateUserPasswordDialog() {
        String password;
        boolean checkPassword = false;
        do {
            password = (String) JOptionPane.showInputDialog(null, "Please enter a 6 characters password(DO NOT USE ANY COMMAS)", "Account", JOptionPane.PLAIN_MESSAGE);
            if (password.length() != 6 || password.contains(",")) {
                JOptionPane.showMessageDialog(null, "Password should be 6 characters long and without a comma!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkPassword = true;
            }
        } while (!checkPassword);
        return password;
    }

    public static void showAccountCreatedDialog() {
        JOptionPane.showMessageDialog(null, "Your account is successfully created!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String showAfterSignUpDialog() {
        String result;
        String[] options = {"Log in", "Exit"};
        result = (String) JOptionPane.showInputDialog(null, "Would you like to ...", "", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result.equals("Log in")) {
            return "1";
        } else {
            return "2";
        }
    }

    public static void showEmptyUserDialog() {
        JOptionPane.showMessageDialog(null, "You need to Sign Up first", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showInputUserNameDialog() {
        return (String) JOptionPane.showInputDialog(null, "Please enter your user name", "Account", JOptionPane.PLAIN_MESSAGE);
    }

    public static void showUserNameErrorDialog() {
        JOptionPane.showMessageDialog(null, "Invalid username!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showInputUserPasswordDialog() {
        return (String) JOptionPane.showInputDialog(null, "Please enter your password", "Account", JOptionPane.PLAIN_MESSAGE);
    }

    public static void showUserPasswordErrorDialog() {
        JOptionPane.showMessageDialog(null, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showLoginSuccessDialog() {
        JOptionPane.showMessageDialog(null, "You successfully log in!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String showAccountDialog() {
        String[] options = {"Edit account", "Delete account", "Go to the main page", "Exit"};
        String result;
        result = (String) JOptionPane.showInputDialog(null, "What would you like to do", "Account", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if(result.equals("Edit account")) {
            return "1";
        } else if(result.equals("Delete account")) {
            return "2";
        } else if(result.equals("Go to the main page")) {
            return "3";
        } else {
            return "4";
        }
    }

    public static String showAccountEditDialog() {
        String[] options = {"Username", "Password", "Both"};
        String result;
        result = (String) JOptionPane.showInputDialog(null, "What would you like to change?", "Account", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result.equals("Username")) {
            return "1";
        } else if (result.equals("Password")) {
            return "2";
        } else {
            return "3";
        }
    }

    public static String showNewUserNameDialog() {
        String username;
        boolean checkUsername = false;
        do {
            username = (String) JOptionPane.showInputDialog(null, "Please enter a new username without comma", "Account", JOptionPane.PLAIN_MESSAGE);
            if(username.equals("")) {
                JOptionPane.showMessageDialog(null, "User name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(username.contains(",")) {
                JOptionPane.showMessageDialog(null, "Username cannot contain any comma!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkUsername = true;
            }
        } while (!checkUsername);
        return username;
    }

    public static String showNewUserPasswordDialog() {
        String password;
        boolean checkPassword = false;
        do {
            password = (String) JOptionPane.showInputDialog(null, "Please enter a new 6 characters password(DO NOT USE ANY COMMAS)", "Account", JOptionPane.PLAIN_MESSAGE);
            if (password.length() != 6 || password.contains(",")) {
                JOptionPane.showMessageDialog(null, "Password should be 6 characters long and without a comma!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkPassword = true;
            }
        } while (!checkPassword);
        return password;
    }

    public static void showEditSuccessDialog() {
        JOptionPane.showMessageDialog(null, "You've successfully edited your account!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showDeleteAccountDialog() {
        JOptionPane.showMessageDialog(null, "You've successfully deleted your account!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String showGoToForumDialog () {
        String[] options = {"Go to the Discussion Forum", "Exit"};
        String result;
        result = (String) JOptionPane.showInputDialog(null, "Would you like to ...", "Learning Management System", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if(result.equals("Go to the Discussion Forum")) {
            return "1";
        } else {
            return "2";
        }
    }

    public static void showEmptyCourseDialog() {
        JOptionPane.showMessageDialog(null, "Sorry, no courses have been added yet.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showEmptyForumDialog() {
        JOptionPane.showMessageDialog(null, "Sorry, Discussion Forums have been added to this course yet", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showEmptyPostDialog() {
        JOptionPane.showMessageDialog(null, "Sorry, Discussion Forums have been added to this course yet", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String showAskAddPostDialog() {
        String[] options = {"Yes", "No", "Exit"};
        String result;
        result = (String) JOptionPane.showInputDialog(null, "Would you like to add a post to this Discussion Forum?", "Discussion Forums", JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result.equals("Yes")) {
            return "1";
        } else if (result.equals("No")) {
            return "2";
        } else {
            return "3";
        }
    }

    public static String showAddPostDialog() {
        String post;
        boolean checkPost = false;
        do {
            post = (String) JOptionPane.showInputDialog(null, "What would like to post?", "Discussion Forums", JOptionPane.QUESTION_MESSAGE);
            if (post.equals("")) {
                JOptionPane.showMessageDialog(null, "Post cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                checkPost = true;
            }
        } while (!checkPost);
        return post;
    }

    public static void showGoodbyeDialog() {
        JOptionPane.showMessageDialog(null, "Thank you for using the Learning Management System!", "Farewell", JOptionPane.INFORMATION_MESSAGE);
    }
}
