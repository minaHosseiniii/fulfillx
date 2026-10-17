package com.fulfillx.delivery_service.service.impl;

import com.fulfillx.delivery_service.domain.model.Delivery;
import com.fulfillx.delivery_service.domain.model.DeliveryAddress;
import com.fulfillx.delivery_service.dto.CreateDeliveryRequest;
import com.fulfillx.delivery_service.dto.DeliveryResponse;
import com.fulfillx.delivery_service.exception.DeliveryNotFoundException;
import com.fulfillx.delivery_service.mapper.DeliveryMapper;
import com.fulfillx.delivery_service.repository.DeliveryRepository;
import com.fulfillx.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        DeliveryAddress address = new DeliveryAddress(
                request.getRecipientName(),
                request.getPhoneNumber(),
                request.getProvince(),
                request.getCity(),
                request.getAddressLine(),
                request.getPostalCode()
        );

        Delivery delivery = Delivery.create(
                request.getOrderId(),
                address
        );

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryResponse(savedDelivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = findDeliveryById(id);

        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order ID: " + orderId
                ));

        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse startPreparing(Long id) {
        Delivery delivery = findDeliveryById(id);

        delivery.startPreparing();

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    @Override
    public DeliveryResponse markReadyForPickup(Long id) {
        Delivery delivery = findDeliveryById(id);

        delivery.markReadyForPickup();

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    @Override
    public DeliveryResponse markInTransit(
            Long id,
            String trackingNumber) {

        Delivery delivery = findDeliveryById(id);

        delivery.markInTransit(trackingNumber);

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    @Override
    public DeliveryResponse markDelivered(Long id) {
        Delivery delivery = findDeliveryById(id);

        delivery.markDelivered();

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    @Override
    public DeliveryResponse markFailed(Long id, String reason) {
        Delivery delivery = findDeliveryById(id);

        delivery.markFailed(reason);

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    @Override
    public DeliveryResponse cancelDelivery(Long id) {
        Delivery delivery = findDeliveryById(id);

        delivery.cancel();

        return deliveryMapper.toDeliveryResponse(
                deliveryRepository.save(delivery)
        );
    }

    private Delivery findDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found with ID: " + id
                ));
    }
}
