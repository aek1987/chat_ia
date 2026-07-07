package com.saas.ai.service;

import com.saas.ai.dto.Dtos.*;
import com.saas.ai.repository.OrderRepository;
import com.saas.ai.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * Point d'entrée principal du chat IA.
     * Analyse l'intention et retourne une réponse contextuelle.
     */
    public ChatResponse processMessage(ChatRequest request) {
        String message = request.getMessage().toLowerCase().trim();
        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        log.debug("Message reçu - customer: {}, message: {}", request.getCustomerId(), message);

        String intent = detectIntent(message);
        String reply = generateReply(intent, message, request.getCustomerId());

        return ChatResponse.builder()
                .reply(reply)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .intent(intent)
                .build();
    }

    /**
     * Détection de l'intention utilisateur (NLU simplifié).
     */
    private String detectIntent(String message) {
        if (containsAny(message, "commande", "order", "livraison", "où est", "statut", "suivi", "tracking")) {
            return "ORDER_STATUS";
        }
        if (containsAny(message, "annuler", "cancel", "rembours", "retour")) {
            return "CANCEL_ORDER";
        }
        if (containsAny(message, "produit", "article", "stock", "disponible", "prix", "catalogue")) {
            return "PRODUCT_INQUIRY";
        }
        if (containsAny(message, "bonjour", "salut", "hello", "bonsoir", "aide", "help")) {
            return "GREETING";
        }
        if (containsAny(message, "merci", "thank", "super", "parfait", "excellent")) {
            return "THANKS";
        }
        return "UNKNOWN";
    }

    /**
     * Génération de la réponse selon l'intention.
     */
    private String generateReply(String intent, String message, Long customerId) {
        return switch (intent) {
            case "ORDER_STATUS" -> handleOrderStatus(message, customerId);
            case "CANCEL_ORDER" -> "Pour annuler votre commande, veuillez nous fournir votre numéro de commande. Notre équipe traitera votre demande dans les plus brefs délais.";
            case "PRODUCT_INQUIRY" -> handleProductInquiry(message);
            case "GREETING" -> "Bonjour ! Je suis votre assistant IA. Je peux vous aider avec vos commandes, la livraison, ou le catalogue produits. Que puis-je faire pour vous ?";
            case "THANKS" -> "Avec plaisir ! N'hésitez pas si vous avez d'autres questions. Bonne journée !";
            default -> "Je n'ai pas bien compris votre demande. Pouvez-vous préciser ? Je peux vous aider avec : vos commandes, la livraison, ou notre catalogue.";
        };
    }

    private String handleOrderStatus(String message, Long customerId) {
        // Extraction du numéro de commande depuis le message
        Long orderId = extractOrderId(message);

        if (orderId != null) {
            DeliveryResponse delivery = orderService.getDeliveryStatus(orderId);
            return delivery.getMessage() +
                   (delivery.getEstimatedDelivery() != null
                       ? " (Livraison estimée : " + delivery.getEstimatedDelivery().toLocalDate() + ")"
                       : "");
        }

        // Si pas de numéro, chercher les commandes du client
        if (customerId != null) {
            List<Order> orders = orderRepository.findByCustomerId(customerId);
            if (!orders.isEmpty()) {
                Order last = orders.get(orders.size() - 1);
                return "Votre dernière commande #" + last.getId() + " (" + last.getProductName() + ") est actuellement : "
                        + formatStatus(last.getStatus()) + ". Pour plus de détails, précisez votre numéro de commande.";
            }
        }

        return "Veuillez indiquer votre numéro de commande (ex: 'commande 123') pour que je puisse vérifier son statut.";
    }

    private String handleProductInquiry(String message) {
        return "Notre catalogue est disponible sur notre site. Précisez le produit qui vous intéresse et je vérifierai sa disponibilité et son prix.";
    }

    private Long extractOrderId(String message) {
        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String formatStatus(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "En attente";
            case CONFIRMED -> "Confirmée";
            case PROCESSING -> "En préparation";
            case SHIPPED -> "En cours de livraison";
            case DELIVERED -> "Livrée";
            case CANCELLED -> "Annulée";
        };
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
