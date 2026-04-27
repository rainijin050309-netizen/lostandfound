import pymysql
from flask import Flask, jsonify, request
from flask_cors import CORS
from pymysql.cursors import DictCursor

from config_loader import get_llm_config, get_mysql_config
from llm_service import generate_sql_from_nl, is_safe_select_sql


def get_connection():
    mysql_config = get_mysql_config()
    return pymysql.connect(
        host=mysql_config["host"],
        port=mysql_config["port"],
        user=mysql_config["user"],
        password=mysql_config["password"],
        database=mysql_config["database"],
        charset="utf8mb4",
        cursorclass=DictCursor,
    )


def _row_to_dict(row):
    return dict(row)


app = Flask(__name__)
CORS(app)


@app.route("/health", methods=["GET"])
def health_check():
    return jsonify({"status": "ok"})


@app.route("/llm-query", methods=["POST"])
def llm_query():
    data = request.get_json(silent=True) or {}
    natural_language = data.get("query", "").strip()

    if not natural_language:
        return jsonify({"error": "query is required"}), 400

    generated_sql = generate_sql_from_nl(natural_language)
    if not is_safe_select_sql(generated_sql):
        return jsonify({"error": "unsafe SQL generated", "sql": generated_sql}), 400

    with get_connection() as conn:
        with conn.cursor() as cur:
            cur.execute(generated_sql)
            rows = cur.fetchall()

    return jsonify(
        {
            "sql": generated_sql,
            "count": len(rows),
            "results": [_row_to_dict(row) for row in rows],
        }
    )


if __name__ == "__main__":
    llm_config = get_llm_config()
    port = llm_config["port"]
    app.run(host="0.0.0.0", port=port, debug=False)
