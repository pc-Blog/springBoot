package blog.service;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ArticleService extends IService<Article> {
    PageVO<Article> page(PageDTO<Article> dto);

    boolean saveWithTags(Article article, List<Long> tagIds);

    boolean updateWithTags(Article article, List<Long> tagIds);
}
