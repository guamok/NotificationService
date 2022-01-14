package es.fermax.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
@EnableRabbit
public class RabbitConfig implements RabbitListenerConfigurer {
	
	private static final Boolean IS_DURABLE_QUEUE = true;
	
	public static final String QUEUE_APPTOKEN = "apptoken-notification-queue";
	public static final String QUEUE_ACK_NOTIFICATION = "ack-notification-queue";


	@Value("${rabbitmq.exchange_name.apptoken}")
	public String apptokenExchange;

	@Value("${rabbitmq.exchange_name.ack-notification}")
	public String ackNotificationExchange;
	
	@Value("${rabbitmq.exchange_name.add-invitee}")
	public String addInviteeExchange;

	@Bean
	Queue queueAppToken() {
		return new Queue(QUEUE_APPTOKEN, IS_DURABLE_QUEUE);
	}
	
	@Bean
	Queue queueSendNotification() {
		return new Queue(QUEUE_ACK_NOTIFICATION, IS_DURABLE_QUEUE);
	}


	@Bean
	FanoutExchange exchangeAppToken() {
		return new FanoutExchange(apptokenExchange);
	}
	
	@Bean
	FanoutExchange exchangeAckNotification() {
       return new FanoutExchange(ackNotificationExchange);
    }

	@Bean
	FanoutExchange exchangeAddInvitee() {
		return new FanoutExchange(addInviteeExchange);
	}

	@Bean
    Binding bindingAppToken() {
		return BindingBuilder.bind(queueAppToken()).to(exchangeAppToken());
    }
	
	@Bean
    Binding bindingSendNotification() {
        return BindingBuilder.bind(queueSendNotification()).to(exchangeAckNotification());
    }

	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
	   return new MappingJackson2MessageConverter();
	}

	@Bean
	public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
	   DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
	   factory.setMessageConverter(consumerJackson2MessageConverter());
	   return factory;
	}
	 
	@Override
	public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
	   registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
		
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
