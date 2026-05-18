package blog.dto;

import blog.entity.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleSaveRequest extends Article {
    private List<Long> tagIds;
}
