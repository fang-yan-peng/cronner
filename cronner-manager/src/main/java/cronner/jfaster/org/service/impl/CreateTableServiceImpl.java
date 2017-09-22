package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.event.type.DatabaseType;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.factory.CronnerDatasourceFactory;
import cronner.jfaster.org.service.CreateTalbleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static cronner.jfaster.org.constants.CronnerConstant.TABLE_JOB;
import static cronner.jfaster.org.constants.CronnerConstant.TABLE_TASK;
import static cronner.jfaster.org.constants.CronnerConstant.TABLE_TRACE;


/**
 * 表初始化
 * @author fangyanpeng
 */
@Slf4j
@Service
public class CreateTableServiceImpl implements CreateTalbleService {

    @Override
    public void createTables() {

        Connection connection = null;
        try {
            DataSource dataSource = CronnerDatasourceFactory.getDataSource();
            connection = dataSource.getConnection();
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            if(DatabaseType.valueFrom(databaseProductName) != DatabaseType.MySQL){
                throw new JobConfigurationException(String.format("Unsupported database %s, please use MySql",databaseProductName));
            }
            createJobConfigurationTable(connection);
            createTaskTable(connection);
            createTaskTaceTable(connection);
        } catch (Exception e) {
            log.error("Init tables fail: ",e);
            throw new JobConfigurationException(e);
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Close connection error: ",e);
                }
            }
        }
    }

    private void createJobConfigurationTable(final Connection conn) throws SQLException {
        String dbSchema = "CREATE TABLE IF NOT EXISTS `" + TABLE_JOB + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, "
                + "`jobName` VARCHAR(50) NOT NULL, "
                + "`cron` VARCHAR(20) NOT NULL, "
                + "`shardingTotalCount` INT NOT NULL, "
                + "`shardingParameter` VARCHAR(100) NOT NULL, "
                + "`jobParameter` VARCHAR(20) NULL, "
                + "`jobShardingStrategyClass` VARCHAR(100) NULL, "
                + "`failover` INT NOT NULL, "
                + "`allowSendJobEvent` INT NOT NULL, "
                + "`misfire` INT NOT NULL, "
                + "`monitorExecution` INT NOT NULL, "
                + "`description` VARCHAR(50) NULL, "
                + "`status` INT NOT NULL, "
                + "`reconcileIntervalMinutes` INT NOT NULL, "
                + "`type` INT NOT NULL, "
                + "`streamingProcess` INT NOT NULL, "
                + "`createTime` DATETIME NOT NULL, "
                + "`updateTime` DATETIME NOT NULL, "
                + "`lastSuccessTime` DATETIME NULL, "
                + "`nextExecuteTime` DATETIME NULL, "
                + "PRIMARY KEY (`id`),"
                + "UNIQUE INDEX unique_index_job_name(`jobName`),"
                + "INDEX index_create_time(`createTime`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
            preparedStatement.execute();
        }
    }

    private void createTaskTable(final Connection conn) throws SQLException {
        String dbSchema = "CREATE TABLE IF NOT EXISTS `" + TABLE_TASK + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, "
                + "`jobId` INT UNSIGNED NOT NULL, "
                + "`jobName` VARCHAR(50) NOT NULL, "
                + "`shardItems` VARCHAR(30) NOT NULL, "
                + "`status` INT NOT NULL, "
                + "`hostname` VARCHAR(50) NULL, "
                + "`ip` VARCHAR(50) NULL, "
                + "`source` INT NULL, "
                + "`parentId` INT NULL, "
                + "`failureCause` VARCHAR (2048) NULL, "
                + "`createTime` DATETIME NOT NULL, "
                + "`startTime` DATETIME NULL, "
                + "`completeTime` DATETIME NULL, "
                + "PRIMARY KEY (`id`),"
                + "INDEX index_job_name(`jobName`), "
                + "INDEX index_parent_id(`parentId`),"
                + "INDEX index_create_time(`createTime`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
            preparedStatement.execute();
        }
    }

    private void createTaskTaceTable(final Connection conn) throws SQLException {
        String dbSchema = "CREATE TABLE IF NOT EXISTS `" + TABLE_TRACE + "` ("
                + "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, "
                + "`jobName` VARCHAR(50) NOT NULL, "
                + "`currentIp` VARCHAR(50) NULL, "
                + "`taskId` INT NOT NULL, "
                + "`executionType` INT NOT NULL, "
                + "`shardingItems` VARCHAR(30) NOT NULL, "
                + "`state` INT NOT NULL, "
                + "`message` VARCHAR(100) NULL, "
                + "`createTime` DATETIME NOT NULL, "
                + "PRIMARY KEY (`id`),"
                + "INDEX index_task_id(`taskId`),"
                + "INDEX index_create_time(`createTime`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
            preparedStatement.execute();
        }
    }
}
