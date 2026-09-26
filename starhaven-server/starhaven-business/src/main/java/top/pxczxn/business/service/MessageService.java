package top.pxczxn.business.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.Message;
import top.pxczxn.business.mapper.MessageMapper;
import top.pxczxn.business.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public List<MessageVO> list() {
        long userId = StpUtil.getLoginIdAsLong();
        return messageMapper.selectList(Wrappers.<Message>lambdaQuery()
                        .eq(Message::getUserId, userId)
                        .orderByDesc(Message::getId))
                .stream()
                .map(item -> {
                    MessageVO vo = new MessageVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                })
                .toList();
    }

    public void read(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null || !message.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            return;
        }
        message.setReadStatus(1);
        messageMapper.updateById(message);
    }
}
