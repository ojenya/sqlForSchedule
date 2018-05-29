public class Workload {
    public int id;
    public int studentClassId;
    public int subjectId;
    public int userId;
    public int errorCode;

    public Workload(int anInt, int id, int studentClassId, int subjectId, int userId) {
        this.id = id;
        this.studentClassId = studentClassId;
        this.subjectId = subjectId;
        this.userId = userId;
    }

    public Workload(){

        this.errorCode = 1;
    }

    public Workload(int errorCode) {
        this.errorCode = 0;
    }
}
