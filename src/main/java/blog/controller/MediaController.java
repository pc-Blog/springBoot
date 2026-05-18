package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.entity.Media;
import blog.service.MediaService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/{id}")
    public Result<Media> getById(@PathVariable Long id) {
        log.info("根据ID查询媒体文件, id:{}", id);
        return Result.success(mediaService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody Media media) {
        log.info("新增媒体文件:{}", JSON.toJSONString(media, SerializerFeature.PrettyFormat));
        mediaService.save(media);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody Media media) {
        log.info("更新媒体文件:{}", JSON.toJSONString(media, SerializerFeature.PrettyFormat));
        mediaService.updateById(media);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除媒体文件, id:{}", id);
        mediaService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<Media>> page(@RequestBody PageDTO<Media> dto) {
        log.info("分页查询媒体文件:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(mediaService.page(dto));
    }
}
