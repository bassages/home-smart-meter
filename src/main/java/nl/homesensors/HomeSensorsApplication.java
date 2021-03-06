package nl.homesensors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.time.Clock;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HomeSensorsApplication {

	public static void main(final String[] args) {
		SpringApplication.run(HomeSensorsApplication.class, args);
	}

    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
	@Scope(value = SCOPE_PROTOTYPE)
	public HttpClientBuilder getHttpClientBuilder() {
		return HttpClientBuilder.create();
	}

	@Bean
	public Runtime getRuntime() {
		return Runtime.getRuntime();
	}
}
