package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.ArticleTag;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleTagService extends IService<ArticleTag> {
    PageVO<ArticleTag> page(PageDTO<ArticleTag> dto);
}
