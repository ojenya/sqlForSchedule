import com.google.gson.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import static spark.Spark.*;


public class Main {

    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        ProcessBuilder process = new ProcessBuilder();
        if (process.environment().get("PORT") != null) {
            port(Integer.parseInt(process.environment().get("PORT")));
        } else {
            port(8080);
        }


        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        Gson gson = new GsonBuilder().create();

        Statement stmt2 = conn.createStatement();
        stmt2.execute("CREATE TABLE if not exists `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `roleId` integer, `name` text," +
                " `surname` text, `fathername` text, `login` text,  `password` text, `errorCode` integer);");

        Statement stmt3 = conn.createStatement();
        stmt3.execute("CREATE TABLE if not exists `studentsClass` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `shiftId` integer, `code` text," +
                " `course` integer, `errorCode` integer);");
        Statement stmt4 = conn.createStatement();
        stmt4.execute("CREATE TABLE if not exists `subject` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `groupNumber` integer, `name` text," +
                " `shortName` text, `errorCode` integer);");

        Statement stmt5 = conn.createStatement();
        stmt5.execute("CREATE TABLE if not exists `scheduleOwner` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `numberOfTheSchemaId` integer , `userOwnerId` text , `main` bit);");


        Statement stmt6 = conn.createStatement();
     stmt6.execute("CREATE TABLE if not exists `workload` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `studentClassId` integer," +
                " `subjectId` integer , `userId` integer);");

        Statement stmtNew = conn.createStatement();
        stmtNew.execute("CREATE TABLE if not exists `cabinet` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` text, `errorCode` integer);");

        //Statement stmtDay = conn.createStatement();
        stmtNew.execute("CREATE TABLE if not exists `day` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` text, `errorCode` integer);");

        //Statement stmtSchedule = conn.createStatement();
        stmtNew.execute("CREATE TABLE if not exists `schedule` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `subjectId` integer" +
                ",`studentClassId` integer, `cabinetId` integer, `dayId` integer, `numberOfTheSchema` integer, `numberOfTheLesson` integer, " +
                "`errorCode` integer);");

      //  stmtNew.execute("ALTER TABLE `scheduleOwner` ADD FOREIGN KEY (numberOfTheSchemaId) REFERENCES `schedule`(numberOfTheSchemaId);");


        get("/users", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `users`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()){
                users.add(new User(
                        rs.getInt("id"),
                        rs.getInt("roleId"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("fathername"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getInt("errorCode")));
            }
            return users;
        }, gson::toJson);

        get("/studentsClass", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `studentsClass`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<StudentsClass> studentsClasses = new ArrayList<>();
            while (rs.next()){
                studentsClasses.add(new StudentsClass(
                        rs.getInt("id"),
                        rs.getInt("shiftId"),
                        rs.getString("code"),
                        rs.getInt("course"),
                        rs.getInt("errorCode")));
            }
            return studentsClasses;
        }, gson::toJson);

        get("/users/:id", (request,response) -> {
            response.type("application/json");
            int userID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `users` WHERE `id` = ?"
            );
            stmnt.setInt(1,userID);
            ResultSet rs = stmnt.executeQuery();
            User user = new User();
            while (rs.next()){

                user = (new User(
                        rs.getInt("id"),
                        rs.getInt("roleId"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("fathername"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getInt("errorCode")));
            }

            return user;
        }, gson::toJson);

        get("/studentsClass/:id", (request,response) -> {
            response.type("application/json");
            int classID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `studentsClass` WHERE `id` = ?"
            );
            stmnt.setInt(1,classID);
            ResultSet rs = stmnt.executeQuery();

            StudentsClass studentsClass = new StudentsClass();

            while (rs.next()){
                studentsClass = (new StudentsClass(
                        rs.getInt("id"),
                        rs.getInt("shiftId"),
                        rs.getString("code"),
                        rs.getInt("course"),
                        rs.getInt("errorCode")));
            }
            return studentsClass;
        }, gson::toJson);

        delete("/users/:id",(request,response)->{
            int userID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `users` WHERE `id` = ?"
            );
            stmnt.setInt(1,userID);
            stmnt.executeUpdate();
            return new User(0);
        }, gson::toJson);

        delete("/studentsClass/:id",(request,response)->{
            int classID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `studentsClass` WHERE `id` = ?"
            );
            stmnt.setInt(1,classID);
            stmnt.executeUpdate();
            return new StudentsClass(0);
        }, gson::toJson);

        post("/users",(request,response)->{
            User user = gson.fromJson(request.body(), User.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `users` (roleId, name, surname, fathername, " +
                            "login, password, errorCode) VALUES (?,?,?,?,?,?,?)"
            );

            stmnt.setInt(1,user.roleId);
            stmnt.setString(2,user.name);
            stmnt.setString(3,user.surname);
            stmnt.setString(4,user.fathername);
            stmnt.setString(5,user.login);
            stmnt.setString(6,user.password);
            stmnt.setInt(7,0);
            stmnt.executeUpdate();

            return user;
        }, gson::toJson);

        post("/studentsClass",(request,response)->{
            StudentsClass studentsClass = gson.fromJson(request.body(), StudentsClass.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `studentsClass` (shiftId, code, course, errorCode) VALUES (?,?,?,?)"
            );

            stmnt.setInt(1,studentsClass.shiftId);
            stmnt.setString(2,studentsClass.code);
            stmnt.setInt(3,studentsClass.course);
            stmnt.setInt(4,0);
            stmnt.executeUpdate();

            return studentsClass;
        }, gson::toJson);

        put("/users/:id",(request,response)->{
            int userID = Integer.parseInt(request.params("id"));
            User user = gson.fromJson(request.body(), User.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `users` SET `roleId`=?,`name`=?, `surname`=?, " +
                            "`fathername`=?, `login`=?, `password`=?, `errorCode`=? WHERE `id`=?"

            );

            stmnt.setInt(1,user.roleId);
            stmnt.setString(2,user.name);
            stmnt.setString(3,user.surname);
            stmnt.setString(4,user.fathername);
            stmnt.setString(5,user.login);
            stmnt.setString(6,user.password);
            stmnt.setInt(7,0);
            stmnt.setInt(8,userID);
            stmnt.executeUpdate();
            return user;
        }, gson::toJson);

        put("/studentsClass/:id",(request,response)->{
            int classID = Integer.parseInt(request.params("id"));
            StudentsClass studentsClass = gson.fromJson(request.body(), StudentsClass.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `studentsClass` SET `shiftId`=?,`code`=?, `course`=?, `errorCode`=? WHERE `id`=?");

            stmnt.setInt(1,studentsClass.shiftId);
            stmnt.setString(2,studentsClass.code);
            stmnt.setInt(3,studentsClass.course);
            stmnt.setInt(4,0);
            stmnt.setInt(5,classID);
            stmnt.executeUpdate();
            return studentsClass;
        }, gson::toJson);


        get("/subject", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `subject`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<Subject> subject = new ArrayList<>();
            while (rs.next()){
                subject.add(new Subject(
                        rs.getInt("id"),
                        rs.getInt("groupNumber"),
                        rs.getString("name"),
                        rs.getString("shortName"),
                        rs.getInt("errorCode")));
            }
            return subject;
        }, gson::toJson);

        get("/subject/:id", (request,response) -> {
            response.type("application/json");
            int subjectID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `subject` WHERE `id` = ?"
            );
            stmnt.setInt(1,subjectID);
            ResultSet rs = stmnt.executeQuery();

            Subject subject = (new Subject(
                    rs.getInt("id"),
                    rs.getInt("groupNumber"),
                    rs.getString("name"),
                    rs.getString("shortName"),
                    rs.getInt("errorCode")));


            return subject;
        }, gson::toJson);




        delete("/subject/:id",(request,response)->{
            int subjectID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `subject` WHERE `id` = ?"
            );
            stmnt.setInt(1,subjectID);
            stmnt.executeUpdate();
            return new Subject(0);
        }, gson::toJson);



        post("/subject",(request,response)->{
            Subject subject = gson.fromJson(request.body(), Subject.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `subject` (groupNumber, name, shortName, errorCode) VALUES (?,?,?,?)"
            );

            stmnt.setInt(1,subject.groupNumber);
            stmnt.setString(2,subject.name);
            stmnt.setString(3,subject.shortName);
            stmnt.setInt(4,0);
            stmnt.executeUpdate();

            return subject;
        }, gson::toJson);

        put("/subject/:id",(request,response)->{
            int subjectID = Integer.parseInt(request.params("id"));
            Subject subject = gson.fromJson(request.body(), Subject.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `subject` SET `groupNumber`=?,`name`=?, `shortName`=?, `errorCode`=? WHERE `id`=?");

            stmnt.setInt(1,subject.groupNumber);
            stmnt.setString(2,subject.name);
            stmnt.setString(3,subject.shortName);
            stmnt.setInt(4,0);
            stmnt.setInt(5,subjectID);
            stmnt.executeUpdate();
            return subject;
        }, gson::toJson);


        get("/scheduleOwner", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `scheduleOwner`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<ScheduleOwner> scheduleOwner = new ArrayList<>();
            while (rs.next()){
                scheduleOwner.add(new ScheduleOwner(
                        rs.getInt("id"),
                        rs.getInt("numberOfTheSchemaId"),
                        rs.getString("userOwnerId"),
                        rs.getBoolean("main"),
                        rs.getInt("errorCode")));
            }
            return scheduleOwner;
        }, gson::toJson);

        get("/scheduleOwner/:id", (request,response) -> {
            response.type("application/json");
            int scheduleOwnerID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `scheduleOwner` WHERE `id` = ?"
            );
            stmnt.setInt(1,scheduleOwnerID);
            ResultSet rs = stmnt.executeQuery();

            ScheduleOwner scheduleOwner = (new ScheduleOwner(
                    rs.getInt("id"),
                    rs.getInt("numberOfTheSchemaId"),
                    rs.getString("userOwnerId"),
                    rs.getBoolean("main"),
                    rs.getInt("errorCode")));


            return scheduleOwner;
        }, gson::toJson);


        delete("/scheduleOwner/:id",(request,response)->{
            int scheduleOwnerID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `scheduleOwner` WHERE `id` = ?"
            );
            stmnt.setInt(1,scheduleOwnerID);
            stmnt.executeUpdate();
            return new ScheduleOwner(0);
        }, gson::toJson);



        post("/scheduleOwner",(request,response)->{
            ScheduleOwner scheduleOwner = gson.fromJson(request.body(), ScheduleOwner.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `scheduleOwner` (numberOfTheSchemaId, userOwnerId, main, errorcode) VALUES (?,?,?,?)"
            );

            stmnt.setInt(1,scheduleOwner.numberOfTheSchemaId);
            stmnt.setString(2,scheduleOwner.userOwnerId);
            stmnt.setBoolean(3,scheduleOwner.main);
            stmnt.setInt(4,scheduleOwner.errorCode);
            stmnt.executeUpdate();

            return scheduleOwner;
        }, gson::toJson);

        put("/scheduleOwner/:id",(request,response)->{
            int scheduleOwnerID = Integer.parseInt(request.params("id"));
            ScheduleOwner scheduleOwner = gson.fromJson(request.body(), ScheduleOwner.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `scheduleOwner` SET `numberOfTheSchemaId`=?,`userOwnerId`=?, `main`=? ,`errorCode`=?  WHERE `id`=?");

            stmnt.setInt(1,scheduleOwner.numberOfTheSchemaId);
            stmnt.setString(2,scheduleOwner.userOwnerId);
            stmnt.setBoolean(3,scheduleOwner.main);
            stmnt.setInt(4,0);
            stmnt.setInt(5,scheduleOwnerID);
            stmnt.executeUpdate();
            return scheduleOwner;
        }, gson::toJson);


        get("/workload", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `workload`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<Workload> workload = new ArrayList<>();
            while (rs.next()){
                workload.add(new Workload(
                        rs.getInt("id"),
                        rs.getInt("studentClassId"),
                        rs.getInt("subjectId"),
                        rs.getInt("userId"),
                        rs.getInt("errorCode")));
           }
            return workload;
        }, gson::toJson);

        get("/workload/:id", (request,response) -> {
            response.type("application/json");
            int workloadID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `workload` WHERE `id` = ?"
            );
            stmnt.setInt(1,workloadID);
            ResultSet rs = stmnt.executeQuery();

            Workload workload = (new Workload(
                    rs.getInt("id"),
                    rs.getInt("studentClassId"),
                    rs.getInt("subjectId"),
                    rs.getInt("userId"),
                    rs.getInt("errorCode")));


            return workload;
        }, gson::toJson);


        delete("/workload/:id",(request,response)->{
            int workloadID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `workload` WHERE `id` = ?"
            );
            stmnt.setInt(1,workloadID);
            stmnt.executeUpdate();
            return new Workload(0);
        }, gson::toJson);

        post("/workload",(request,response)->{
            Workload workload = gson.fromJson(request.body(), Workload.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `workload` (groupNumber, name, shortName, errorCode) VALUES (?,?,?,?)"
            );

            stmnt.setInt(1,workload.studentClassId);
            stmnt.setInt(2,workload.subjectId);
            stmnt.setInt(3,workload.userId);
            stmnt.setInt(3,workload.errorCode);
            stmnt.executeUpdate();

            return workload;
        }, gson::toJson);

        put("/workload/:id",(request,response)->{
            int workloadID = Integer.parseInt(request.params("id"));
            Workload workload = gson.fromJson(request.body(), Workload.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `workload` SET `studentClassId`=?,`subjectId`=?, `userId`=?, `errorCode`=?  WHERE `id`=?");

            stmnt.setInt(1,workload.studentClassId);
            stmnt.setInt(2,workload.subjectId);
            stmnt.setInt(3,workload.userId);
            stmnt.setInt(4,0);
            stmnt.setInt(5,workloadID);
            stmnt.executeUpdate();
            return workload;
        }, gson::toJson);


        get("/cabinets", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `cabinet`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<Cabinet> cabinets = new ArrayList<>();
            while (rs.next()){
                cabinets.add(new Cabinet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("errorCode")));
            }
            return cabinets;
        }, gson::toJson);

        get("/days", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `day`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<Day> days = new ArrayList<>();
            while (rs.next()){
                days.add(new Day(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("errorCode")));
            }
            return days;
        }, gson::toJson);

        get("/schedule", (request,response) -> {
            response.type("application/json");
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `schedule`"
            );
            ResultSet rs = stmnt.executeQuery();
            List<Schedule> schedule = new ArrayList<>();
            while (rs.next()){
                schedule.add(new Schedule(
                        rs.getInt("id"),
                        rs.getInt("subjectId"),
                        rs.getInt("studentClassId"),
                        rs.getInt("cabinetId"),
                        rs.getInt("dayId"),
                        rs.getInt("numberOfTheSchema"),
                        rs.getInt("numberOfTheLesson"),
                        rs.getInt("errorCode")
                ));
            }
            return schedule;
        }, gson::toJson);

        get("/cabinets/:id", (request,response) -> {
            response.type("application/json");
            int cabinetID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `cabinet` WHERE `id` = ?"
            );
            stmnt.setInt(1,cabinetID);
            ResultSet rs = stmnt.executeQuery();

            Cabinet cabinets = new Cabinet(rs.getInt("id"), rs.getString("name"), rs.getInt("errorCode"));

            return cabinets;
        }, gson::toJson);

        get("/days/:id", (request,response) -> {
            response.type("application/json");
            int dayID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `day` WHERE `id` = ?"
            );

            stmnt.setInt(1,dayID);
            ResultSet rs = stmnt.executeQuery();

            Day days = new Day(rs.getInt("id"), rs.getString("name"), rs.getInt("errorCode"));

            return days;
        }, gson::toJson);

        get("/schedule/:id", (request,response) -> {
            response.type("application/json");
            int scheduleID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "SELECT * FROM `schedule` WHERE `id` = ?"
            );

            stmnt.setInt(1,scheduleID);
            ResultSet rs = stmnt.executeQuery();

            Schedule schedule = new Schedule(
                    rs.getInt("id"),
                    rs.getInt("subjectId"),
                    rs.getInt("studentClassId"),
                    rs.getInt("cabinetId"),
                    rs.getInt("dayId"),
                    rs.getInt("numberOfTheSchema"),
                    rs.getInt("numberOfTheLesson"),
                    rs.getInt("errorCode")
            );

            return schedule;
        }, gson::toJson);
        delete("/cabinets/:id",(request,response)->{
            int cabinetID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `cabinet` WHERE `id` = ?"
            );
            stmnt.setInt(1,cabinetID);
            stmnt.executeUpdate();
            return new Cabinet(0);
        }, gson::toJson);

        delete("/days/:id",(request,response)->{
            int dayID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `day` WHERE `id` = ?"
            );
            stmnt.setInt(1,dayID);
            stmnt.executeUpdate();
            return new Day(0);
        }, gson::toJson);

        delete("/schedule/:id",(request,response)->{
            int scheduleID = Integer.parseInt(request.params("id"));
            PreparedStatement stmnt = conn.prepareStatement(
                    "DELETE FROM `schedule` WHERE `id` = ?"
            );
            stmnt.setInt(1,scheduleID);
            stmnt.executeUpdate();
            return new Schedule(0);
        }, gson::toJson);

        post("/cabinets",(request,response)->{
            Cabinet cabinets = gson.fromJson(request.body(), Cabinet.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `cabinet` (name, errorCode) VALUES (?,?)"
            );

            stmnt.setString(1,cabinets.name);
            stmnt.setInt(2,0);
            stmnt.executeUpdate();

            return cabinets;
        }, gson::toJson);

        post("/days",(request,response)->{
            Day days = gson.fromJson(request.body(), Day.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `day` (name, errorCode) VALUES (?,?)"
            );

            stmnt.setString(1,days.name);
            stmnt.setInt(2,0);
            stmnt.executeUpdate();

            return days;
        }, gson::toJson);

        post("/schedule",(request,response)->{
            Schedule schedule = gson.fromJson(request.body(), Schedule.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "INSERT INTO `schedule` (subjectId, studentClassId, cabinetId, dayId, numberOfTheSchema, numberOfTheLesson, errorCode) VALUES (?,?,?,?,?,?,?)"
            );

            stmnt.setInt(1,schedule.subjectId);
            stmnt.setInt(2,schedule.studentClassId);
            stmnt.setInt(3,schedule.cabinetId);
            stmnt.setInt(4,schedule.dayId);
            stmnt.setInt(5,schedule.numberOfTheSchema);
            stmnt.setInt(6,schedule.numberOfTheLesson);
            stmnt.setInt(7,0);
            stmnt.executeUpdate();

            return schedule;
        }, gson::toJson);



        put("/cabinets/:id",(request,response)->{
            int cabinetID = Integer.parseInt(request.params("id"));
            Cabinet cabinets = gson.fromJson(request.body(), Cabinet.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `cabinet` SET `name`=?, `errorCode`=? WHERE `id`=?");

            stmnt.setString(1,cabinets.name);
            stmnt.setInt(2,0);
            stmnt.setInt(3,cabinetID);
            stmnt.executeUpdate();

            return cabinets;
        }, gson::toJson);

        put("/days/:id",(request,response)->{
            int dayID = Integer.parseInt(request.params("id"));
            Day days = gson.fromJson(request.body(), Day.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `day` SET `name`=?, `errorCode`=? WHERE `id`=?");

            stmnt.setString(1,days.name);
            stmnt.setInt(2,0);
            stmnt.setInt(3,dayID);
            stmnt.executeUpdate();

            return days;
        }, gson::toJson);

        post("/schedule/:id",(request,response)->{
            int scheduleID = Integer.parseInt(request.params("id"));
            Schedule schedule = gson.fromJson(request.body(), Schedule.class);
            PreparedStatement stmnt = conn.prepareStatement(
                    "UPDATE `schedule` SET `subjectId`=?, `studentClassId`=?, `cabinetId`=?, `dayId`=?, `numberOfTheSchema`=?, `numberOfTheLesson`=?, `errorCode`=? WHERE `id`=?"
            );

            stmnt.setInt(1,schedule.subjectId);
            stmnt.setInt(2,schedule.studentClassId);
            stmnt.setInt(3,schedule.cabinetId);
            stmnt.setInt(4,schedule.dayId);
            stmnt.setInt(5,schedule.numberOfTheSchema);
            stmnt.setInt(6,schedule.numberOfTheLesson);
            stmnt.setInt(7,0);
            stmnt.setInt(8,scheduleID);
            stmnt.executeUpdate();

            return schedule;
        }, gson::toJson);
    }
}
