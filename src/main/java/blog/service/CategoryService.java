package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    PageVO<Category> page(PageDTO<Category> dto);
}
