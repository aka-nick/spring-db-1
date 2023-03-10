package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);

            transfer(con, fromId, toId, money);

            con.commit();
        }
        catch(Exception e) {
            con.rollback();
            log.info("실패 발생", e);
            throw new IllegalStateException(e);
        }
        finally {
            releaseConnection(con);
        }

    }

    private void transfer(Connection con, String fromId, String toId, int money)
            throws SQLException {
        Member fromMember = memberRepository.select(con, fromId);
        Member toMember = memberRepository.select(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void releaseConnection(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            }
            catch(Exception e) {
                log.info("예외 발생", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외가 발생");
        }
    }
}
