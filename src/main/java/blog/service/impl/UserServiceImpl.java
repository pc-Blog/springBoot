package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.User;
import blog.exception.BaseException;
import blog.mapper.UserMapper;
import blog.service.UserService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public PageVO<User> page(PageDTO<User> dto) {
        var page = PageUtil.<User>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean save(User user) {
        checkUsernameUnique(user.getUsername(), null);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return super.save(user);
    }

    @Override
    public boolean updateById(User user) {
        checkUsernameUnique(user.getUsername(), user.getId());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        return super.updateById(user);
    }

    private void checkUsernameUnique(String username, Long excludeId) {
        var wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BaseException("用户名已存在");
        }
    }
}
