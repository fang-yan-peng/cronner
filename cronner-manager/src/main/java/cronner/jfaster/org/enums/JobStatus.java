package cronner.jfaster.org.enums;

import lombok.Getter;

/**
 * @author fangyanpeng
 */
public enum JobStatus {

    PAUSE(0,"停止"),STARTUP(1,"开启"), SHUTDOWN(2,"关闭"),OTHER(2,"其它");

    @Getter
    private String desc;

    @Getter
    private int flag;

    JobStatus(int flag,String desc){
        this.flag = flag;
        this.desc = desc;
    }

    public static JobStatus of(int flag){
        for(JobStatus status : JobStatus.values()){
            if(status.flag == flag){
                return status;
            }
        }
        return JobStatus.OTHER;
    }

}
