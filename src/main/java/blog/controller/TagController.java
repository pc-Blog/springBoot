package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.entity.Tag;
import blog.service.TagService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public Result<Tag> getById(@PathVariable Long id) {
        log.info("根据ID查询标签, id:{}", id);
        return Result.success(tagService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody Tag tag) {
        log.info("新增标签:{}", JSON.toJSONString(tag, SerializerFeature.PrettyFormat));
        tagService.save(tag);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody Tag tag) {
        log.info("更新标签:{}", JSON.toJSONString(tag, SerializerFeature.PrettyFormat));
        tagService.updateById(tag);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除标签, id:{}", id);
        tagService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<Tag>> page(@RequestBody PageDTO<Tag> dto) {
        log.info("分页查询标签:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(tagService.page(dto));
    }
}
