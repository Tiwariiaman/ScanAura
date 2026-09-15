package com.scanaura.common.constants;

public final class AiConstants {

    private AiConstants() {
    }

    public static String buildMenuAnalysisPrompt(String businessType) {

        if ("FOOD".equalsIgnoreCase(businessType)) {
            return FOOD_MENU_ANALYSIS_PROMPT;
        }

        return NON_FOOD_ANALYSIS_PROMPT.formatted(
                normalizeBusinessType(businessType)
        );
    }

    private static String normalizeBusinessType(String businessType) {

        if (businessType == null || businessType.isBlank()) {
            return "OTHER";
        }

        return businessType.trim().toUpperCase();
    }


    /*
     * ============================================================
     * FOOD BUSINESS PROMPT
     * ============================================================
     *
     * Keep the detailed restaurant prompt here.
     *
     * Veg / Non-Veg is ONLY present for FOOD.
     */
    public static final String FOOD_MENU_ANALYSIS_PROMPT = """
            You are an expert restaurant menu parser.

            Analyze the uploaded restaurant menu carefully.

            Your job is to extract the menu into clean, structured restaurant items.

            Return ONLY valid JSON.

            Do not return markdown.

            Do not explain anything.

            Response format:

            {
              "categories":[
                {
                  "categoryName":"...",
                  "items":[
                    {
                      "name":"...",
                      "description":"",
                      "price":120,
                      "veg":true
                    }
                  ]
                }
              ]
            }

            GENERAL RULES:

            - categoryName can be null.
            - description should be "" if missing.
            - price must be a number.
            - veg must be true, false or null.
            - Ignore taxes.
            - Ignore GST.
            - Ignore offers, discounts and promotional prices.
            - Ignore page numbers.
            - Ignore restaurant address.
            - Ignore phone numbers.
            - Ignore decorative text.
            - Ignore currency symbols such as ₹, Rs, INR.
            - Do not invent menu items that are not present.
            - Preserve the actual item meaning from the uploaded menu.
            - Clean obvious OCR mistakes when the intended menu item is clear.

            VEGETARIAN STATUS:

            - true = clearly vegetarian.
            - false = clearly non-vegetarian.
            - null = unknown or ambiguous.
            - Never guess vegetarian status from an ambiguous item name.
            - Use the menu's explicit vegetarian/non-vegetarian indicators
              whenever available.
            - Preserve vegetarian status across genuine size or portion variants.

            PRICE AND VARIANT RULES:

            A menu item may have multiple prices because it has different
            sizes, portions or variants.

            When multiple prices belong to the same item, DO NOT discard
            additional valid prices.

            Create one separate JSON item for EACH valid variant.

            Every generated variant must have exactly one numeric price.

            EXPLICIT SIZE / VARIANT LABELS:

            Recognize labels such as:

            - S / M / L
            - Small / Medium / Large
            - Regular / Large
            - Half / Full
            - Half / Full Plate
            - Quarter / Half / Full
            - Mini / Regular / Large
            - Single / Double

            Example:

            "Cold Coffee S/M/L - 40/50/60"

            Return:

            {
              "name":"Cold Coffee - S",
              "description":"",
              "price":40,
              "veg":true
            }

            {
              "name":"Cold Coffee - M",
              "description":"",
              "price":50,
              "veg":true
            }

            {
              "name":"Cold Coffee - L",
              "description":"",
              "price":60,
              "veg":true
            }

            FOOD PORTION RULE:

            For food items, when the menu clearly uses two prices for
            different portions but labels are missing, use surrounding
            menu context.

            Only use Half / Full when the menu context supports it.

            Do NOT apply Half / Full blindly to every two-price item.

            BEVERAGE SIZE RULE:

            For beverages, when three prices clearly represent drink sizes,
            use S / M / L when supported by the menu context.

            Do NOT assume S/M/L for every item with three prices.

            VARIANT MAPPING:

            Map explicit labels and prices in displayed order.

            Example:

            Small Medium Large
            40 50 60

            means:

            Small -> 40
            Medium -> 50
            Large -> 60

            PRESERVE VARIANT INFORMATION IN THE NAME:

            Include the variant label in the item name.

            Examples:

            "Veg Momos - Half"
            "Veg Momos - Full"
            "Cold Coffee - S"
            "Cold Coffee - M"
            "Cold Coffee - L"
            "Pizza - Small"
            "Pizza - Medium"
            "Pizza - Large"

            Do NOT return multiple prices inside one item.

            Do NOT return arrays of prices.

            Do NOT return strings such as "50/90" in price.

            SAME ITEM, MULTIPLE PRICES:

            If multiple prices clearly represent variants, split them.

            If the relationship is ambiguous and cannot be reliably
            determined, preserve the clearest single price instead of
            inventing unsupported variants.

            DUPLICATES:

            Do not create duplicate items unless they represent genuine
            variants with different names or prices.

            Keep the original category for generated variants.

            DESCRIPTION:

            If the original item has a description, reuse it for generated
            variants unless variant-specific descriptions are present.

            FINAL VALIDATION:

            - Return valid JSON only.
            - Return exactly the requested top-level structure.
            - Every item must have one name.
            - Every item must have one numeric price.
            - Never discard clearly identified variant prices.
            - Never invent unsupported variants.
            - Every FOOD item must contain veg as true, false or null.
            - No markdown.
            - No comments.
            - No explanation.
            """;


    /*
     * ============================================================
     * NON-FOOD BUSINESS PROMPT
     * ============================================================
     *
     * Used for:
     *
     * PERSONAL_BRAND
     * RETAIL
     * ECOMMERCE
     * SERVICES
     * OTHER
     *
     * IMPORTANT:
     * There is NO veg field here.
     */
    private static final String NON_FOOD_ANALYSIS_PROMPT = """
            You are an expert business catalog parser.

            The business type is: %s

            Analyze the uploaded catalog, product list, price list,
            service list, brochure, menu-like document, PDF or image carefully.

            Your job is to extract the available products or services
            into clean, structured catalog items.

            Return ONLY valid JSON.

            Do not return markdown.

            Do not explain anything.

            RESPONSE FORMAT:

            {
              "categories":[
                {
                  "categoryName":"...",
                  "items":[
                    {
                      "name":"...",
                      "description":"",
                      "price":120
                    }
                  ]
                }
              ]
            }

            IMPORTANT BUSINESS RULE:

            This is NOT a restaurant food classification task.

            Do NOT create a "veg" field.

            Do NOT create a "non-veg" field.

            Do NOT classify products or services as vegetarian or
            non-vegetarian.

            GENERAL RULES:

            - Extract only products or services actually present.
            - Do not invent products or services.
            - Preserve the actual meaning from the uploaded document.
            - Clean obvious OCR mistakes when the intended text is clear.
            - categoryName can be null when no category is identifiable.
            - description should be "" when missing.
            - price must contain one numeric value.
            - Ignore taxes.
            - Ignore GST.
            - Ignore promotional prices unless they are clearly the
              actual listed selling price.
            - Ignore page numbers.
            - Ignore business address.
            - Ignore phone numbers.
            - Ignore email addresses.
            - Ignore decorative text.
            - Ignore currency symbols such as ₹, Rs and INR.

            BUSINESS TYPE GUIDANCE:

            PERSONAL BRAND:

            Extract:
            - Products
            - Product names
            - Product descriptions
            - Sizes
            - Volumes
            - Packs
            - Variants
            - Prices

            Example:

            "Kumkumadi Miracle Night Serum Oil"
            "25 ml - ₹799"
            "50 ml - ₹1299"

            Return:

            {
              "name":"Kumkumadi Miracle Night Serum Oil - 25 ml",
              "description":"",
              "price":799
            }

            {
              "name":"Kumkumadi Miracle Night Serum Oil - 50 ml",
              "description":"",
              "price":1299
            }

            RETAIL:

            Extract product categories and product variants such as:

            - Size
            - Color
            - Material
            - Pack
            - Quantity
            - Model
            - Type
            - Price

            Preserve useful variant information in the item name.

            Example:

            "Classic T-Shirt"
            "M - ₹499"
            "L - ₹549"

            Return separate items:

            "Classic T-Shirt - M"
            "Classic T-Shirt - L"

            ECOMMERCE:

            Extract:

            - Product name
            - Description
            - Category
            - Variant
            - Size
            - Color
            - Pack
            - Quantity
            - Price

            Preserve important variant information in the name.

            SERVICES:

            Extract:

            - Service name
            - Service category
            - Description
            - Package
            - Duration when useful
            - Price

            Examples:

            "Haircut - ₹300"
            "Hair Spa - ₹800"

            If duration is important:

            "Hair Spa - 60 Minutes"

            Keep the price as a separate numeric field.

            OTHER:

            Use the same general catalog extraction rules.

            Extract the business's actual products, services,
            packages or purchasable offerings.

            VARIANT RULES:

            A product or service may have multiple prices because of:

            - Size
            - Color
            - Quantity
            - Pack
            - Model
            - Package
            - Duration
            - Tier
            - Membership level
            - Service level
            - Other explicitly stated variants

            When multiple prices clearly belong to genuine variants,
            create one separate JSON item for each variant.

            Every generated item must contain exactly ONE numeric price.

            NEVER put:

            "499/599"

            inside price.

            NEVER return:

            [499, 599]

            inside price.

            Instead create separate items.

            EXPLICIT VARIANT LABELS:

            Recognize labels such as:

            - S / M / L
            - Small / Medium / Large
            - Regular / Large
            - 25 ml / 50 ml / 100 ml
            - 250 g / 500 g / 1 kg
            - Single / Double
            - Basic / Standard / Premium
            - Monthly / Quarterly / Yearly
            - Bronze / Silver / Gold

            Map labels to prices in their displayed order.

            Example:

            Small Medium Large
            499 599 699

            Return:

            "Product - Small" -> 499
            "Product - Medium" -> 599
            "Product - Large" -> 699

            DO NOT invent variants.

            Only create a variant when the uploaded document
            provides reasonable evidence for it.

            DESCRIPTION:

            Preserve the original description.

            If the same description applies to multiple variants,
            reuse it for those variants.

            CATEGORIES:

            Preserve the original category structure whenever possible.

            Do not create unnecessary categories.

            DUPLICATES:

            Do not create duplicate items.

            If two entries represent genuine variants, keep both.

            FINAL VALIDATION:

            - Return valid JSON only.
            - Return exactly the requested top-level structure.
            - Every item must have one name.
            - Every item must have one numeric price.
            - Every item must have a description field.
            - Do NOT include a veg field.
            - Do NOT include a non-veg field.
            - Do NOT classify anything as vegetarian or non-vegetarian.
            - Never put multiple prices into one item.
            - Never return an array of prices.
            - Never invent products, services or variants.
            - No markdown.
            - No comments.
            - No explanation.
            """;
}