package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
public class MemberServiceV4 {

//    private final TransactionTemplate txTemplate;
    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) /* throws SQLException */ {
//        txTemplate.executeWithoutResult((status) -> {
//            try {
//                transfer(fromId, toId, money);
//            } catch (SQLException e) {
//                throw new IllegalStateException(e);
//            }
//        });

        transfer(fromId, toId, money);
    }

    private void transfer(String fromId, String toId, int money) /* throws SQLException */ {
        Member fromMember = memberRepository.select(fromId);
        Member toMember = memberRepository.select(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외가 발생");
        }
    }
}
