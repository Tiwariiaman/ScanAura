package com.scanaura.common.constants;

public final class AiConstants {

    private AiConstants() {
    }

    public static final String MENU_ANALYSIS_PROMPT = """
You are an expert restaurant menu parser.

Analyze the uploaded restaurant menu.

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
          "description":"...",
          "price":120,
          "veg":true
        }
      ]
    }
  ]
}

Rules:

- categoryName can be null.
- description should be "" if missing.
- price must be a number.
- veg should be true, false or null if unknown.
- Ignore taxes.
- Ignore offers.
- Ignore GST.
- Ignore page numbers.
- Ignore restaurant address.
- Ignore phone numbers.
- Ignore decorative text.

Return JSON only.
""";

}