package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.entity.Category;
import blog.service.CategoryService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        log.info("根据ID查询分类, id:{}", id);
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody Category category) {
        log.info("新增分类:{}", JSON.toJSONString(category, SerializerFeature.PrettyFormat));
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody Category category) {
        log.info("更新分类:{}", JSON.toJSONString(category, SerializerFeature.PrettyFormat));
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除分类, id:{}", id);
        categoryService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<Category>> page(@RequestBody PageDTO<Category> dto) {
        log.info("分页查询分类:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(categoryService.page(dto));
    }
}
