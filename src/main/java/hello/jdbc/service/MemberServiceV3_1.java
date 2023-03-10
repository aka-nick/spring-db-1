package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    //    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        TransactionStatus status =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            transfer(fromId, toId, money);
            transactionManager.commit(status);
        }
        catch(Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }

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
