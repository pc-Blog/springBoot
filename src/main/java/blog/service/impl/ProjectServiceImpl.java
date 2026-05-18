package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Category;
import blog.entity.Project;
import blog.exception.BaseException;
import blog.mapper.CategoryMapper;
import blog.mapper.ProjectMapper;
import blog.service.ProjectService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    private final CategoryMapper categoryMapper;

    public ProjectServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageVO<Project> page(PageDTO<Project> dto) {
        var page = PageUtil.<Project>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean save(Project project) {
        checkCategoryValid(project.getCategoryId());
        return super.save(project);
    }

    @Override
    public boolean updateById(Project project) {
        checkCategoryValid(project.getCategoryId());
        return super.updateById(project);
    }

    private void checkCategoryValid(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || category.getDeleted() == 1) {
            throw new BaseException("项目分类不存在");
        }
        if (!"PROJECT".equals(category.getType())) {
            throw new BaseException("所选分类不是项目类型");
        }
    }
}
