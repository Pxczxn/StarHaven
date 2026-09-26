package top.pxczxn.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.pxczxn.business.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
