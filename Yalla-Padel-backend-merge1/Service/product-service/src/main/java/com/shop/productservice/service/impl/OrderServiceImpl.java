package com.shop.productservice.service.impl;

import com.shop.productservice.dto.OrderDTO;
import com.shop.productservice.entity.*;
import com.shop.productservice.repository.CartRepo;
import com.shop.productservice.repository.ProductOrderRepo;
import com.shop.productservice.repository.ProductRepo;
import com.shop.productservice.service.OrderService;
import com.shop.productservice.exception.InsufficientStockException;
import com.shop.productservice.exception.OrderNotFoundException;
import com.shop.productservice.exception.InvalidLocationException;
import com.shop.productservice.dto.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepo orderRepository;

    @Autowired
    private CartRepo cartRepository;

    @Autowired
    private ProductRepo productRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public void saveOrder(String userId, OrderDTO orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if (carts.isEmpty()) {
            throw new Exception("Cart is empty for user id: " + userId);
        }

        for (Cart cart : carts) {
            Product product = cart.getProduct();

            if (product.getStock() < cart.getQuantity()) {
                throw new InsufficientStockException(product.getId(), cart.getQuantity(), product.getStock());
            }
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);

            ProductOrder order = ProductOrder.builder()
                    .orderId(UUID.randomUUID().toString())
                    .orderDate(LocalDate.now())
                    .product(product)
                    .price(cart.getProduct().getDiscountPrice())
                    .quantity(cart.getQuantity())
                    .userId(userId)
                    .status(OrderStatus.IN_PROGRESS.getName())
                    .paymentType(orderRequest.getPaymentType())
                    .orderAddress(OrderAdress.builder()
                            .firstName(orderRequest.getFirstName())
                            .lastName(orderRequest.getLastName())
                            .email(orderRequest.getEmail() != null ? orderRequest.getEmail() : "no-email@example.com")
                            .telephone(orderRequest.getTelephone())
                            .address(orderRequest.getAddress())
                            .city(orderRequest.getCity())
                            .state(orderRequest.getState())
                            .postalCode(orderRequest.getPostalCode())
                            .build())
                    .build();

            orderRepository.save(order);
        }

        cartRepository.deleteByUserId(userId);
    }

    @Override
    public List<ProductOrder> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            return orderRepository.save(productOrder);
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public ProductOrder getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ProductOrder updateOrder(Integer id, ProductOrder orderData) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder existingOrder = findById.get();
            existingOrder.setQuantity(orderData.getQuantity());

            if (orderData.getStatus() != null) {
                existingOrder.setStatus(orderData.getStatus());
            }

            if (orderData.getOrderAddress() != null && existingOrder.getOrderAddress() != null) {
                OrderAdress existingAddr = existingOrder.getOrderAddress();
                OrderAdress newAddr = orderData.getOrderAddress();

                existingAddr.setFirstName(newAddr.getFirstName());
                existingAddr.setLastName(newAddr.getLastName());
                existingAddr.setEmail(newAddr.getEmail());
                existingAddr.setTelephone(newAddr.getTelephone());
                existingAddr.setAddress(newAddr.getAddress());
                existingAddr.setCity(newAddr.getCity());
                existingAddr.setState(newAddr.getState());
                existingAddr.setPostalCode(newAddr.getPostalCode());
            }

            return orderRepository.save(existingOrder);
        }
        return null;
    }

    @Override
    public BigDecimal getTotalMoneyForCurrentUser(String userId) {
        Double sum = orderRepository.sumTotalAmountByUserId(userId);
        return BigDecimal.valueOf(sum != null ? sum : 0.0).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTotalMoneyForAllUsers() {
        Double sum = orderRepository.sumTotalAmountAllUsers();
        return BigDecimal.valueOf(sum != null ? sum : 0.0).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public LocationDTO getLocationDTO(Integer orderId) {
        ProductOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getLatitude() == null || order.getLongitude() == null) {
            String searchQuery = "";
            if (order.getOrderAddress() != null) {
                String address = order.getOrderAddress().getAddress() != null ? order.getOrderAddress().getAddress() + " " : "";
                String city = order.getOrderAddress().getCity() != null ? order.getOrderAddress().getCity() : "";
                String state = order.getOrderAddress().getState() != null ? " " + order.getOrderAddress().getState() : "";
                searchQuery = address + city + state;
            }

            if (!searchQuery.trim().isEmpty()) {
                try {
                    String encodedQuery = URLEncoder.encode(searchQuery.trim(), StandardCharsets.UTF_8);
                    String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedQuery;

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", "ShopProductService/1.0");
                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

                    if (response.getBody() != null && !response.getBody().isEmpty()) {
                        Map<String, Object> firstResult = (Map<String, Object>) response.getBody().get(0);
                        order.setLatitude(Double.parseDouble((String) firstResult.get("lat")));
                        order.setLongitude(Double.parseDouble((String) firstResult.get("lon")));
                        order.setDeliveryAddress((String) firstResult.get("display_name"));
                        orderRepository.save(order);
                    }
                } catch (Exception e) {
                    System.err.println("Forward Geocoding failed: " + e.getMessage());
                }
            }
        }

        return LocationDTO.builder()
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .address(order.getDeliveryAddress())
                .build();
    }

    @Override
    @Transactional
    public void updateOrderLocation(Integer orderId, Double latitude, Double longitude) {
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new InvalidLocationException("Invalid latitude: " + latitude);
        }
        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new InvalidLocationException("Invalid longitude: " + longitude);
        }

        ProductOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setLatitude(latitude);
        order.setLongitude(longitude);

        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "ShopProductService/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("display_name")) {
                order.setDeliveryAddress((String) response.getBody().get("display_name"));
            } else {
                order.setDeliveryAddress("Unknown Address");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch address from Nominatim: " + e.getMessage());
            order.setDeliveryAddress("Address fetch failed");
        }

        orderRepository.save(order);
    }
}
