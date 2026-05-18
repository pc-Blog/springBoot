package blog.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {
    private int pageNum = 1;
    private int pageSize = 10;
    private List<SortField> sortFields;
    private T query = null;

    @JsonIgnore
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    @Data
    public static class SortField {
        private String field;      // 字段名
        private SortDirection direction = SortDirection.ASC;
    }
}
