package com.example.area_backend;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(
    info=@Info(
        contact=@Contact(
            name="pAREAge",
            email="nattan.cochet@epitech.eu, kenzo.escuret@epitech.eu, joan.ruiz@epitech.eu, vincent.montero-fontaine@epitech.eu, tom.calogheris@epitech.eu"
        ),
        description="Automation Platform",
        title="Area",
        version="1.0"
    ),
    servers={
        @Server(
            description="Local Environment",
            url="http://localhost:8080"
        ),
        @Server(
            description="Production Environment",
            url="http://localhost:8080" //a changer avec le deployement du back ${api.server.url}
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth"),
        @SecurityRequirement(name = "userIdAuth")
    }
)
@SecuritySchemes(
    value={
        @SecurityScheme(
            name="bearerAuth",
            description="JWT Access Token (Format: Bearer <token>)",
            scheme="bearer",
            type=SecuritySchemeType.HTTP,
            bearerFormat="JWT",
            in=SecuritySchemeIn.HEADER
        ),
        @SecurityScheme(
            name="userIdAuth",
            description="User Identification Header",
            type=SecuritySchemeType.APIKEY,
            in=SecuritySchemeIn.HEADER,
            paramName = "UserID"
        )
    }
)
public class OpenApiConfig{}