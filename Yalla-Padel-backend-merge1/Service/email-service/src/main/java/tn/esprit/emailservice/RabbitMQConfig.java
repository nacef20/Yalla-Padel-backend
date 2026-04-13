package tn.esprit.emailservice;




import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
@Configuration
public class RabbitMQConfig {

    public static final String JOIN_GROUP_QUEUE = "join.group.queue";
    public static final String TOURNOI_INSCRIPTION_QUEUE = "tournoi.inscription.queue";
    public static final String TOURNOI_GAGNANT_QUEUE = "tournoi.gagnant.queue";
    public static final String TOURNOI_EXCHANGE = "tournoi.exchange";
    public static final String TOURNOI_INSCRIPTION_ROUTING_KEY = "tournoi.inscription";
    public static final String TOURNOI_GAGNANT_ROUTING_KEY = "tournoi.gagnant";

    @Bean
    public Queue queue() {
        return new Queue(JOIN_GROUP_QUEUE, true);
    }

    @Bean
    public Queue tournoiInscriptionQueue() {
        return new Queue(TOURNOI_INSCRIPTION_QUEUE, true);
    }

    @Bean
    public Queue tournoiGagnantQueue() {
        return new Queue(TOURNOI_GAGNANT_QUEUE, true);
    }

    @Bean
    public org.springframework.amqp.core.DirectExchange tournoiExchange() {
        return new org.springframework.amqp.core.DirectExchange(TOURNOI_EXCHANGE, true, false);
    }

    @Bean
    public org.springframework.amqp.core.Binding tournoiInscriptionBinding(
            @Qualifier("tournoiInscriptionQueue") Queue tournoiInscriptionQueue,
            @Qualifier("tournoiExchange") org.springframework.amqp.core.DirectExchange tournoiExchange) {
        return org.springframework.amqp.core.BindingBuilder
                .bind(tournoiInscriptionQueue)
                .to(tournoiExchange)
                .with(TOURNOI_INSCRIPTION_ROUTING_KEY);
    }

        @Bean
        public org.springframework.amqp.core.Binding tournoiGagnantBinding(
            @Qualifier("tournoiGagnantQueue") Queue tournoiGagnantQueue,
            @Qualifier("tournoiExchange") org.springframework.amqp.core.DirectExchange tournoiExchange) {
        return org.springframework.amqp.core.BindingBuilder
            .bind(tournoiGagnantQueue)
            .to(tournoiExchange)
            .with(TOURNOI_GAGNANT_ROUTING_KEY);
        }
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());

        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }

}
