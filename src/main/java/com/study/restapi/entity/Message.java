package com.study.restapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean deletedBySender;

    @Column(nullable = false)
    private boolean deletedByReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    // 작성자 혹은 수신자가 계정을 삭제하면, 같이 지우기 위해서입니다.
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User receiver;

    // deleteBySender, deleteByReceiver, isDeleted 메소드는 편지 삭제와 관련된 메소드입니다.
    // 설명을 해드리자면 deleteBySender&Receiver 메소드는
    //수신자 혹은 발신자가 메시지를 삭제하면, deleteByxxx 필드의 값을 true로 바꿔줍니다.
    // 그리고 isDeleted 메소드는 deleteBySender&Receiver 의 값이
    //둘다 true이면 true를 반환해줍니다.
    // 즉 이 메소드를 호출했을 때 값이 true라면, 메시지를 DB에서 삭제하면 됩니다.
    //이 메소드들은 전부 서비스단에서 이용하게 됩니다.
    public void deleteBySender(){
        this.deletedBySender = true;
    }

    public void deleteByReceiver() {
        this.deletedByReceiver = true;
    }

    public boolean isDeleted() {
        return isDeletedBySender() && isDeletedByReceiver();
    }
}
