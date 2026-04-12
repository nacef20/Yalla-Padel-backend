package tn.esprit.eventmodule.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "event.exchange";
    public static final String REGISTRATION_QUEUE = "event.registration.queue";
    public static final String REGISTRATION_ROUTING_KEY = "event.registration.routing.key";

    // Create the Queue
    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, true);
    }

    // Create the Exchange
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Bind the Queue to the Exchange with the routing key
    @Bean
    public Binding registrationBinding(Queue registrationQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(registrationQueue).to(eventExchange).with(REGISTRATION_ROUTING_KEY);
    }

    // JSON serialization <-> POJO
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate configured to use the JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
