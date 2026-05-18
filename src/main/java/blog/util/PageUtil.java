package blog.util;

import blog.common.PageDTO;
import blog.common.SortDirection;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class PageUtil {

    public static <T> Page<T> toPage(PageDTO<?> dto) {
        Page<T> page = Page.of(dto.getPageNum(), dto.getPageSize());
        if (dto.getSortFields() != null) {
            for (PageDTO.SortField sf : dto.getSortFields()) {
                if (sf.getDirection() == SortDirection.DESC) {
                    page.addOrder(OrderItem.desc(sf.getField()));
                } else {
                    page.addOrder(OrderItem.asc(sf.getField()));
                }
            }
        }
        return page;
    }
}
