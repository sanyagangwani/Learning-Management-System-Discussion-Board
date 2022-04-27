import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LMSServer implements Runnable{

    private User activeUser;
    private String activeCourse;
    private Socket socket;
    private static Object guard = new Object();
    public LMSServer(Socket socket) {
        this.socket = socket;
        this.activeUser = null;
        this.activeCourse = null;
    }

    private static ArrayList<User> users = readUserDatabase();

    private static ArrayList<String> courses = readCoursesDatabase();

    private static ArrayList<DiscussionTopic> discussionTopics = readDiscussionTopicDatabase();

    private static ArrayList<Replies> replies = readRepliesDatabase();

    private static ArrayList<Comments> comments = readCommentsDatabase();

    private static ArrayList<VotersList> votersList = readVotersListDatabase();

    private static ArrayList<Grades> grades =  readGradesDatabase();

    public static void main(String[] args) {

        try {
            ServerSocket ss = new ServerSocket(4242);
            while (true) {
                Socket socket = ss.accept();
                LMSServer lmsServer = new LMSServer(socket);
                Thread thread = new Thread(lmsServer);
                thread.start();
                //thread.join(); (do we use this or not?) coz if we do, it means one client will run fully and then the other, but we don't want that
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    @Override
    public void run() {
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            pw.flush();

        }catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String logInOrSignUp = br.readLine(); //1st read
            if (logInOrSignUp.equals("signup")) {
                String name = br.readLine(); //1a read (name)
                String role = br.readLine(); //1b read (role)
                boolean unameExists = false;
                String username;
                do {
                    username = br.readLine(); //1c read (username, check if its already taken)

                    if (users.isEmpty()) {
                        pw.write("undexist"); //1ci send
                        pw.println();
                        pw.flush();
                        unameExists = false;
                    } else if (usernameExists(username, users)) {
                        pw.write("unexists"); //1cii send
                        pw.println();
                        unameExists = true;
                    } else if (!usernameExists(username, users)) {
                        pw.write("undexist"); //1ci send
                        pw.println();
                        pw.flush();
                        unameExists = false;
                    }
                } while (unameExists);
                String password = br.readLine(); //1d read (password)
                User user = new User(name, role, username, password, "");
                synchronized (guard) {
                    users.add(user); //synchronize this
                    writeToUserDatabase(users);
                }
                pw.write("usercreated"); //1e write (user successfully created
                pw.println();
                pw.flush();

                String logInOrExit = br.readLine();// 1f read
                if (logInOrExit.equals("exit")) {
                    synchronized (guard) {
                        writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                    }
                    this.activeUser = null;
                    return;
                } else if (logInOrExit.equals("login")) {
                    logInOrSignUp = "login";
                }

            }
            if (logInOrSignUp.equals("login")) {
                if (users.isEmpty()) {
                    pw.write("empty"); //login send 1i
                    pw.println();
                    pw.flush();
                } else if (!users.isEmpty()) {
                    pw.write("notempty"); //login send 1i
                    pw.println();
                    pw.flush();
                }
                if (users.isEmpty()) {
                    String name = br.readLine(); //1a read (name)
                    String role = br.readLine(); //1b read (role)
                    boolean unameExists = false;
                    String username;
                    do {
                        username = br.readLine(); //1c read (username, check if its already taken)

                        if (users.isEmpty()) {
                            pw.write("undexist"); //1ci send
                            pw.println();
                            pw.flush();
                            unameExists = false;
                        } else if (usernameExists(username, users)) {
                            pw.write("unexists"); //1cii send
                            pw.println();
                            pw.flush();
                            unameExists = true;
                        } else if (!usernameExists(username, users)) {
                            pw.write("undexist"); //1ci send
                            pw.println();
                            pw.flush();
                            unameExists = false;
                        }
                    } while (unameExists);
                    String password = br.readLine(); //1d read (password)
                    User user = new User(name, role, username, password, "");
                    synchronized (guard) {
                        users.add(user); //synchronize this

                        writeToUserDatabase(users);
                    }
                    pw.write("usercreated"); //1e write (user successfully created
                    pw.println();
                    pw.flush();
                }

                if (!users.isEmpty()) {
                    String username;
                    String password;
                    boolean unameExists = true;

                    do {
                        username = br.readLine(); // 1f login username read
                        if (!usernameExists(username, users)) {
                            pw.write("undexist"); //1gi send if username doesn't exist
                            pw.println();
                            pw.flush();
                            unameExists = false;
                        } else if (usernameExists(username, users)) {
                            pw.write("unexists"); // 1gii send
                            pw.println();
                            pw.flush();
                            unameExists = true;
                        }
                    } while (!unameExists);

                    boolean correctPswd = true;
                    do {
                        password = br.readLine(); //1h read (password)
                        if (checkUsernameAndPassword(username, password, users)) {
                            pw.write("correctpswd"); //1hi send (correct pswd)
                            pw.println();
                            pw.flush();
                            correctPswd = true;
                        } else if (!checkUsernameAndPassword(username, password, users)) {
                            pw.write("incorrectpswd"); //1hii send (incorrect pswd)
                            pw.println();
                            pw.flush();
                            correctPswd = false;
                        }
                    } while (!correctPswd);
                    int index = 1000;
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getUsername().equals(username)) {
                            index = i;
                        }
                    }
                    this.activeUser = users.get(index);
                    pw.write("loggedin"); // 2nd send
                    pw.println();
                    pw.flush();

                    String afterLogin = br.readLine(); //3 (a,b,c,d) read
                    if (afterLogin.equals("exit")) {
                        synchronized (guard) {
                            writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                        }
                        this.activeUser = null;
                        return;
                    } else if (afterLogin.equals("deleteaccount")) {
                        synchronized (guard) {
                            deleteAccount(activeUser.getUsername(), users);
                            writeToUserDatabase(users);
                        }
                        pw.write("accountdeleted"); //3e send
                        pw.println();
                        pw.flush();
                        synchronized (guard) {
                            writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades);
                        }
                        this.activeUser = null;
                        return;
                    } else if (afterLogin.equals("editaccount")) {
                        String changeDetails = br.readLine(); //4 (a,b,c) read
                        if (changeDetails.equals("changeusername")) {
                            String newUsername;
                            boolean b = false;
                            do {
                                newUsername = br.readLine(); //5th read
                                if (usernameExists(newUsername, users)) {
                                    pw.write("unexists"); //6a write
                                    pw.println();
                                    pw.flush();
                                    b = true;
                                } else if (!usernameExists(newUsername, users)) {
                                    pw.write("undexist"); //6b write
                                    pw.println();
                                    pw.flush();
                                    b = false;
                                }
                            } while (b);

                            synchronized (guard) {
                                changeUsername(this.activeUser.getUsername(), newUsername, users);
                                writeToUserDatabase(users);
                            }
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(newUsername)) {
                                    this.activeUser = users.get(i);
                                }
                            }
                        } else if (changeDetails.equals("changepassword")) {
                            String newPassword = br.readLine(); //7th read
                            synchronized (guard) {
                                changePassword(this.activeUser.getUsername(), newPassword, users);
                                writeToUserDatabase(users);
                            }
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(this.activeUser.getUsername())) {
                                    this.activeUser = users.get(i);
                                }
                            }
                        } else if (changeDetails.equals("changeboth")) {
                            String newUsername;
                            boolean b = false;
                            do {
                                newUsername = br.readLine(); //5th read
                                if (usernameExists(newUsername, users)) {
                                    pw.write("unexists"); //6a write
                                    pw.println();
                                    pw.flush();
                                    b = true;
                                } else if (!usernameExists(newUsername, users)) {
                                    pw.write("undexist"); //6b write
                                    pw.println();
                                    pw.flush();
                                    b = false;
                                }
                            } while (b);

                            synchronized (guard) {
                                changeUsername(this.activeUser.getUsername(), newUsername, users);
                                writeToUserDatabase(users);
                            }
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(newUsername)) {
                                    this.activeUser = users.get(i);
                                }
                            }

                            String newPassword = br.readLine(); //7th read
                            synchronized (guard) {
                                changePassword(this.activeUser.getUsername(), newPassword, users);
                                writeToUserDatabase(users);
                            }
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUsername().equals(this.activeUser.getUsername())) {
                                    this.activeUser = users.get(i);
                                }
                            }
                        }
                        pw.write("editedaccount"); //8th send
                        pw.println();
                        pw.flush();
                        String gotompExit = br.readLine(); //9 (a,b) read
                        if (gotompExit.equals("gotomp")) {
                            afterLogin = "gotomainpage";
                        } else if (gotompExit.equals("exit")) {
                            synchronized (guard) {
                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                            }
                            this.activeUser = null;
                            return;
                        }

                    }

                    if (afterLogin.equals("gotomainpage")) {
                        String loopingBackOrExit;
                        do {
                            if (activeUser.getRole().equals("t")) {
                                pw.write("teacher"); //10a send
                                pw.println();
                                pw.flush();
                            } else if (activeUser.getRole().equals("s")) {
                                pw.write("student"); //10b send
                                pw.println();
                                pw.flush();
                            }

                            if (activeUser.getRole().equals("t")) {

                                String goToCourseExit = "";
                                String addGoExit = "";
                                if (courses.isEmpty()) {
                                    pw.write("coursesempty"); //11a send
                                    pw.println();
                                    pw.flush();
                                } else if (!courses.isEmpty()) {
                                    pw.write("coursesnotempty"); //11b send
                                    pw.println();
                                    pw.flush();
                                }
                                if (courses.isEmpty()) {
                                    String courseName = br.readLine(); //12 (a,b) read
                                    if (courseName.equals("exit")) {
                                        synchronized (guard) {
                                            writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                        }
                                        this.activeUser = null;
                                        return;
                                    } else {

                                        synchronized (guard) {
                                            courses.add(courseName); // sync
                                            writeToCoursesDatabase(courses); //sync
                                        }
                                        pw.write("courseadded"); // 13 send
                                        pw.println();
                                        pw.flush();
                                        goToCourseExit = br.readLine(); //14 read
                                        if (goToCourseExit.equals("exit")) {
                                            synchronized (guard) {
                                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                            }
                                            this.activeUser = null;
                                            return;
                                        }

                                    }
                                } else if (!courses.isEmpty()) {

                                    addGoExit = br.readLine(); //15th read
                                    if (addGoExit.equals("exit")) {
                                        synchronized (guard) {
                                            writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                        }
                                        this.activeUser = null;
                                        return;
                                    } else if (addGoExit.equals("addcourse")) {
                                        String courseName = br.readLine(); //12 (a,b) read repeat
                                        if (courseName.equals("exit")) {
                                            synchronized (guard) {
                                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                            }
                                            this.activeUser = null;
                                            return;
                                        } else {

                                            synchronized (guard) {
                                                courses.add(courseName); // sync
                                                writeToCoursesDatabase(courses); //sync
                                            }
                                            pw.write("courseadded"); // 13 send repeat
                                            pw.println();
                                            pw.flush();
                                            goToCourseExit = br.readLine(); //14 read repeat
                                            if (goToCourseExit.equals("exit")) {
                                                synchronized (guard) {
                                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                }
                                                this.activeUser = null;
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (goToCourseExit.equals("gotocourse") || addGoExit.equals("gotocourse")) {

                                    pw.write(String.valueOf(courses.size())); // 16 send
                                    pw.println();
                                    pw.flush();
                                    for (int i = 0; i < courses.size(); i++) {
                                        pw.write(courses.get(i)); //17 write (loop)
                                        pw.println();
                                        pw.flush();
                                    }
                                    this.activeCourse = br.readLine(); //18th read
                                    int counter = 0;
                                    for (int i = 0; i < discussionTopics.size(); i++) {
                                        if (discussionTopics.get(i).getCourseName().equals(activeCourse)) {
                                            counter++;
                                        }
                                    }
                                    if (counter == 0) {
                                        pw.write("nodtopics"); // 19a send
                                        pw.println();
                                        pw.flush();
                                    } else if (counter > 0) {
                                        pw.write("yesdtopics"); //19b send
                                        pw.println();
                                        pw.flush();
                                    }
                                    String addDFAns;
                                    String goToDFexit = "";
                                    String discussionForumMenu = "";
                                    if (counter == 0) {
                                        addDFAns = br.readLine(); //20 (a,b) read
                                        if (addDFAns.equals("exit")) {
                                            synchronized (guard) {
                                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                            }
                                            this.activeUser = null;
                                            this.activeCourse = null;
                                            return;
                                        } else if (addDFAns.equals("adddf")) {

                                            String newDtString = br.readLine();//21 read
                                            DiscussionTopic newDt = readDiscussionTopicString(newDtString);
                                            synchronized (guard) {
                                                discussionTopics.add(newDt); //synchronise
                                                writeToDiscussionTopicsDatabase(discussionTopics);//synchronise
                                            }
                                            counter++;
                                            pw.write("dfadded"); //22 send
                                            pw.println();
                                            pw.flush();
                                        }

                                    }
                                    if (counter > 0) {

                                        counter = 0;
                                        for (int i = 0; i < discussionTopics.size(); i++) {
                                            if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                counter++;
                                            }
                                        }
                                        pw.write(String.valueOf(counter)); //23 send
                                        pw.println();
                                        pw.flush();
                                        for (int i = 0; i < discussionTopics.size(); i++) {
                                            if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                pw.write(discussionTopics.get(i).toString()); //24 send (loop)
                                                pw.println();
                                                pw.flush();
                                            }
                                        }
                                        discussionForumMenu = br.readLine(); //25 (a,b,c,d,e) read

                                        if (discussionForumMenu.equals("exit")) {
                                            synchronized (guard) {
                                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                            }
                                            this.activeUser = null;
                                            this.activeCourse = null;
                                            return;
                                        } else if (discussionForumMenu.equals("addforum")) {
                                            String newDtString = br.readLine();//21 read
                                            DiscussionTopic newDt = readDiscussionTopicString(newDtString);
                                            synchronized (guard) {
                                                discussionTopics.add(newDt); //synchronise
                                                writeToDiscussionTopicsDatabase(discussionTopics);//synchronise
                                            }
                                            //counter++;
                                            pw.write("dfadded"); //22 send
                                            pw.println();
                                            pw.flush();

                                            goToDFexit = br.readLine(); //26 (a,b) read
                                            if (goToDFexit.equals("exit")) {
                                                synchronized (guard) {
                                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                }
                                                this.activeUser = null;
                                                this.activeCourse = null;
                                                return;
                                            }

                                        } else if (discussionForumMenu.equals("editforum")) {
                                            counter = 0;
                                            for (int i = 0; i < discussionTopics.size(); i++) {
                                                if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                    counter++;
                                                }
                                            }
                                            pw.write(String.valueOf(counter)); //23 send
                                            pw.println();
                                            pw.flush();
                                            for (int i = 0; i < discussionTopics.size(); i++) {
                                                if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                    pw.write(discussionTopics.get(i).toString()); //24 send (loop)
                                                    pw.println();
                                                    pw.flush();
                                                }
                                            }
                                            String originalTopicTitle = br.readLine(); //27th read (original topic title)
                                            String originalTopicDescription = br.readLine(); //28th read (original topic description)
                                            String editedTopicTitle = br.readLine(); //29th read (edited topic title)
                                            String editedTopicDescription = br.readLine(); //30th read (edited topic desc)

                                            synchronized (guard) {
                                                for (int i = 0; i < discussionTopics.size(); i++) {
                                                    if (discussionTopics.get(i).getTopicTitle().equals(originalTopicTitle)) {
                                                        if (discussionTopics.get(i).getTopicDescription().equals(originalTopicDescription)) {
                                                            discussionTopics.get(i).setTopicTitle(editedTopicTitle); //synchronize
                                                            discussionTopics.get(i).setTopicDescription(editedTopicDescription); //synchronize
                                                        }
                                                    }
                                                }
                                                writeToDiscussionTopicsDatabase(discussionTopics); //synchronize
                                            }

                                            synchronized (guard) {
                                                if (!replies.isEmpty()) {
                                                    for (int i = 0; i < replies.size(); i++) {
                                                        if (replies.get(i).getDt().getTopicTitle().equals(originalTopicTitle)) {
                                                            if (replies.get(i).getDt().getTopicDescription().
                                                                    equals(originalTopicDescription)) {
                                                                replies.get(i).getDt().setTopicTitle(editedTopicTitle);
                                                                replies.get(i).getDt().setTopicDescription(editedTopicDescription);
                                                            }
                                                        }
                                                    }
                                                    writeToRepliesDatabase(replies);
                                                }

                                            }

                                            synchronized (guard) {
                                                if (!comments.isEmpty()) {
                                                    for (int i = 0; i < comments.size(); i++) {
                                                        if (comments.get(i).getPost().getDt().getTopicTitle().
                                                                equals(originalTopicTitle)) {
                                                            if (comments.get(i).getPost().getDt().getTopicDescription().
                                                                    equals(originalTopicDescription)) {
                                                                comments.get(i).getPost().getDt().setTopicTitle(editedTopicTitle);
                                                                comments.get(i).getPost().getDt().setTopicDescription
                                                                        (editedTopicDescription);
                                                            }
                                                        }
                                                    }
                                                    writeToCommentsDatabase(comments);
                                                }
                                            }
                                            pw.write("succesfullyedited"); //31 st send
                                            pw.println();
                                            pw.flush();

                                            goToDFexit = br.readLine(); //26 (a,b) read
                                            if (goToDFexit.equals("exit")) {
                                                synchronized (guard) {
                                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                }
                                                this.activeUser = null;
                                                this.activeCourse = null;
                                                return;
                                            }
                                        } else if (discussionForumMenu.equals("deleteforum")) {

                                            counter = 0;
                                            for (int i = 0; i < discussionTopics.size(); i++) {
                                                if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                    counter++;
                                                }
                                            }
                                            pw.write(String.valueOf(counter)); //23 send
                                            pw.println();
                                            pw.flush();
                                            for (int i = 0; i < discussionTopics.size(); i++) {
                                                if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                    pw.write(discussionTopics.get(i).toString()); //24 send (loop)
                                                    pw.println();
                                                    pw.flush();
                                                }
                                            }
                                            String originalTopicTitle = br.readLine(); //27th read (original topic title) repeat
                                            String originalTopicDescription = br.readLine(); //28th read (original topic description) repeat

                                            synchronized (guard) {
                                                for (int i = 0; i < discussionTopics.size(); i++) {
                                                    if (discussionTopics.get(i).getTopicTitle().equals(originalTopicTitle)) {
                                                        if (discussionTopics.get(i).getTopicDescription().
                                                                equals(originalTopicDescription)) {
                                                            discussionTopics.remove(i);
                                                        }
                                                    }
                                                }
                                                writeToDiscussionTopicsDatabase(discussionTopics);
                                            }

                                            synchronized (guard) {
                                                if (!replies.isEmpty()) {
                                                    for (int i = 0; i < replies.size(); i++) {
                                                        if (replies.get(i).getDt().getTopicTitle().equals(originalTopicTitle)) {
                                                            if (replies.get(i).getDt().getTopicDescription().
                                                                    equals(originalTopicDescription)) {
                                                                replies.remove(i);
                                                            }
                                                        }
                                                    }
                                                    writeToRepliesDatabase(replies);
                                                }
                                            }

                                            synchronized (guard) {

                                                if (!comments.isEmpty()) {
                                                    for (int i = 0; i < comments.size(); i++) {
                                                        if (comments.get(i).getPost().getDt().getTopicTitle().
                                                                equals(originalTopicTitle)) {
                                                            if (comments.get(i).getPost().getDt().getTopicDescription().
                                                                    equals(originalTopicDescription)) {
                                                                comments.remove(i);
                                                            }
                                                        }
                                                    }
                                                    writeToCommentsDatabase(comments);
                                                }
                                            }
                                            pw.write("successfullydeleted"); // 32nd send
                                            pw.println();
                                            pw.flush();
                                            goToDFexit = br.readLine(); //26 (a,b) read
                                            if (goToDFexit.equals("exit")) {
                                                synchronized (guard) {
                                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                }
                                                this.activeUser = null;
                                                this.activeCourse = null;
                                                return;
                                            }
                                        }
                                        if (goToDFexit.equals("gotodf") || discussionForumMenu.equals("gotoforum")) {
                                            int c = 0;

                                            for (int i = 0; i < discussionTopics.size(); i++) {
                                                if (discussionTopics.get(i).getCourseName().equals(activeCourse)) {
                                                    c++;
                                                }
                                            }


                                            if (c == 0) {
                                                //we want to ask the end question of looping back after this if this is the case
                                                pw.write("nodf"); //33a send
                                                pw.println();
                                                pw.flush();
                                            } else if (c > 0) {
                                                pw.write("yesdf"); //33b send
                                                pw.println();
                                                pw.flush();
                                                pw.write(String.valueOf(c)); // 34 send
                                                pw.println();
                                                pw.flush();
                                                for (int i = 0; i < discussionTopics.size(); i++) {
                                                    if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                        pw.write(discussionTopics.get(i).toString()); //24 send (loop)
                                                        pw.println();
                                                        pw.flush();
                                                    }
                                                }
                                                String topicTitle = br.readLine(); //35 read
                                                String topicDescription = br.readLine(); //36 read

                                                int numOfReplies = 0;
                                                for (int i = 0; i < replies.size(); i++) {
                                                    if (replies.get(i).getDt().getTopicTitle().equals(topicTitle)) {
                                                        if (replies.get(i).getDt().getTopicDescription().equals(topicDescription)) {
                                                            numOfReplies++;
                                                        }
                                                    }
                                                }

                                                if (numOfReplies == 0) {
                                                    //we want to ask the end question of looping back after this if this the case
                                                    pw.write("noreplies"); // 37a send
                                                    pw.println();
                                                    pw.flush();
                                                } else if (numOfReplies > 0) {
                                                    pw.write("yesreplies"); //37b send
                                                    pw.println();
                                                    pw.flush();

                                                    pw.write(String.valueOf(numOfReplies)); // 38 send
                                                    pw.println();
                                                    pw.flush();

                                                    for (int i = 0; i < replies.size(); i++) {
                                                        if (replies.get(i).getDt().getTopicTitle().equals(topicTitle)) {
                                                            if (replies.get(i).getDt().getTopicDescription().equals(topicDescription)) {
                                                                pw.write(replies.get(i).toString()); //39 send (loop)
                                                                pw.println();
                                                                pw.flush();
                                                            }
                                                        }
                                                    }

                                                    int numOfComments = 0;
                                                    for (int i = 0; i < comments.size(); i++) {
                                                        if (comments.get(i).getPost().getDt().getTopicTitle().equals(topicTitle)) {
                                                            if (comments.get(i).getPost().getDt().getTopicDescription().equals(topicDescription)) {
                                                                numOfComments++;
                                                            }
                                                        }
                                                    }

                                                    if (numOfComments == 0) {
                                                        pw.write("nocomments"); //40a send
                                                        pw.println();
                                                        pw.flush();
                                                    } else if (numOfComments > 0) {
                                                        pw.write("yescomments"); //40b write
                                                        pw.println();
                                                        pw.flush();

                                                        pw.write(String.valueOf(numOfComments)); //41 write
                                                        pw.println();
                                                        pw.flush();
                                                        for (int i = 0; i < comments.size(); i++) {
                                                            if (comments.get(i).getPost().getDt().getTopicTitle().equals(topicTitle)) {
                                                                if (comments.get(i).getPost().getDt().getTopicDescription().equals(topicDescription)) {
                                                                   pw.write(comments.get(i).toString()); //42 send (loop)
                                                                    pw.println();
                                                                   pw.flush();
                                                                }
                                                            }
                                                        }
                                                        String commentorExit = br.readLine(); //43 (a,b) read
                                                        if (commentorExit.equals("exit")) {
                                                            synchronized (guard) {
                                                                writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                            }
                                                            this.activeUser = null;
                                                            this.activeCourse = null;
                                                            return;
                                                        } else if (commentorExit.equals("moveahead")) {

                                                        } else if (commentorExit.equals("teacherwantstocomment")) {
                                                            pw.write(activeUser.getUsername()); //44 write
                                                            pw.println();
                                                            pw.flush();
                                                            Comments teacherCommentObj = readCommentsString(br.readLine()); //45 read
                                                            synchronized (guard) {
                                                                comments.add(teacherCommentObj); //synchronise

                                                                writeToCommentsDatabase(comments);
                                                            }

                                                            for (int i = 0; i < replies.size(); i++) {
                                                                if (replies.get(i).getUsername().equals(teacherCommentObj.getPost().getUsername())) {
                                                                    if (replies.get(i).getReply().equals(teacherCommentObj.getPost().getReply())) {
                                                                        pw.write(replies.get(i).toString()); //46 write
                                                                        pw.println();
                                                                        pw.flush();
                                                                    }
                                                                }
                                                            }

                                                            int numOfCommentsTeacherComment = 0;
                                                            for (int i = 0; i < comments.size(); i++) {
                                                                if (comments.get(i).getPost().getUsername().equals(teacherCommentObj.getPost().getUsername())) {
                                                                    if (comments.get(i).getPost().getReply().equals(teacherCommentObj.getPost().getReply())) {
                                                                        numOfCommentsTeacherComment++;
                                                                    }
                                                                }
                                                            }
                                                            pw.write(String.valueOf(numOfCommentsTeacherComment)); //47 write
                                                            pw.println();
                                                            pw.flush();
                                                            for (int i = 0; i < comments.size(); i++) {
                                                                if (comments.get(i).getPost().getUsername().equals(teacherCommentObj.getPost().getUsername())) {
                                                                    if (comments.get(i).getPost().getReply().equals(teacherCommentObj.getPost().getReply())) {
                                                                        pw.write(comments.get(i).toString()); //48 write (loop)
                                                                        pw.println();
                                                                        pw.flush();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }
                                        //ask if teacher wants to check votes
                                        int numOfDf = 0;
                                        for (int i = 0; i < discussionTopics.size(); i++) {
                                            if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                numOfDf++;
                                            }
                                        }
                                        if (numOfDf == 0) {
                                            pw.write("dontaskforvotechecking"); // 49a write
                                            pw.println();
                                            pw.flush();
                                        } else if (numOfDf > 0) {
                                            pw.write("askforvotechecking"); //49b write
                                            pw.println();
                                            pw.flush();

                                            String checkVotesAns = br.readLine(); // 50 (a,b,c) read
                                            if (checkVotesAns.equals("exit")) {
                                                synchronized (guard){
                                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                                }
                                                this.activeUser = null;
                                                this.activeCourse = null;
                                                return;
                                            } else if (checkVotesAns.equals("teacherdoesntwanttocheckvotes")) {

                                            } else if (checkVotesAns.equals("teacherwantstocheckvotes")) {

                                                ArrayList<DiscussionTopic> activeCourseDtopics = new ArrayList<>();
                                                for (int i = 0; i < discussionTopics.size(); i++) {
                                                    if (discussionTopics.get(i).getCourseName().equals(this.activeCourse)) {
                                                        activeCourseDtopics.add(discussionTopics.get(i));
                                                    }
                                                }
                                                pw.write(String.valueOf(activeCourseDtopics.size())); //51 write
                                                pw.println();
                                                pw.flush();

                                                for (int i = 0; i < activeCourseDtopics.size(); i++) {
                                                    pw.write(activeCourseDtopics.get(i).getTopicTitle()); //52 write
                                                    pw.println();
                                                    pw.flush();
                                                    pw.write(activeCourseDtopics.get(i).getTimestamp()); //53 write
                                                    pw.println();
                                                    pw.flush();
                                                    pw.write(activeCourseDtopics.get(i).getTopicDescription()); //54 write
                                                    pw.println();
                                                    pw.flush();

                                                    ArrayList<Replies> currentDTReplies = new ArrayList<>();
                                                    for (int j = 0; j < replies.size(); j++) {
                                                        if (replies.get(j).getDt().getTopicTitle().equals(activeCourseDtopics.get(i).getTopicTitle())) {
                                                            if (replies.get(j).getDt().getTopicDescription().equals(activeCourseDtopics.get(i).getTopicDescription())) {
                                                                currentDTReplies.add(replies.get(j));
                                                            }
                                                        }
                                                    }
                                                    ArrayList<Replies> highestVotedRepliesArrayList = new ArrayList<>();
                                                    ArrayList<String> highestVotedRepliesNameArrayList = new ArrayList<>();
                                                    if (currentDTReplies.isEmpty()) {
                                                        pw.write("noreplies"); //55a write
                                                        pw.println();
                                                        pw.flush();
                                                    } else if (!currentDTReplies.isEmpty() && checkVotesAllZero(currentDTReplies)) {
                                                        pw.write("yesrepliesbutallzero"); //55b write
                                                        pw.println();
                                                        pw.flush();
                                                    } else if (!currentDTReplies.isEmpty() && !checkVotesAllZero(currentDTReplies)) {
                                                        pw.write("yesrepliesbutnotallzero"); //55c write
                                                        pw.println();
                                                        pw.flush();

                                                        highestVotedRepliesArrayList = highestVotes(currentDTReplies);
                                                        for (int k = 0; k < highestVotedRepliesArrayList.size(); i++) {
                                                            for (int l = 0; l < users.size(); l++) {
                                                                if (highestVotedRepliesArrayList.get(k).getUsername().equals(users.get(l).getUsername())) {
                                                                    highestVotedRepliesNameArrayList.add(users.get(l).getName());
                                                                }
                                                            }
                                                        }
                                                        pw.write(String.valueOf(highestVotedRepliesArrayList.size())); // 56 send
                                                        pw.println();
                                                        pw.flush();

                                                        for (int m = 0; m < highestVotedRepliesNameArrayList.size(); m++) {
                                                            pw.write(highestVotedRepliesArrayList.get(m).toString()); //57 send (loop)
                                                            pw.println();
                                                            pw.flush();

                                                            pw.write(highestVotedRepliesNameArrayList.get(i)); //58 send (loop)
                                                            pw.println();
                                                            pw.flush();
                                                        }

                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                //grading
                                String gradingAns = br.readLine(); //59 (a,b,c) read
                                if (gradingAns.equals("teacherwantstograde")) {

                                    ArrayList<User> students = new ArrayList<>();
                                    for (int i = 0; i < users.size(); i++) {
                                        if (users.get(i).getRole().equals("s")) {
                                            students.add(users.get(i));
                                        }
                                    }
                                    if (students.isEmpty()) {
                                        pw.write("nostudents"); //60a write
                                        pw.println();
                                        pw.flush();
                                    } else if (!students.isEmpty()) {
                                        pw.write("studentsnotempty"); //60b write
                                        pw.println();
                                        pw.flush();

                                        pw.write(String.valueOf(students.size())); //61 send
                                        pw.println();
                                        pw.flush();

                                        for (int i = 0; i < students.size(); i++) {
                                            pw.write(students.get(i).toString()); //62 send (loop)
                                            pw.println();
                                            pw.flush();
                                        }

                                        String selectedStudentUsername = br.readLine(); //63 read
                                        ArrayList<Replies> selectedStudentReplies = new ArrayList<>();
                                        for (int i = 0; i < replies.size(); i++) {
                                            if (replies.get(i).getUsername().equals(selectedStudentUsername)) {
                                                selectedStudentReplies.add(replies.get(i));
                                            }
                                        }
                                        if (selectedStudentReplies.isEmpty()) {
                                            pw.write("nopostsbythisstudent"); //64a  send
                                            pw.println();
                                            pw.flush();
                                        } else if (!selectedStudentReplies.isEmpty()) {
                                            pw.write("yespostsbythisstudent"); //64b send
                                            pw.println();
                                            pw.flush();

                                            pw.write(String.valueOf(selectedStudentReplies.size())); //65 write
                                            pw.println();
                                            pw.flush();

                                            for (int i = 0; i < selectedStudentReplies.size(); i++) {
                                                pw.write(selectedStudentReplies.get(i).toString()); //66 send (loop)
                                                pw.println();
                                                pw.flush();
                                            }
                                            int loopRounds = Integer.parseInt(br.readLine()); // 67 read
                                            for (int i = 0; i < loopRounds; i++) {
                                                Grades gradeObj = readGradesString(br.readLine()); //68 read
                                                synchronized (guard) {
                                                    grades.add(gradeObj); //synchronise
                                                }
                                            }
                                            synchronized (guard) {
                                                writeToGradesDatabase(grades); //synchronise
                                            }

                                        }

                                    }

                                } else if (gradingAns.equals("exit")) {
                                    synchronized (guard) {
                                        writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                    }
                                    this.activeUser = null;
                                    this.activeCourse = null;
                                    return;
                                }

                            }
                            //teacher part ends here


                            loopingBackOrExit = br.readLine(); //69 (a,b) read
                            if (loopingBackOrExit.equals("exit")) {
                                synchronized (guard) {
                                    writeToAllFiles(users, courses, discussionTopics, replies, comments, votersList, grades); //synchronise
                                }
                                this.activeUser = null;
                                this.activeCourse = null;
                                return;
                            }

                        } while (loopingBackOrExit.equals("loopback")); //looping back entre menu from after login = go to main page
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        };


    }

    public static void writeToAllFiles(ArrayList<User> users, ArrayList<String> courses,
                                       ArrayList<DiscussionTopic> discussionTopics,
                                       ArrayList<Replies> replies, ArrayList<Comments> comments,
                                       ArrayList<VotersList> votersList,
                                       ArrayList<Grades> grades) {
        if (!users.isEmpty()) {
            writeToUserDatabase(users);
        }
        if (!courses.isEmpty()) {
            writeToCoursesDatabase(courses);
        }
        if (!discussionTopics.isEmpty()) {
            writeToDiscussionTopicsDatabase(discussionTopics);
        }
        if (!replies.isEmpty()) {
            writeToRepliesDatabase(replies);
        }
        if (!comments.isEmpty()) {
            writeToCommentsDatabase(comments);
        }
        if (!votersList.isEmpty()) {
            writeToVotersListDatabase(votersList);
        }
        if (!grades.isEmpty()) {
            writeToGradesDatabase(grades);
        }
    }

    public static boolean checkVotesAllZero(ArrayList<Replies> replies) {
        int counter = 0;
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getVotes() == 0) {
                counter++;
            }
        }
        if (counter == replies.size())
            return true;
        else
            return false;

    }

    public static ArrayList<Replies> highestVotes(ArrayList<Replies> replies) {
        int highestVotes = replies.get(0).getVotes();
        ArrayList<Replies> highestVoteCountArrayList = new ArrayList<>();
        for (int m = 1; m < replies.size(); m++) {
            if (replies.get(m).getVotes() > highestVotes) {
                highestVotes = replies.get(m).getVotes();
            }
        }
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getVotes() == highestVotes) {
                highestVoteCountArrayList.add(replies.get(i));
            }
        }
        return highestVoteCountArrayList;
    }

    public static void writeToUserDatabase(ArrayList<User> users) {
        try {
            File f = new File("UserDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < users.size(); i++) {
                pw.println(users.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToCoursesDatabase(ArrayList<String> courses) {
        try {
            File f = new File("CoursesDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < courses.size(); i++) {
                pw.println(courses.get(i));
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToDiscussionTopicsDatabase(ArrayList<DiscussionTopic> dt) {
        try {
            File f = new File("DiscussionTopicsDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < dt.size(); i++) {
                pw.println(dt.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToRepliesDatabase(ArrayList<Replies> replies) {
        try {
            File f = new File("RepliesDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < replies.size(); i++) {
                pw.println(replies.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToCommentsDatabase(ArrayList<Comments> comments) {
        try {
            File f = new File("CommentsDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < comments.size(); i++) {
                pw.println(comments.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToVotersListDatabase(ArrayList<VotersList> votersList) {
        try {
            File f = new File("VotersListDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < votersList.size(); i++) {
                pw.println(votersList.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToGradesDatabase(ArrayList<Grades> grades) {
        try {
            File f = new File("GradesDatabase.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos, true);
            for (int i = 0; i < grades.size(); i++) {
                pw.println(grades.get(i).toString());
            }
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static ArrayList<User> readUserDatabase() {
        ArrayList<User> users = new ArrayList<>();

        try {
            File f = new File("UserDatabase.txt");
            if (f.exists()) {
            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            int commaIndex;

            while (s != null && !s.isEmpty()) {
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

                User user = new User(name, role, username, password, grade);
                users.add(user);

                s = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }


    public static ArrayList<String> readCoursesDatabase() {

        ArrayList<String> courses = new ArrayList<>();
        try {
            File f = new File("CoursesDatabase.txt");
            if (f.exists()) {
            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String s = br.readLine();

            while (s != null && !s.isEmpty()) {
                courses.add(s);
                s = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return courses;
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



    public static ArrayList<DiscussionTopic> readDiscussionTopicDatabase() {

        ArrayList<DiscussionTopic> dt = new ArrayList<>();
        try {
            File f = new File("DiscussionTopicsDatabase.txt");
            if (f.exists()) {

            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String s = br.readLine();
            int commaIndex;

            while (s != null && !s.isEmpty()) {
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

                DiscussionTopic dtObject = new DiscussionTopic(courseName,
                        topicTitle, topicDescription, timestamp);

                dt.add(dtObject);

                s = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dt;
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

    public static ArrayList<Replies> readRepliesDatabase() {

        ArrayList<Replies> replies = new ArrayList<>();
        try {
            File f = new File("RepliesDatabase.txt");
            if (f.exists()) {

            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            int index;

            while (s != null && !s.isEmpty()) {
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
                Replies replyObject = new Replies(dtObject, username,
                        replyTimestamp, reply, votes);

                replies.add(replyObject);

                s = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return replies;
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




    public static ArrayList<Comments> readCommentsDatabase() {

        ArrayList<Comments> comments = new ArrayList<>();
        try {
            File f = new File("CommentsDatabase.txt");
            if (f.exists()) {

            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String s = br.readLine();
            int index;

            while (s != null && !s.isEmpty()) {
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
                Comments commentObject = new Comments(replyObject,
                        commenterUsername, commenterTimestamp, comment);

                comments.add(commentObject);

                s = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
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

    public static ArrayList<VotersList> readVotersListDatabase() {

        ArrayList<VotersList> vl = new ArrayList<>();
        try {
            File f = new File("VotersListDatabase.txt");
            if (f.exists()) {

            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            int commaIndex;

            while (s != null && !s.isEmpty()) {
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

                VotersList vlObject = new VotersList(courseName, topicTitle,
                        reply, username);

                vl.add(vlObject);

                s = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vl;
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

    public static ArrayList<Grades> readGradesDatabase() {
        ArrayList<Grades> grades = new ArrayList<>();
        try {
            File f = new File("GradesDatabase.txt");
            if (f.exists()) {

            } else {
                f.createNewFile();
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            int commaIndex;

            while (s != null && !s.isEmpty()) {
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

                Grades gradeObject = new Grades(courseName, topicTitle,
                        reply, username, grade);

                grades.add(gradeObject);

                s = br.readLine();
            }
            br.close();
            fr.close();
            return grades;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public static boolean usernameExists(String username, ArrayList<User> users) {

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkUsernameAndPassword(String username, String password, ArrayList<User> users) {

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                if (users.get(i).getPassword().equals(password))
                    return true;
            }
        }
        return false;
    }

    public static void deleteAccount(String username, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
            }
        }
    }

    public static void changeUsername(String username, String newUsername, ArrayList<User> users) {

        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                index = i;
            }
        }
        users.get(index).setUsername(newUsername);
    }

    public static void changePassword(String username, String newPassword, ArrayList<User> users) {

        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                index = i;
            }
        }
        users.get(index).setPassword(newPassword);
    }

}

//I am using isConnect to find out if the client has been connected to the server, but isConnect
// will return true even if the client had once been connected once in the past
// in the server class, in addition to static variables of the class, do we also synchronise the local variables?
// we don't need to synchronise those parts where we are "accessing" the static variables, only those parts where we are changing it right? ***
//do we use different objects to sync diff arraylists or the same one?
//what does using diff objects to sync mean?
// vvvv imp: object why no changes?
//check those 2 places (with voting and grading, dtopics are empty)
//check if you're writing to database after every edit
//make sure to account for cross on top of panel for GUIs
//students: voting, round variable (to check the number of current round)


//active dtreplies will be made everytime
//set this.activeCourse= null everywhere you're exiting
