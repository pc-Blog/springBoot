package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.dto.ArticleSaveRequest;
import blog.entity.Article;
import blog.service.ArticleService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        log.info("根据ID查询文章, id:{}", id);
        return Result.success(articleService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody ArticleSaveRequest request) {
        log.info("新增文章:{}", JSON.toJSONString(request, SerializerFeature.PrettyFormat));
        articleService.saveWithTags(request, request.getTagIds());
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody ArticleSaveRequest request) {
        log.info("更新文章:{}", JSON.toJSONString(request, SerializerFeature.PrettyFormat));
        articleService.updateWithTags(request, request.getTagIds());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除文章, id:{}", id);
        articleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<Article>> page(@RequestBody PageDTO<Article> dto) {
        log.info("分页查询文章:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(articleService.page(dto));
    }
}
