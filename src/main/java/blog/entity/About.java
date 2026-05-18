package blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_about")
public class About {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    @Email(message = "联系邮箱格式不正确")
    private String contactEmail;

    private String githubUrl;
    private String socialLinks;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
