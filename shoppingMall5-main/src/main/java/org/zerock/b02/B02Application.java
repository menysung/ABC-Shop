package org.zerock.b02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //자동으로 등록, 수정 시간 업데이트
public class B02Application {

	public static void main(String[] args) {
		SpringApplication.run(B02Application.class, args);
	}

}
