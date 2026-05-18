package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Article;
import blog.entity.ArticleTag;
import blog.entity.Category;
import blog.entity.Tag;
import blog.exception.BaseException;
import blog.mapper.ArticleMapper;
import blog.mapper.ArticleTagMapper;
import blog.mapper.CategoryMapper;
import blog.mapper.TagMapper;
import blog.service.ArticleService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public ArticleServiceImpl(ArticleTagMapper articleTagMapper, CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.articleTagMapper = articleTagMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public PageVO<Article> page(PageDTO<Article> dto) {
        var page = PageUtil.<Article>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional
    public boolean saveWithTags(Article article, List<Long> tagIds) {
        checkCategoryValid(article.getCategoryId());
        checkTagIdsValid(tagIds);
        save(article);
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateWithTags(Article article, List<Long> tagIds) {
        checkCategoryValid(article.getCategoryId());
        checkTagIdsValid(tagIds);
        updateById(article);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId()));
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
        return true;
    }

    private void checkCategoryValid(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || category.getDeleted() == 1) {
            throw new BaseException("文章分类不存在");
        }
        if (!"ARTICLE".equals(category.getType())) {
            throw new BaseException("所选分类不是文章类型");
        }
    }

    private void checkTagIdsValid(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            Tag tag = tagMapper.selectById(tagId);
            if (tag == null || tag.getDeleted() == 1) {
                throw new BaseException("标签不存在: id=" + tagId);
            }
        }
    }
}
