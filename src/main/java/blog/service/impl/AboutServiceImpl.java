package blog.service.impl;

import blog.entity.About;
import blog.mapper.AboutMapper;
import blog.service.AboutService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AboutServiceImpl extends ServiceImpl<AboutMapper, About> implements AboutService {

    @Override
    public About getAbout() {
        return getOne(new LambdaQueryWrapper<About>().last("LIMIT 1"));
    }

    @Override
    public boolean updateAbout(About about) {
        About existing = getAbout();
        if (existing != null) {
            about.setId(existing.getId());
            return updateById(about);
        }
        return save(about);
    }
}
