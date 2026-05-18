package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Skill;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SkillService extends IService<Skill> {
    PageVO<Skill> page(PageDTO<Skill> dto);
}
