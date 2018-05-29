public class Subject {

    public int id;
    public int groupNumber;
    public String name;
    public String shortName;
    public int errorCode;

    public Subject(int id, int groupNumber, String name, String shortName, int errorCode) {
        this.id = id;
        this.groupNumber = groupNumber;
        this.name = name;
        this.shortName = shortName;
        this.errorCode = errorCode;

    }

    public Subject(){

        this.errorCode = 1;
    }

    public Subject(int errorCode) {
        this.errorCode = 0;
    }

}
