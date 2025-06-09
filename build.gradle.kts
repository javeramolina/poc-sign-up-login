plugins {
	java
	id("org.springframework.boot") version "2.5.14"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("jacoco")
}

group = "com.jvera.goballogic"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(11))
	}
}
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.springframework.boot:spring-boot-starter-security")
	implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.5")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation ("org.springdoc:springdoc-openapi-ui:1.7.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-core")

}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		html.required.set(true)
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"**/JwtService.class",
					"**/SecurityConfig.class",
					"**/dto/**",
					"**/exception/**",
					"**/filter/**",
					"**/model/**",
					"**/repository/**",
					"**/config/**",
					"**/controller/**",
					"com/jotavera/demo/auth/LoginSignupAppApplication.class",
					"com/jotavera/demo/auth/service/JwtService.class",
					"com/jotavera/demo/auth/service/UserService.class"
				)
			}
		})
	)

}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.test)

	violationRules {
		rule {
			limit {
				minimum = "0.80".toBigDecimal()
			}
		}
	}

	classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
	executionData.setFrom(tasks.jacocoTestReport.get().executionData)
}

tasks.check {
	dependsOn(tasks.named("jacocoTestCoverageVerification"))
}