package br.com.cesarcastro.apis.ccshortener.domain.mapper;


import br.com.cesarcastro.apis.ccshortener.domain.entities.ShortenedUrlEntity;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static br.com.cesarcastro.apis.ccshortener.util.QRCodeGenerator.generatePng;

@Mapper(componentModel = "spring")
public interface ShortenedMapper {

    ShortenedMapper INSTANCE = Mappers.getMapper(ShortenedMapper.class);

    @Mapping(source = "originalUrl", target = "targetUrl")
    ShortenedUrlEntity convertToEntity(ShortenedURLRequestDTO requestDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "targetUrl", target = "originalUrl")
    @Mapping(source = "code", target = "shortUrl")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(target = "qrCodeImage", source = "code", qualifiedByName = "qrcodeGenerator")
    ShortenedURLResponseDTO toResponseDTO(ShortenedUrlEntity entity);

    @Named("qrcodeGenerator")
    default byte[] generateQRCode(String shortUrl) {
        return generatePng(shortUrl);
    }
}
