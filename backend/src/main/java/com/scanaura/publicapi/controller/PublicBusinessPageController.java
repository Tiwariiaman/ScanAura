package com.scanaura.publicapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanaura.business.entity.Business;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.publicapi.dto.SharePreviewResponse;
import com.scanaura.publicapi.service.PublicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PublicBusinessPageController {

    private static final List<String> RESERVED_SUBDOMAINS = List.of(
            "www", "api", "admin", "app", "mail", "support", "help",
            "blog", "docs", "dashboard", "login", "register", "signup",
            "pricing", "about", "contact", "status", "cdn", "static",
            "business"
    );

    private final PublicService publicService;
    private final ObjectMapper objectMapper;

    @Value("${scanaura.public-domain:scanaura.in}")
    private String publicDomain;

    /**
     * Handles https://business-slug.scanaura.in/
     * The reverse proxy must preserve the original Host header.
     */
    @GetMapping("/_business-page")
    public RedirectView businessPageFromSubdomain(HttpServletRequest request) {
        String slug = extractBusinessSlug(request.getServerName());

        if (slug == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String qrCode = publicService.getDigitalQrCodeForPublicSlug(slug);
        return new RedirectView("https://" + publicDomain + "/q/" + qrCode, true);
    }

    /**
     * Supports https://business.scanaura.in/{business-slug} as a short alias
     * and redirects visitors to the existing Flutter QR route.
     */
    @GetMapping("/{slug}")
    public RedirectView businessPageFromPath(
            @PathVariable String slug,
            HttpServletRequest request
    ) {
        String normalized = slug == null ? "" : slug.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")
                || RESERVED_SUBDOMAINS.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (request.getServerName().equalsIgnoreCase("business." + publicDomain)) {
            String qrCode = publicService.getDigitalQrCodeForPublicSlug(normalized);
            return new RedirectView("https://" + publicDomain + "/q/" + qrCode, true);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        List<Business> businesses = publicService.getBusinessesForSitemap();
        StringBuilder xml = new StringBuilder(1024);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        for (Business business : businesses) {
            if (business.getPublicSlug() == null || business.getPublicSlug().isBlank()) {
                continue;
            }
            String url = canonicalUrl(business.getPublicSlug());
            xml.append("<url><loc>")
                    .append(xmlEscape(url))
                    .append("</loc>");
            if (business.getUpdatedAt() != null) {
                xml.append("<lastmod>")
                        .append(business.getUpdatedAt().toLocalDate())
                        .append("</lastmod>");
            }
            xml.append("</url>");
        }

        xml.append("</urlset>");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofMinutes(10)).cachePublic())
                .contentType(MediaType.APPLICATION_XML)
                .body(xml.toString());
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        String body = "User-agent: *\n"
                + "Allow: /\n"
                + "Disallow: /api/\n"
                + "Sitemap: https://business." + publicDomain + "/sitemap.xml\n";
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic())
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body(body);
    }



    private ModelAndView renderBusinessPage(String slug) {
        final SharePreviewResponse page;
        try {
            page = publicService.getPublicBusinessPage(slug);
        } catch (BusinessException exception) {
            if (exception.getMessage() != null
                    && exception.getMessage().toLowerCase(Locale.ROOT).contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            throw exception;
        }

        String canonical = canonicalUrl(page.getPublicSlug());
        String description = page.getDescription();
        if (description == null || description.isBlank()) {
            description = "Explore " + page.getBusinessName() + " on ScanAura.";
        }
        String metaDescription = description.replaceAll("\\s+", " ").trim();
        if (metaDescription.length() > 160) {
            metaDescription = metaDescription.substring(0, 157).replaceAll("\\s+$", "") + "...";
        }

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", schemaType(page.getBusinessType()));
        schema.put("name", page.getBusinessName());
        schema.put("url", canonical);
        schema.put("description", description);
        if (hasText(page.getLogoUrl())) {
            schema.put("image", page.getLogoUrl());
            schema.put("logo", page.getLogoUrl());
        }
        if (hasText(page.getPhone())) {
            schema.put("telephone", page.getPhone());
        }

        Map<String, Object> address = new LinkedHashMap<>();
        address.put("@type", "PostalAddress");
        putIfText(address, "streetAddress", page.getAddress());
        putIfText(address, "addressLocality", page.getCity());
        putIfText(address, "addressRegion", page.getState());
        putIfText(address, "postalCode", page.getPincode());
        putIfText(address, "addressCountry", page.getCountry());
        if (address.size() > 1) {
            schema.put("address", address);
        }
        List<String> sameAs = java.util.stream.Stream.of(
                        page.getWebsite(),
                        page.getInstagramUrl(),
                        page.getFacebookUrl(),
                        page.getYoutubeUrl()
                )
                .filter(PublicBusinessPageController::hasText)
                .toList();
        if (!sameAs.isEmpty()) {
            schema.put("sameAs", sameAs);
        }

        String schemaJson;
        try {
            schemaJson = objectMapper.writeValueAsString(schema)
                    .replace("<", "\\u003c")
                    .replace(">", "\\u003e")
                    .replace("&", "\\u0026");
        } catch (JsonProcessingException exception) {
            schemaJson = "{}";
        }

        ModelAndView view = new ModelAndView("public/business-page");
        view.addObject("page", page);
        view.addObject("businessName", page.getBusinessName());
        view.addObject("description", description);
        view.addObject("metaDescription", metaDescription);
        view.addObject("logoUrl", page.getLogoUrl());
        view.addObject("canonicalUrl", canonical);
        view.addObject("schemaJson", schemaJson);
        view.addObject("pageTitle", page.getBusinessName()
                + (hasText(page.getCity()) ? " | " + page.getCity() : "")
                + " | ScanAura");
        return view;
    }

    private String extractBusinessSlug(String hostName) {
        if (hostName == null) {
            return null;
        }
        String host = hostName.toLowerCase(Locale.ROOT).trim();
        String domain = publicDomain.toLowerCase(Locale.ROOT).trim();
        if (host.equals(domain) || !host.endsWith("." + domain)) {
            return null;
        }

        String prefix = host.substring(0, host.length() - domain.length() - 1);
        if (prefix.isBlank() || prefix.contains(".") || RESERVED_SUBDOMAINS.contains(prefix)) {
            return null;
        }
        if (!prefix.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            return null;
        }
        return prefix;
    }

    private String canonicalUrl(String slug) {
        return "https://" + slug + "." + publicDomain.toLowerCase(Locale.ROOT);
    }

    private static String schemaType(String businessType) {
        if (businessType == null) {
            return "LocalBusiness";
        }
        return switch (businessType.toUpperCase(Locale.ROOT)) {
            case "RESTAURANT" -> "Restaurant";
            case "HOTEL" -> "Hotel";
            case "RETAIL", "SHOP", "STORE" -> "Store";
            case "SALON", "BEAUTY" -> "BeautySalon";
            default -> "LocalBusiness";
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static void putIfText(Map<String, Object> target, String key, String value) {
        if (hasText(value)) {
            target.put(key, value);
        }
    }

    private static String xmlEscape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
