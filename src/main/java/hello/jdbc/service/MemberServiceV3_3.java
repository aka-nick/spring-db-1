package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

// 트랜잭션 템플릿 -> @Transactional AOP
@Slf4j
public class MemberServiceV3_3 {

//    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
//        txTemplate.executeWithoutResult((status) -> {
//            try {
//                transfer(fromId, toId, money);
//            } catch (SQLException e) {
//                throw new IllegalStateException(e);
//            }
//        });

        transfer(fromId, toId, money);
    }

    private void transfer(String fromId, String toId, int money)
            throws SQLException {
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
