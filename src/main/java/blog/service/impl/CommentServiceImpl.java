package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Article;
import blog.entity.Comment;
import blog.exception.BaseException;
import blog.mapper.ArticleMapper;
import blog.mapper.CommentMapper;
import blog.service.CommentService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final ArticleMapper articleMapper;

    public CommentServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public PageVO<Comment> page(PageDTO<Comment> dto) {
        var page = PageUtil.<Comment>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean save(Comment comment) {
        checkArticleValid(comment.getArticleId());
        return super.save(comment);
    }

    private void checkArticleValid(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw new BaseException("评论的文章不存在");
        }
        if (article.getIsPublished() == null || article.getIsPublished() != 1) {
            throw new BaseException("不能对未发布的文章发表评论");
        }
    }
}
