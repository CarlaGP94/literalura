package com.aluracursos.literalura;

import com.aluracursos.literalura.homepage.HomePage;
import com.aluracursos.literalura.repository.IAuthorRepository;
import com.aluracursos.literalura.repository.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
	@Autowired
	private IBookRepository bookRepository;
	@Autowired
	private IAuthorRepository authorRepository;
	@Autowired
	private HomePage homePage;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		homePage.showMenu();
	}
}
