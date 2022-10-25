package com.study.restapi.controller;


import com.study.restapi.config.PrincipalDetails;
import com.study.restapi.dto.BoardDto;
import com.study.restapi.entity.User;
import com.study.restapi.repository.UserRepository;
import com.study.restapi.response.Response;
import com.study.restapi.service.BoardService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.PrinterInfo;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;

    // 전체 게시글 조회
    @ApiOperation(value = "전체 게시글 보기", notes = "전체 게시글을 조회한다.")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/boards")
    public Response getBoards() {
        return new Response("성공", "전체 게시물 리턴", boardService.getBoards());
    }

    // 개별 게시글 조회
    @ApiOperation(value = "개별 게시글 보기", notes = "개별 게시글을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boards/{id}")
    public Response getBoard(@PathVariable("id") Integer id) {
        return new Response("성공", "개별 게시물 리턴", boardService.getBoard(id));
    }

    // 게시글 작성
    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성한다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/boards/write")
    public Response write(@RequestBody BoardDto boardDto, Authentication authentication) {
        // 원래 로그인을 하면, User 정보는 세션으 통해서 구하고 주면 되지만
        // 일단 JWT 로그인은 생략하고, 임의로 findById로 유저 정보를 넣어줌
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        return new Response("성공", "글 작성 성공", boardService.write(boardDto, user));
    }


    // 게시글 수정
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/boards/update/{id}")
    public Response edit(@RequestBody BoardDto boardDto,
                         @PathVariable("id") Integer id, Authentication authentication) {
//         원래 로그인을 하면, User 정보는 세션을 통해서 구하고 주면 되지만, 일단 생략
//         JWT 로그인을 배우고 나서 적용해야 할 것
//         1. 현제 요청을 보낸 유저의 JWT 토큰 정보(프론트엔드가 헤더를 통해 보내줌)를 바탕으로
//         현제 로그인한 유저의 정보가 PathVariable로 들어오는 BoardId의 작성자인 user정보와 일치하는지 확인 하고
//         맞으면 아래 로직 수행, 틀리면 다른 로직(ResponseFail 등 커스텀으로 만들어서) 수행
//         이건 if문으로 처리할 수 있음 * 이 방법 말고 service 내부에서 확인해도 상관없음

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        if (user.getName().equals(boardService.getBoard(id).getWriter())) {
            // 로그인된 유저의 글이 맞다면
            return new Response("성공", "글 수정 성공", boardService.update(id, boardDto));
        } else {
            return new Response("실패", "본인 게시글만 수정할 수 있습니다.", null);
        }
    }


    // 게시글 삭제
    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/board/delete/{id}")
    public Response delete(@PathVariable("id") Integer id, Authentication authentication) {
        // 이것도 나중에, JWT 적용 후 현제 이 요청을 보낸 로그인된 유저의 정보가
        // 게시글의 주인인지 확인하고, 맞으면 삭제 수행 후 리턴해주고, 틀리면 에러 리턴해주면 됨

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        if (user.getName().equals(boardService.getBoard(id).getWriter())) {
            // 로그인된 유저가 글 작성자와 같다면
            boardService.delete(id); // 이 메소드는 반환값이 없으므로 따로 삭제 수행해주고,
            // 리턴에는 null을 넣어줌
            return new Response("성공", "글 삭제 성공", null);
        } else {
            return new Response("실패", "본인 게시물만 삭제할 수 있습니다", null);
        }
    }
}
