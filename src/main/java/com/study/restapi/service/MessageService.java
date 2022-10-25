package com.study.restapi.service;

import com.study.restapi.dto.MessageDto;
import com.study.restapi.entity.Message;
import com.study.restapi.entity.User;
import com.study.restapi.repository.MessageRepository;
import com.study.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    // MessageDto에서 receiverName 과 senderName 을 받음
    // 이 둘을 이용해서, UserRepository에서 User정보를 name을 기반으로 받는다.
    // write 메소드에서 새로운 Message 클래스를 만들어서, 값을 설정한다.
    // 저장하고 Dto로 바꿔서 값을 준다.
    public MessageDto write(MessageDto messageDto) {
        User receiver = userRepository.findByName(messageDto.getReceiverName());
        User sender = userRepository.findByName(messageDto.getSenderName());

        Message message = new Message();
        message.setReceiver(receiver);
        message.setSender(sender);

        message.setTitle(messageDto.getTitle());
        message.setContent(messageDto.getContent());
        message.setDeletedByReceiver(false);
        message.setDeletedBySender(false);
        messageRepository.save(message);

        return MessageDto.toDto(message);
    }

    // receive&sentMessage 메소드의 경우
    // 현제 접속중인 유저를 (컨트롤러에서 세션을 기반으로 유저 정보를 전달받음) 받아서
    // MessageRepository에서 만든 findAllByReceiver 메소드를 이용해서
    // 편지들을 List<Message> 형태로 받는다.
    // 그리고나서, List<MessageDto> 에다가 message.isDeletedByxxx()메소드
    // (entity에서 만든 메소드)를 이용해서 받은 혹은 보낸 메시지가 삭제됐는지 확인하고,
    // 삭제가 안 됐으면 List<MessageDto>에 추가시켜 리턴

    @Transactional(readOnly = true)
    public MessageDto findMessageById(int id) {
        Message message = messageRepository.findById(id).orElseThrow(()->{
           return new IllegalArgumentException("메세지를 찾을 수 없습니다.");
        });
        return MessageDto.toDto(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> receivedMessages(User user) {
        // 받은 편지함 불러오기
        // 한명의 유저가 받은 모든 메세지
        // 추후 JWT를 이용해서 재구현 예정
        List<Message> messages = messageRepository.findAllByReceiver(user);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(Message message : messages) {
            // message 에서 받은 편지함에서 삭제하지 않았으면 보낼 때 추가해서 보내줌
            if(!message.isDeletedByReceiver()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }

    // 받은 편지 삭제
    @Transactional
    // deleteMessageByxxx 메소드 같은 경우
    // 메시지 id를 이용해서 메시지를 찾고,
    // 메세지를 보내거나 받은 유저가 현재 접속중인 유저와 같은지 확인하고,
    // 같다면, 보내거나 받은 유저에 해당하는 엔티티 필드의 값을 ture로 바꿔준다.
    // 이때 보낸 유저와 받은 유저 둘다 삭제를 했다면 DB에서 삭제를 한다.
    public Object deleteMessageByReceiver(MessageDto messageDto, User user) {
        Message message = messageRepository.findById(messageDto.getId()).get();
        message.deleteByReceiver(); // 받은 사람에게 메세지 삭제
        if (message.isDeleted()) {
            // 받은사람과 보낸 사람 모두 삭제했으면, 데이터베이스에서 삭제요청
            messageRepository.delete(message);
            return "양쪽 모두 삭제";
        }
        return "한쪽만 삭제";
    }

    @Transactional(readOnly = true)
    public List<MessageDto> sentMessage(User user) {
        // 보낸 편지함 불러오기
        // 한 명의 유저가 받은 모든 메세지
        // 추후 JWT를 이용해서 재구형 예정
        List<Message> messages = messageRepository.findAllBySender(user);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(Message message : messages) {
            // message에서 받은 편지함에서 삭제하지 않았으면 보낼 때 추가해서 보내줌
            if(!message.isDeletedBySender()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }
    // 보낸 편지 삭제
    @Transactional
    public Object deleteMessageBySender(MessageDto messageDto, User user) {
        Message message = messageRepository.findById(messageDto.getId()).get();
        message.deleteBySender();
        if (message.isDeleted()) {
            // 받은사람과 보낸 사람 모두 삭제했으면, 데이터베이스에서 삭제요청
            messageRepository.delete(message);
            return "양쪽 모두 삭제";
        }
        return "한쪽만 삭제";
    }
}
