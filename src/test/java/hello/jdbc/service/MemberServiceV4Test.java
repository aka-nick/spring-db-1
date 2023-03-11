package hello.jdbc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.MemberRepositoryV4_2;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 의존
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    public static final String Member_A = "memberA";
    public static final String Member_B = "memberB";
    public static final String Member_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberService;

    @TestConfiguration
    static class TestConfig {
//        @Bean
//        DataSource dataSource() {
//            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        }
//
//        @Bean
//        PlatformTransactionManager transactionManager() {
//            return new DataSourceTransactionManager(dataSource());
//        }

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepositoryV4_2 memberRepository() {
            return new MemberRepositoryV4_2(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }
    }

//    @BeforeEach
//    void beforeEach() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        memberRepository = new MemberRepositoryV3(dataSource);
////        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
//        memberService = new MemberServiceV3_3(memberRepository);
//    }

    @AfterEach
    void afterEach() {
        memberRepository.delete(Member_A);
        memberRepository.delete(Member_B);
        memberRepository.delete(Member_EX);
    }

    @Test
    void aopCheck() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransferTest() {
        Member memberA = new Member(Member_A, 10000);
        Member memberB = new Member(Member_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        log.info("===== START TX =====");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("=====  END TX  =====");

        Member findA = memberRepository.select(Member_A);
        Member findB = memberRepository.select(Member_B);

        assertThat(findA.getMoney()).isEqualTo(8000);
        assertThat(findB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferErrorTest() {
        Member memberA = new Member(Member_A, 10000);
        Member memberEx = new Member(Member_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        assertThatThrownBy(() -> memberService.accountTransfer(
                memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        Member findA = memberRepository.select(Member_A);
        Member findEx = memberRepository.select(Member_EX);


        assertThat(findA.getMoney()).isEqualTo(10000);
        assertThat(findEx.getMoney()).isEqualTo(10000);
    }
}