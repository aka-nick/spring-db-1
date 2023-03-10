package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

// 트랜잭션 매니저 -> 트랜잭션 템플릿
@Slf4j
public class MemberServiceV3_2 {

    //    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) {
//        TransactionStatus status =
//                transactionManager.getTransaction(new DefaultTransactionDefinition());
//
//        try {
//            transfer(fromId, toId, money);
//            transactionManager.commit(status);
//        }
//        catch(Exception e) {
//            transactionManager.rollback(status);
//
//        }
        txTemplate.executeWithoutResult((status) -> {
            try {
                transfer(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

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
