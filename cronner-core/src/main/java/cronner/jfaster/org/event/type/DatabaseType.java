package cronner.jfaster.org.event.type;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import java.util.Arrays;

/**
 * 数据库类型.
 *
 * @author fangyanpeng
 * 
 */
public enum DatabaseType {
    
    H2("H2"), MySQL("MySQL"), Oracle("Oracle"), SQLServer("Microsoft SQL Server"), DB2("DB2"), PostgreSQL("PostgreSQL");
    
    private final String productName;
    
    DatabaseType(final String productName) {
        this.productName = productName;
    }
    
    /**
     * 获取数据库类型枚举.
     * 
     * @param databaseProductName 数据库类型
     * @return 数据库类型枚举
     */
    public static DatabaseType valueFrom(final String databaseProductName) {
        Optional<DatabaseType> databaseTypeOptional = Iterators.tryFind(Arrays.asList(DatabaseType.values()).iterator(), new Predicate<DatabaseType>() {
            @Override
            public boolean apply(final DatabaseType input) {
                return input.productName.equals(databaseProductName);
            }
        });
        if (databaseTypeOptional.isPresent()) {
            return databaseTypeOptional.get();
        } else {
            throw new RuntimeException("Unsupported database:" + databaseProductName);
        }
    }
}
