package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Timeline;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TimelineService extends IService<Timeline> {
    PageVO<Timeline> page(PageDTO<Timeline> dto);
}
