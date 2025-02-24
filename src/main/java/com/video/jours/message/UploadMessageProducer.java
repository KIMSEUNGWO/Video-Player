package com.video.jours.message;

import com.video.jours.dto.serializable.ConvertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(ConvertRequest request) {
        rabbitTemplate.convertAndSend("video-queue", request);
        System.out.println("Message Sent: " + request);
    }

}
