package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareItTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main() {
		String[] strings = new String[1];
		strings[0] = "";
		ShareitServer.main(strings);
	}

}
