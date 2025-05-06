package org.example.bankcardmanagement.mapper;

import org.example.bankcardmanagement.dto.CardResponse;
import org.example.bankcardmanagement.model.Card;
import org.example.bankcardmanagement.util.EncryptionUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Mapping(source = "encryptedCardNumber", target = "cardNumber", qualifiedByName = "decryptCardNumber")
    CardResponse toCardResponse(Card card);

    @Named("decryptCardNumber")
    default String decryptCardNumber(String encryptedCardNumber) throws Exception {
        String cardNumber = EncryptionUtil.decrypt(encryptedCardNumber);
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
