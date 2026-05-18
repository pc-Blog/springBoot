package blog.controller;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.common.Result;
import blog.entity.User;
import blog.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        log.info("根据ID查询用户, id:{}", id);
        return Result.success(userService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody User user) {
        log.info("新增用户:{}", JSON.toJSONString(user, SerializerFeature.PrettyFormat));
        userService.save(user);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody User user) {
        log.info("更新用户:{}", JSON.toJSONString(user, SerializerFeature.PrettyFormat));
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除用户, id:{}", id);
        userService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    public Result<PageVO<User>> page(@RequestBody PageDTO<User> dto) {
        log.info("分页查询用户:{}", JSON.toJSONString(dto, SerializerFeature.PrettyFormat));
        return Result.success(userService.page(dto));
    }
}
