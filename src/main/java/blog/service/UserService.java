package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    PageVO<User> page(PageDTO<User> dto);
}
