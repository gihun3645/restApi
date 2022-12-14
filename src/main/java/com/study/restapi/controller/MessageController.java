package com.study.restapi.controller;


import com.study.restapi.config.PrincipalDetails;
import com.study.restapi.dto.MessageDto;
import com.study.restapi.entity.User;
import com.study.restapi.repository.UserRepository;
import com.study.restapi.response.Response;
import com.study.restapi.service.MessageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @ApiOperation(value = "쪽지 보내기, ", notes = "쪽지 보내기")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/messages")
    public Response<?> sendMessage(@RequestBody MessageDto messageDto,
                                   Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        messageDto.setSenderName(user.getName());

        return new Response<>("성공", "쪽지를 보냈습니다.", messageService.write(messageDto));
    }

    @ApiOperation(value = "받은 편지함 읽기", notes = "받은 편지함 확인")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/messages/received")
    public Response<?> getReceivedMessage(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        return new Response("성공", "받은 쪽지를 불러왔습니다.", messageService.receivedMessages(user));
    }

    @ApiOperation(value = "받은 쪽지 삭제하기", notes = "받은 쪽지를 삭제합니다.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/messages/received/{id}")
    public Response<?> deleteReceivedMessage(@PathVariable("id") Integer id,
                                             Authentication authentication) {
        // 임의로 유저 정보를 넣었지만, JWT 도입하고 현재 로그인 된 유저의 정보를 넘겨줘야함
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        MessageDto messageDto = messageService.findMessageById(id);

        if(messageDto.getReceiverName().equals(user.getName())) {
            return new Response<>("삭제성공", "받은 쪽지인, "+id+"번 쪽지를 삭제했습니다.", messageService.deleteMessageByReceiver(messageDto, user));
        } else {
            return new Response<>("삭제 실패", "사용자 정보가 다릅니다.", null);
        }
    }

    @ApiOperation(value = "보낸 편지함 읽기", notes = "보낸 편지함 확인")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/messages/sent")
    public Response<?> getSentMessage(Authentication authentication) {
        // 임의로 유저 정보를 넣었지만, JWT 도입하고 현제 로그인 된 유저의 정보를 넘겨줘야함

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        return new Response("성공", "보낸 쪽지를 불러왔습니다.", messageService.sentMessage(user));
    }

    @ApiOperation(value = "보낸 쪽지 삭제하기", notes = "보낸 쪽지를 삭제합니다.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/messages/sent/{id}")
    public Response<?> deleteSentMessage(@PathVariable("id") Integer id,
                                         Authentication authentication) {
        // 임의로 유저 정보를 넣었지만, JWT 도입하고 현재 로그인 된 유저의 정보를 넘겨줘야함

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        MessageDto messageDto = messageService.findMessageById(id);

        if(messageDto.getSenderName().equals(user.getName())) {
            return new Response<>("삭제 성공", "보낸 쪽지인, "+id+"번 쪽지를 삭제했습니다.",messageService.deleteMessageBySender(messageDto, user));
        } else {
            return new Response<>("삭제 실패", "사용자 정보가 다릅니다.", null);
        }
    }
}
