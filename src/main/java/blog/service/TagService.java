package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TagService extends IService<Tag> {
    PageVO<Tag> page(PageDTO<Tag> dto);
}
