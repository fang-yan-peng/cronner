package cronner.jfaster.org.enums;

import lombok.Getter;

/**
 * @author fangyanpeng
 */
public enum  TaskStatus {

    READY(0,"准备运行"),RUNNING(1,"运行中"),SUCCESS(2,"成功"),FAIL(3,"失败"),OTHER(4,"其它");

    @Getter
    private String desc;

    @Getter
    private int flag;

    TaskStatus(int flag,String desc){
        this.flag = flag;
        this.desc = desc;
    }

    public static TaskStatus of(int flag){
        for(TaskStatus status : TaskStatus.values()){
            if(status.flag == flag){
                return status;
            }
        }
        return TaskStatus.OTHER;
    }
}
