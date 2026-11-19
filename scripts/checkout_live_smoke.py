"""One-time live smoke test. Creates one clearly labelled dummy order. No payment."""
import json
import os
import sys
import time
import urllib.error
import urllib.request

BASE = os.environ.get("CHECKOUT_BASE_URL", "https://platable-eats.vercel.app").rstrip("/")
RUN = os.environ.get("GITHUB_RUN_ID", "local")

def request(method, path, payload=None, *, retries=0):
    url = BASE + path
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    headers = {
        "Accept": "application/json",
        "User-Agent": "PlateablePortfolioCheckoutSmoke/1.0",
    }
    if payload is not None:
        headers["Content-Type"] = "application/json"
    for attempt in range(retries + 1):
        req = urllib.request.Request(url, data=data, headers=headers, method=method)
        try:
            with urllib.request.urlopen(req, timeout=170) as resp:
                return resp.status, resp.read().decode("utf-8", errors="replace")
        except urllib.error.HTTPError as e:
            body = e.read().decode("utf-8", errors="replace")
            if e.code in (502, 503, 504) and attempt < retries:
                print(f"Retrying {method} {path} after HTTP {e.code}", flush=True)
                time.sleep(4)
                continue
            return e.code, body

def expect(method, path, want_status, payload=None, *, retries=0):
    status, body = request(method, path, payload, retries=retries)
    print(f"{method} {path} -> {status} (expected {want_status})", flush=True)
    if status != want_status:
        raise AssertionError(f"{method} {path}: expected HTTP {want_status}, got {status}: {body[:350]}")
    return json.loads(body) if body and body.strip().startswith(("{", "[")) else body

print(f"Testing {BASE} with run {RUN}", flush=True)

health = expect("GET", "/actuator/health", 200, retries=1)
assert health.get("status") == "UP", health
menu = expect("GET", "/api/menu", 200, retries=1)
assert isinstance(menu, list) and len(menu) > 1, "Menu is empty"
available = sorted([item for item in menu if item.get("available") and item.get("price", 0) > 0], key=lambda x: x["id"])
assert len(available) >= 2, "Need at least two available menu items"

a, b = available[:2]
name = "Checkout Smoke Test"
payload = {
    "customerName": name,
    "contactNumber": "0000000000",
    "deliveryAddress": "TEST ONLY DO NOT DELIVER " + RUN,
    "specialInstructions": "Automated portfolio checkout test; no payment; delete after verification.",
    "items": [
        {"menuItemId": a["id"], "quantity": 2},
        {"menuItemId": b["id"], "quantity": 1}
    ]
}

expect("GET", "/api/orders", 401)
expect("GET", "/api/reservations", 401)
expect("POST", "/api/checkout", 400, {**payload, "items": []})
expect("POST", "/api/checkout", 400, {**payload, "items": [{"menuItemId": a["id"], "quantity": -1}]})
expect("POST", "/api/checkout", 404, {**payload, "items": [{"menuItemId": "__DOES_NOT_EXIST__", "quantity": 1}]})

# Only this request should persist a dummy order; do not retry POST on ambiguous timeouts.
receipt = expect("POST", "/api/checkout", 201, payload)
expected = round(float(a["price"]) * 2 + float(b["price"]), 2)
assert receipt["status"] == "PENDING", receipt
assert receipt["itemCount"] == 3, receipt
assert abs(float(receipt["total"]) - expected) < 0.001, (receipt, expected)
assert receipt["orderId"].startswith("ORD"), receipt
print("LIVE_CHECKOUT_PASS", json.dumps({
    "run": RUN,
    "orderId": receipt["orderId"],
    "total": receipt["total"],
    "itemCount": receipt["itemCount"],
    "lines": [a["id"], b["id"]],
    "name": name,
}), flush=True)
