package cronner.jfaster.org.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 分页参数
 * @author fangyanpeng
 */
@Setter
@Getter
@NoArgsConstructor
public class PageParam {

    private int pageSize;

    private int totalCnt;

    private int totalPages;

    private int page;

    private int start;

    private List<?> data = Collections.emptyList();

    public PageParam(int page,int pageSize,int totalCnt){
        this.pageSize = pageSize;
        this.totalCnt = totalCnt;
        this.totalPages = totalCnt % pageSize == 0 ? totalCnt / pageSize : totalCnt / pageSize + 1;
        this.page = page;
        start = (page - 1) * pageSize;
    }

}
