package com.J2EE.BlogNMCVC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		// 1. Thông báo server + mở trình duyệt
		String url = "http://localhost:8080/";
		System.out.println("\n🚀 Server started!");
		System.out.println("👉 Open: " + url + "\n");

		// 2. Khu vực test utils
//		testUtils();
	}

//	private static void testUtils() {
//		System.out.println("===== TEST UTILS =====");
//
//		String input = "Nhà, mây, cá và cây";
//		String slug = SlugUtils.toSlug(input);
//
//		System.out.println("Input: " + input);
//		System.out.println("Slug : " + slug);
//		System.out.println("Unique slug: " + SlugUtils.toUniqueSlug(slug, LocalDateTime.now()));
//
//		System.out.println("======================\n");
//	}

}
