package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.ArticleTag;
import blog.mapper.ArticleTagMapper;
import blog.service.ArticleTagService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

    @Override
    public PageVO<ArticleTag> page(PageDTO<ArticleTag> dto) {
        var page = PageUtil.<ArticleTag>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }
}
