package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.entity.Comment;
import blog.service.CommentService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public Result<Comment> getById(@PathVariable Long id) {
        log.info("根据ID查询评论, id:{}", id);
        return Result.success(commentService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody Comment comment) {
        log.info("新增评论:{}", JSON.toJSONString(comment, SerializerFeature.PrettyFormat));
        commentService.save(comment);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody Comment comment) {
        log.info("更新评论:{}", JSON.toJSONString(comment, SerializerFeature.PrettyFormat));
        commentService.updateById(comment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除评论, id:{}", id);
        commentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<Comment>> page(@RequestBody PageDTO<Comment> dto) {
        log.info("分页查询评论:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(commentService.page(dto));
    }
}
