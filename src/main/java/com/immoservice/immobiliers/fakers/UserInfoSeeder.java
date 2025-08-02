// package com.immoservice.immobiliers.fakers;

// import com.github.javafaker.Faker;
// import com.immoservice.immobiliers.entities.UserInfo;
// import com.immoservice.immobiliers.repository.UserInfoRepository;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import java.util.Date;
// import java.util.Random;
// import java.util.Calendar;

// @Component
// public class UserInfoSeeder implements CommandLineRunner {

// private final UserInfoRepository userInfoRepository;
// private final Faker faker = new Faker();
// private final Random random = new Random();

// public UserInfoSeeder(UserInfoRepository userInfoRepository) {
// this.userInfoRepository = userInfoRepository;
// }

// @Override
// public void run(String... args) throws Exception {
// if (userInfoRepository.count() > 0) {
// System.out.println("ℹ️ Les utilisateurs existent déjà, seed ignoré.");
// return;
// }

// for (int i = 0; i < 10; i++) {
// final UserInfo user = new UserInfo();
// user.setName(faker.name().firstName());
// user.setSecondName(faker.name().lastName());
// user.setEmail(faker.internet().emailAddress());
// user.setAdresse(faker.address().fullAddress());
// user.setNumero(faker.number().numberBetween(60000000, 99999999));
// user.setDateNaissance(generateRandomBirthDate());
// user.setPassword("password" + i); // ou utilisez un hash

// userInfoRepository.save(user);
// }

// System.out.println("✅ 10 utilisateurs fictifs insérés avec succès !");
// }

// private Date generateRandomBirthDate() {
// Calendar calendar = Calendar.getInstance();
// int year = faker.number().numberBetween(1970, 2003);
// int month = faker.number().numberBetween(0, 11);
// int day = faker.number().numberBetween(1, 28); // éviter invalid date
// calendar.set(year, month, day);
// return calendar.getTime();
// }
// }
