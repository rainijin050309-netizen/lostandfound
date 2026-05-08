import re

from config_loader import get_llm_config


def is_safe_select_sql(sql):
    if not sql:
        return False

    normalized = sql.strip().lower()

    if not normalized.startswith("select"):
        return False
    if ";" in normalized:
        return False

    blocked = re.search(
        r"\b(insert|update|delete|drop|alter|create|replace|truncate|grant|revoke|pragma|attach|detach)\b",
        normalized,
    )
    if blocked:
        return False

    if " from item" not in normalized and " from `item`" not in normalized:
        return False

    return True


def _rule_based_sql(user_query):
    q = user_query.lower()

    conditions = []

    if "lost" in q or "丢" in q:
        conditions.append("i.type = 'lost'")
    if "found" in q or "捡" in q:
        conditions.append("i.type = 'found'")

    category_map = {
        "电子产品": ["electronic", "electronics", "laptop", "phone", "airpods", "charger", "calculator", "电子"],
        "书籍": ["book", "textbook", "notebook", "书"],
        "配件": ["umbrella", "bag", "wallet", "watch", "背包", "雨伞"],
        "证件": ["id", "card", "document", "证件", "学生证"],
    }
    for category, keywords in category_map.items():
        if any(keyword in q for keyword in keywords):
            conditions.append(f"LOWER(i.category) LIKE '%{category}%'")
            break

    locations = [
        "library",
        "cafeteria",
        "dorm",
        "gym",
        "gate",
        "bus",
        "lab",
        "hall",
        "图书馆",
        "饭堂",
        "操场",
        "教学楼",
    ]
    for location in locations:
        if location in q:
            conditions.append(f"LOWER(i.location) LIKE '%{location.lower()}%'")
            break

    base_sql = (
        "SELECT i.id AS item_id, i.title AS name, i.category, i.description, '' AS color, "
        "i.location, COALESCE(i.lostDate, i.foundDate) AS date, i.type AS status FROM item i"
    )
    if conditions:
        base_sql += " WHERE " + " AND ".join(conditions)
    base_sql += " ORDER BY COALESCE(i.lostDate, i.foundDate) DESC LIMIT 30"
    return base_sql


def _strip_code_block(text):
    cleaned = text.strip()
    cleaned = cleaned.replace("```sql", "").replace("```", "").strip()
    return cleaned


def generate_sql_from_nl(user_query):
    llm_config = get_llm_config()
    api_key = llm_config["openai_api_key"]
    if not api_key:
        return _rule_based_sql(user_query)

    try:
        from openai import OpenAI

        client = OpenAI(
            api_key=api_key,
            base_url="https://api.openai-proxy.org/v1"
        )
        system_prompt = (
            "You are an SQL generator. Convert user query into SQL for the item table. "
            "Only output one safe SELECT statement without semicolon. "
            "Never use INSERT, UPDATE, DELETE, DROP, ALTER, CREATE, PRAGMA."
        )

        user_prompt = (
            "Table: item(id, userId, type, title, description, category, location, lostDate, foundDate, status).\n"
            "Use alias output columns: item_id, name, category, description, color, location, date, status.\n"
            f"User query: {user_query}\n"
            "Return SQL only."
        )

        response = client.responses.create(
            model=llm_config["openai_model"],
            input=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            temperature=0,
        )

        sql = _strip_code_block(response.output_text or "")
        if is_safe_select_sql(sql):
            return sql

        return _rule_based_sql(user_query)
    except Exception:
        return _rule_based_sql(user_query)
