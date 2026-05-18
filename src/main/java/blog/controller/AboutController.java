package blog.controller;

import blog.common.Result;
import blog.entity.About;
import blog.service.AboutService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/about")
public class AboutController {

    private final AboutService aboutService;

    public AboutController(AboutService aboutService) {
        this.aboutService = aboutService;
    }

    @GetMapping
    public Result<About> get() {
        log.info("查询关于信息");
        return Result.success(aboutService.getAbout());
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody About about) {
        log.info("更新关于信息:{}", JSON.toJSONString(about, SerializerFeature.PrettyFormat));
        aboutService.updateAbout(about);
        return Result.success();
    }
}
