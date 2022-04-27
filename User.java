import java.io.*;
import java.util.ArrayList;

public class User {
    private String name;
    private String role; // it will always either be t or s
    private String username;
    private String password;
    private String grade;

    public User(String name, String role, String username, String password, String grade) {

        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }
/*
    //check if the username exists in the ArrayList
    public static boolean usernameExists(String username, ArrayList<User> users) {
        /*try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            User user = (User) ois.readObject();
            if (user == null) {
                return false;
            }
            while (user != null) {
                if (user.getUsername().equals(username)) {
                    return true;
                }
                user = (User) ois.readObject();
            }
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

     /*   for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkUsernameAndPassword(String username, String password, ArrayList<User> users) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            User user = (User) ois.readObject();
            if (user == null) {
                return false;
            }
            while (user != null) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return true;
                }
                user = (User) ois.readObject();
            }
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;


        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                if (users.get(i).getPassword().equals(password))
                return true;
            }
        }
        return false;
    }

    //change username, given old username and new one
    public void changeUsername(String username, String newUsername, ArrayList<User> users) {



        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                index = i;
            }
        }
        users.get(index).setUsername(newUsername);
    }

    //change password given username and new password
    public void changePassword(String username, String newPassword, ArrayList<User> users) {

        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                index = i;
            }
        }
        users.get(index).setPassword(newPassword);
    }


    //delete an account
    public void deleteAccount(String username, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
            }
        }
    }
    */


    public String toString() {
        return String.format("%s,%s,%s,%s,%s", name, role, username, password, grade);
    }

}

//Q. what if the users don't enter anything when asked for their username? Invalid prompt right

