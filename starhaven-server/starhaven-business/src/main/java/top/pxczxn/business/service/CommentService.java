package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.dto.CreateCommentDTO;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.Comment;
import top.pxczxn.business.entity.CommentImage;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.mapper.CommentImageMapper;
import top.pxczxn.business.mapper.CommentMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.vo.CommentVO;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final CommentImageMapper commentImageMapper;
    private final OrderService orderService;
    private final HouseMapper houseMapper;
    private final UserMapper userMapper;
    private final RedisFacade redisFacade;

    @Transactional
    public void create(CreateCommentDTO dto) {
        BookingOrder order = orderService.requireOwner(dto.getOrderId());
        if (!"FINISHED".equals(order.getOrderStatus()) && !"CHECK_IN".equals(order.getOrderStatus())) {
            throw new BusinessException("订单完成后才可评价");
        }
        Long exists = commentMapper.selectCount(Wrappers.<Comment>lambdaQuery().eq(Comment::getOrderId, order.getId()));
        if (exists > 0) {
            throw new BusinessException("该订单已评价");
        }
        Comment comment = new Comment();
        comment.setUserId(StpUtil.getLoginIdAsLong());
        comment.setHouseId(order.getHouseId());
        comment.setOrderId(order.getId());
        comment.setScore(dto.getScore());
        comment.setContent(dto.getContent());
        commentMapper.insert(comment);
        if (dto.getImages() != null) {
            for (String url : dto.getImages()) {
                CommentImage image = new CommentImage();
                image.setCommentId(comment.getId());
                image.setImageUrl(url);
                commentImageMapper.insert(image);
            }
        }
        refreshHouseScore(order.getHouseId());
        redisFacade.delete(RedisKeys.HOUSE_DETAIL + order.getHouseId());
    }

    public List<CommentVO> listByHouse(Long houseId) {
        return commentMapper.selectList(Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getHouseId, houseId)
                        .orderByDesc(Comment::getId))
                .stream()
                .map(this::toVo)
                .toList();
    }

    private CommentVO toVo(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setScore(comment.getScore());
        vo.setContent(comment.getContent());
        vo.setCreateTime(comment.getCreateTime());
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        vo.setImages(commentImageMapper.selectList(Wrappers.<CommentImage>lambdaQuery()
                        .eq(CommentImage::getCommentId, comment.getId()))
                .stream()
                .map(CommentImage::getImageUrl)
                .toList());
        return vo;
    }

    private void refreshHouseScore(Long houseId) {
        List<Comment> comments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getHouseId, houseId));
        House house = houseMapper.selectById(houseId);
        if (house == null) {
            return;
        }
        double avg = comments.stream().mapToInt(Comment::getScore).average().orElse(0);
        house.setAvgScore(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        house.setCommentCount(comments.size());
        houseMapper.updateById(house);
    }
}
