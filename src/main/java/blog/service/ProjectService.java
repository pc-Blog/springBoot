package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProjectService extends IService<Project> {
    PageVO<Project> page(PageDTO<Project> dto);
}
