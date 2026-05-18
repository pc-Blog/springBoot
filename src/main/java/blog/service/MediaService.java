package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Media;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MediaService extends IService<Media> {
    PageVO<Media> page(PageDTO<Media> dto);
}
