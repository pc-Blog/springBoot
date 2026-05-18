package blog.service.impl;

import blog.common.PageDTO;
import blog.common.PageVO;
import blog.entity.Media;
import blog.mapper.MediaMapper;
import blog.service.MediaService;
import blog.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements MediaService {

    @Override
    public PageVO<Media> page(PageDTO<Media> dto) {
        var page = PageUtil.<Media>toPage(dto);
        page(page);
        return new PageVO<>(page.getTotal(), page.getRecords());
    }
}
