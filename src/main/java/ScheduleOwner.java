public class ScheduleOwner {
    public int id;
    public int numberOfTheSchemaId;
    public String userOwnerId;
    public boolean main;
    public int errorCode;

    public ScheduleOwner(int id, int numberOfTheSchemaId, String userOwnerId, boolean main, int errorCode) {
        this.id = id;
        this.numberOfTheSchemaId = numberOfTheSchemaId;
        this.userOwnerId = userOwnerId;
        this.main = main;
        this.errorCode = errorCode;

    }
    public ScheduleOwner(){

        this.errorCode = 1;
    }

    public ScheduleOwner(int errorCode) {
        this.errorCode = 0;
    }

}
