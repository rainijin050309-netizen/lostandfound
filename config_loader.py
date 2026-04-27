import os
from functools import lru_cache
from urllib.parse import urlparse

import yaml


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DEFAULT_CONFIG_PATH = os.path.join(BASE_DIR, "src", "main", "resources", "application.yml")


def _safe_int(value, default):
    try:
        return int(value)
    except (TypeError, ValueError):
        return default


def _get_nested(mapping, keys, default=None):
    current = mapping
    for key in keys:
        if not isinstance(current, dict):
            return default
        current = current.get(key)
        if current is None:
            return default
    return current


@lru_cache(maxsize=1)
def _load_application_config():
    config_path = os.getenv("APP_CONFIG_PATH", DEFAULT_CONFIG_PATH)
    if not os.path.exists(config_path):
        return {}

    with open(config_path, "r", encoding="utf-8") as file:
        data = yaml.safe_load(file) or {}

    return data if isinstance(data, dict) else {}


def _parse_mysql_jdbc_url(jdbc_url):
    host = "127.0.0.1"
    port = 3306
    database = "lostandfound"

    if not jdbc_url:
        return host, port, database

    normalized = jdbc_url.strip()
    if normalized.startswith("jdbc:"):
        normalized = normalized[len("jdbc:") :]

    parsed = urlparse(normalized)
    if parsed.hostname:
        host = parsed.hostname
    if parsed.port:
        port = parsed.port
    if parsed.path and parsed.path != "/":
        database = parsed.path.lstrip("/")

    return host, port, database


def get_mysql_config():
    config = _load_application_config()
    datasource = _get_nested(config, ["spring", "datasource"], {})

    jdbc_url = datasource.get("url", "") if isinstance(datasource, dict) else ""
    host, port, database = _parse_mysql_jdbc_url(jdbc_url)

    username = datasource.get("username", "root") if isinstance(datasource, dict) else "root"
    password = datasource.get("password", "") if isinstance(datasource, dict) else ""

    return {
        "host": host,
        "port": port,
        "user": username,
        "password": password,
        "database": database,
    }


def get_llm_config():
    config = _load_application_config()
    llm_config = _get_nested(config, ["llm"], {})

    service_config = llm_config.get("service", {}) if isinstance(llm_config, dict) else {}
    openai_config = llm_config.get("openai", {}) if isinstance(llm_config, dict) else {}

    model = str(openai_config.get("model", "gpt-4o-mini")).strip() or "gpt-4o-mini"

    return {
        "port": _safe_int(service_config.get("port", 5000), 5000),
        "openai_api_key": str(openai_config.get("api-key", "")).strip(),
        "openai_model": model,
    }