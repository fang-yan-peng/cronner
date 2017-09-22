package cronner.jfaster.org.job.storage;

import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;

/**
 * 事务执行操作的回调接口.
 *
 * @author fangyanpeng
 */
public interface TransactionExecutionCallback {
    
    /**
     * 事务执行的回调方法.
     * 
     * @param curatorTransactionFinal 执行事务的上下文
     * @throws Exception 处理中异常
     */
    void execute(CuratorTransactionFinal curatorTransactionFinal) throws Exception;
}
