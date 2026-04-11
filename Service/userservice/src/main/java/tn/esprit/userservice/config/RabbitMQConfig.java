package tn.esprit.userservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REGISTRATION_QUEUE = "event.registration.queue";

    // Declare the queue so userservice can create it if eventmodule is down
    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, true); // true = durable
    }

    // JSON serialization <-> POJO
    // Required to automatically convert the incoming JSON message back to EventRegistrationMessage
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
