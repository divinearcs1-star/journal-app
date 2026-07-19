package com.firstproject.journalApp;

import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JournalAppApplicationTests {

    @Autowired
    private UserRepository userRepository;

	@Test
	void add() {
        User byUserName = userRepository.findByUserName("vipul");
        assertTrue(!byUserName.getJournalentries().isEmpty());
    }

    @ParameterizedTest
    @ValueSource( strings = {
            "shyam",
            "vipul"
    })
    public void test(String name) {
        assertNotNull(userRepository.findByUserName(name));
    }


    @ParameterizedTest
    @CsvSource({
            "2,10,12",
            "3,3,5"
    })
    public void test(int a,int b, int exp) {
        assertEquals(exp, a + b);
    }
}
