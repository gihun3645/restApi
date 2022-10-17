package com.study.restapi.dto;

import com.study.restapi.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private int id;
    private String title;
    private String content;
    private String writer;

    // toDto 메소드를 만드는 이유는 boardDto.setTitle(board.getTitle);
    // 이런식으로 Dto의 값을 하나하나 설정해주면 귀찮으니
    // toDto 메소드를 만들어서, Board 객체만 넣으면 바로 BoardDto 로 만들어주게끔 했다.
    public static BoardDto toDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUser().getName());
    }
}
