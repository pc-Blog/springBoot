package blog.service;

import blog.entity.About;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AboutService extends IService<About> {
    About getAbout();

    boolean updateAbout(About about);
}
