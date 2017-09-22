package cronner.jfaster.org.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cronner.jfaster.org.constants.CronnerConstant;
import org.jfaster.mango.datasource.SimpleDataSourceFactory;
import org.jfaster.mango.operator.Mango;
import org.jfaster.mango.plugin.spring.AbstractMangoFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

/**
 * @author fangyanpeng
 */
public class CronnerDatasourceFactory extends AbstractMangoFactoryBean {

    private static final Logger logger = LoggerFactory.getLogger(CronnerDatasourceFactory.class);

    @Value("${datasource.driver}")
    private String driver;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.user}")
    private String user;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.maximumPoolSize:20}")
    private int maxPoolSize;

    @Value("${datasource.connectionTimeout:3000}")
    private long connectionTimeout;

    @Value("${datasource.autoCommit:true}")
    private boolean autoCommit;

    private static volatile Mango mango;


    @Override
    public Mango createMango() {
        try {
            Mango mango = Mango.newInstance(new SimpleDataSourceFactory(CronnerConstant.DB,buildDataSource()));
            this.mango = mango;
            return mango;
        } catch (Exception e) {
            logger.error("Init datasource error",e);
            throw e;
        }
    }

    public static DataSource getDataSource(){
        if(mango == null){
            return null;
        }
        return mango.getMasterDataSource(CronnerConstant.DB);
    }

    protected DataSource buildDataSource() {
        if (maxPoolSize < 10) {
            maxPoolSize = 10;
        }
        if (maxPoolSize > 1000) {
            maxPoolSize = 1000;
        }
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTimeout(connectionTimeout);
        config.setInitializationFailTimeout(connectionTimeout);
        config.setAutoCommit(autoCommit);
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
}
